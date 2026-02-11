package no.helseid.grants;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import no.helseid.cache.InMemoryExpiringCache;
import no.helseid.configuration.Client;
import no.helseid.dpop.DefaultDPoPProofCreator;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.metadata.RemoteMetadataProvider;
import no.helseid.signing.JWKKeyReference;
import no.helseid.signing.KeyReference;
import no.helseid.testutil.WireMockUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.*;
import java.util.concurrent.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultClientCredentialsTest {
  private static final String DPOP_NONCE = "eyJ7S_zG.eyJH0-Z.HX4w-7v";
  private static final String MOCK_ACCESS_TOKEN = "header.payload.signature";
  private static final JWK JWK;
  private static final KeyReference KEY_REFERENCE;
  private static final Set<String> SCOPE = Collections.singleton("nhn:helseid/test");

  static {
    try {
      JWK = new RSAKeyGenerator(2048)
          .algorithm(JWSAlgorithm.PS256)
          .keyID(UUID.randomUUID().toString())
          .keyUse(KeyUse.SIGNATURE)
          .generate();
      KEY_REFERENCE = JWKKeyReference.parse(JWK.toJSONString());
    } catch (JOSEException | HelseIdException e) {
      throw new RuntimeException(e);
    }
  }

  private WireMockServer wms;

  @BeforeEach
  void setup() {
    wms = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wms.start();
  }

  @Test
  void builder_should_fail_if_no_client_is_provided() {
    try {
      new DefaultClientCredentials.Builder(URI.create(wms.baseUrl())).build();
      fail();
    } catch (HelseIdException e) {
      assertEquals("No client is provided", e.getMessage());
    }
  }

  @Test
  void ClientCredentials_should_create_token() throws HelseIdException {
    // Providing metadata for the test
    WireMockUtils.stub_metadata_with_base_url(wms);

    // Expected failure with a DPoP proof without nonce
    WireMockUtils.stub_token_with_use_dpop_nonce_response(wms, DPOP_NONCE);

    // Expected result with a DPoP proof containing expected nonce
    WireMockUtils.stub_token_matching_dpop_nonce_returning_mock_access_token(wms, DPOP_NONCE, MOCK_ACCESS_TOKEN, SCOPE);

    Client client = new Client("client-id", KEY_REFERENCE, SCOPE);
    ClientCredentials clientCredentials = new ClientCredentials.Builder(URI.create(wms.baseUrl()))
        .withClient(client)
        .setCustomDPoPProofCreator(new DefaultDPoPProofCreator(KEY_REFERENCE))
        .setCustomTokenCache(new InMemoryExpiringCache<>())
        .build();
    TokenRequestDetails tokenRequestDetails = new TokenRequestDetails.Builder()
        .withChildOrganizationNumber("994598759")
        .build();
    TokenResponse tokenResponse = clientCredentials.getAccessToken(tokenRequestDetails);

    wms.verify(1, getRequestedFor(urlEqualTo("/.well-known/openid-configuration")));

    if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
      // Only two requests to the token endpoint should be registered, one returning DPoP nonce and one successful with a token
      wms.verify(2, postRequestedFor(urlEqualTo("/connect/token")));

      assertEquals(MOCK_ACCESS_TOKEN, accessTokenResponse.accessToken());
    } else if (tokenResponse instanceof ErrorResponse errorResponse) {
      fail(errorResponse.rawResponseBody());
    }
  }


  @Test
  void ClientCredentials_should_cache_token_for_a_given_assertion_detail() throws HelseIdException {
    // Providing metadata for the test
    WireMockUtils.stub_metadata_with_base_url(wms);

    // Expected failure with a DPoP proof without nonce
    WireMockUtils.stub_token_with_use_dpop_nonce_response(wms, DPOP_NONCE);

    // Expected result with a DPoP proof containing expected nonce
    WireMockUtils.stub_token_matching_dpop_nonce_returning_mock_access_token(wms, DPOP_NONCE, MOCK_ACCESS_TOKEN, SCOPE);

    Client client = new Client("client-id", KEY_REFERENCE, SCOPE);
    ClientCredentials clientCredentials = new ClientCredentials.Builder(URI.create(wms.baseUrl())).withClient(client).build();

    TokenRequestDetails tokenRequestDetails = new TokenRequestDetails.Builder()
        .withChildOrganizationNumber("994598759")
        .build();
    TokenResponse tokenResponseFirst = clientCredentials.getAccessToken(tokenRequestDetails);
    TokenResponse tokenResponseSecond = clientCredentials.getAccessToken(tokenRequestDetails);

    wms.verify(1, getRequestedFor(urlEqualTo("/.well-known/openid-configuration")));

    if (tokenResponseFirst instanceof AccessTokenResponse accessTokenResponseFirst) {
      // Only two requests to the token endpoint should be registered, one returning DPoP nonce and one successful with a token
      wms.verify(2, postRequestedFor(urlEqualTo("/connect/token")));

      assertEquals(MOCK_ACCESS_TOKEN, accessTokenResponseFirst.accessToken());

      if (tokenResponseSecond instanceof AccessTokenResponse accessTokenResponseSecond) {
        assertEquals(MOCK_ACCESS_TOKEN, accessTokenResponseSecond.accessToken());
      } else if (tokenResponseSecond instanceof ErrorResponse errorResponse) {
        fail(errorResponse.rawResponseBody());
      }
    } else if (tokenResponseFirst instanceof ErrorResponse errorResponse) {
      fail(errorResponse.rawResponseBody());
    }
  }

  @Test
  public void should_forward_error_response() throws HelseIdException {
    // Providing metadata for the test
    WireMockUtils.stub_metadata_with_base_url(wms);

    // Expected result with a DPoP proof containing expected nonce
    WireMockUtils.stub_token_with_status_code(wms, 500);

    Client client = new Client("client-id", KEY_REFERENCE, SCOPE);
    ClientCredentials clientCredentials = new ClientCredentials.Builder(URI.create(wms.baseUrl())).withClient(client).build();

    TokenResponse tokenResponse = clientCredentials.getAccessToken();

    wms.verify(1, getRequestedFor(urlEqualTo("/.well-known/openid-configuration")));
    wms.verify(1, postRequestedFor(urlEqualTo("/connect/token")));
    assertInstanceOf(ErrorResponse.class, tokenResponse);
    ErrorResponse errorResponse = (ErrorResponse) tokenResponse;
    assertEquals(500, errorResponse.statusCode());
  }

  @Test
  void ClientCredentials_should_handle_simultaneous_requests() throws HelseIdException, InterruptedException, ExecutionException, TimeoutException {
    ExecutorService executor = Executors.newFixedThreadPool(2);

    // Providing metadata for the test
    WireMockUtils.stub_metadata_with_base_url(wms);

    // Expected failure with a DPoP proof without nonce
    WireMockUtils.stub_token_with_use_dpop_nonce_response(wms, DPOP_NONCE);

    // Expected result with a DPoP proof containing expected nonce
    WireMockUtils.stub_token_matching_dpop_nonce_returning_mock_access_token(wms, DPOP_NONCE, MOCK_ACCESS_TOKEN, SCOPE);

    URI authority = URI.create(wms.baseUrl());
    Client client = new Client("client-id", KEY_REFERENCE, SCOPE);
    ClientCredentials clientCredentials = new ClientCredentials.Builder(authority).withClient(client).build();

    // Initalizing the metadata, ensuring it is called only once
    var metadata = RemoteMetadataProvider.getInstance(authority);
    metadata.getMetadata();

    List<String> orgNrList = Arrays.asList(
        "994598700",
        "994598701",
        "994598702",
        "994598703",
        "994598704"
    );

    List<Future<TokenResponse>> futures = executor.invokeAll(
        orgNrList.stream()
            .map(orgNr -> (Callable<TokenResponse>) () -> {
              try {
                var tokenRequestDetails = new TokenRequestDetails.Builder().withChildOrganizationNumber(orgNr).build();
                return clientCredentials.getAccessToken(tokenRequestDetails);
              } catch (HelseIdException e) {
                throw new RuntimeException(e);
              }
            }).toList(),
        10, TimeUnit.SECONDS);
    executor.shutdown();


    for (int i = 0; i < orgNrList.size(); i++) {
      var tokenResponse = futures.get(i).get(1, TimeUnit.SECONDS);
      var tokenRequestDetails = new TokenRequestDetails.Builder().withChildOrganizationNumber(orgNrList.get(i)).build();
      var tokenResponseCached = clientCredentials.getAccessToken(tokenRequestDetails);

      if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
        if (tokenResponseCached instanceof AccessTokenResponse accessTokenResponseCached) {
          assertEquals(
              accessTokenResponseCached.accessToken(),
              accessTokenResponse.accessToken(),
              "Cached access token is not equal the original access token"
          );
        } else {
          fail("Cached token response " + i + " failed");
        }
      } else {
        fail("Token response " + i + " failed");
      }
    }

    // Once, since the metadata is initialized prior to the requests
    wms.verify(1, getRequestedFor(urlEqualTo("/.well-known/openid-configuration")));

    // Two per unique token requests
    wms.verify(orgNrList.size() * 2, postRequestedFor(urlEqualTo("/connect/token")));
  }
}