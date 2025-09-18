package no.helseid.selfservice;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.helseid.clientassertion.MockClientCredentials;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.dpop.MockDPoPProofCreator;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import no.helseid.selfservice.clientsecret.DefaultClientSecretUpdater;
import no.helseid.selfservice.clientsecret.UpdatedClientSecretError;
import no.helseid.selfservice.clientsecret.UpdatedClientSecretResult;
import no.helseid.selfservice.clientsecret.UpdatedClientSecretSuccess;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretEndpoint;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretErrorResponse;
import no.helseid.signing.Algorithm;
import no.helseid.signing.RSAKeyReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wiremock.net.minidev.json.JSONObject;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class DefaultClientSecretUpdaterTest {
  private static String CLIENT_SECRET_PATH = "/v1/client-secret";
  private static List<String> CLIENT_SECRET_SCOPE = Collections.singletonList("nhn:selvbetjening/client");
  private WireMockServer wms;

  private static ClientCredentials mockClientCredentials(TokenResponse tokenResponse) {
    DPoPProofCreator dPoPProofCreator = new MockDPoPProofCreator("my.dpop.proof", "keyId");
    return new MockClientCredentials(tokenResponse, dPoPProofCreator);
  }

  @BeforeEach
  void setup() {
    wms = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wms.start();
  }

  @Test
  void successful_update_of_client_secret_should_return_an_expiration_date() throws HelseIdException {
    ClientCredentials clientCredentials = mockClientCredentials(new AccessTokenResponse(
        "my.access.token",
        "DPoP",
        10,
        CLIENT_SECRET_SCOPE));
    DefaultClientSecretUpdater updater = new DefaultClientSecretUpdater(
        URI.create(wms.baseUrl() + CLIENT_SECRET_PATH),
        clientCredentials,
        CLIENT_SECRET_SCOPE);
    var expiration = ZonedDateTime.now().plusMonths(1);
    wms.stubFor(WireMock.post(WireMock.urlPathEqualTo(CLIENT_SECRET_PATH))
        .willReturn(ok().withBody(
            new JSONObject()
                .appendField("expiration", expiration.toString())
                .toJSONString()
        )));

    UpdatedClientSecretResult updatedClientSecretResult = updater.updateClientSecret(RSAKeyReference.generate(Algorithm.PS256));

    assertInstanceOf(UpdatedClientSecretSuccess.class, updatedClientSecretResult);
    UpdatedClientSecretSuccess updatedClientSecretSuccess = (UpdatedClientSecretSuccess) updatedClientSecretResult;
    assertEquals(expiration, updatedClientSecretSuccess.expiration());
  }

  @Test
  void failed_update_of_client_secret_should_return_a_failure_containing_an_ClientSecretErrorResponse() throws HelseIdException {
    ClientCredentials clientCredentials = mockClientCredentials(new AccessTokenResponse(
        "my.access.token",
        "DPoP",
        10,
        Collections.singletonList("nhn:helseid")));
    DefaultClientSecretUpdater updater = new DefaultClientSecretUpdater(
        URI.create(wms.baseUrl() + CLIENT_SECRET_PATH),
        clientCredentials,
        CLIENT_SECRET_SCOPE
    );
    wms.stubFor(WireMock.post(WireMock.urlPathEqualTo(CLIENT_SECRET_PATH))
        .willReturn(badRequest().withBody(
            new JSONObject()
                .appendField("type", "type")
                .appendField("title", "title")
                .appendField("status", 400)
                .appendField("detail", "detail")
                .appendField("instance", "instance")
                .toJSONString()
        )));

    UpdatedClientSecretResult updatedClientSecretResult = updater.updateClientSecret(RSAKeyReference.generate(Algorithm.PS256));

    assertInstanceOf(UpdatedClientSecretError.class, updatedClientSecretResult);
    UpdatedClientSecretError updatedClientSecretError = (UpdatedClientSecretError) updatedClientSecretResult;
    ClientSecretErrorResponse clientSecretErrorResponse = updatedClientSecretError.clientSecretErrorResponse();

    assertEquals("type", clientSecretErrorResponse.type());
    assertEquals("title", clientSecretErrorResponse.title());
    assertEquals(400, clientSecretErrorResponse.status());
    assertEquals("detail", clientSecretErrorResponse.detail());
    assertEquals("instance", clientSecretErrorResponse.instance());
  }

  @Test
  void failed_token_request_should_return_a_failure_containing_an_ErrorResponse() throws HelseIdException {
    ErrorResponse errorResponse = new ErrorResponse("SOME_ERROR", "SOME_ERROR", 428, "{}");
    ClientCredentials clientCredentials = mockClientCredentials(errorResponse);
    DefaultClientSecretUpdater updater = new DefaultClientSecretUpdater(
        URI.create(wms.baseUrl() + CLIENT_SECRET_PATH),
        clientCredentials,
        CLIENT_SECRET_SCOPE
    );

    UpdatedClientSecretResult updatedClientSecretResult = updater.updateClientSecret(RSAKeyReference.generate(Algorithm.PS256));

    assertInstanceOf(UpdatedClientSecretError.class, updatedClientSecretResult);
    UpdatedClientSecretError updatedClientSecretError = (UpdatedClientSecretError) updatedClientSecretResult;
    assertEquals(errorResponse, updatedClientSecretError.tokenErrorResponse());
  }
}