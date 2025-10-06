package no.helseid.selfservice.clientsecret;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import net.minidev.json.JSONObject;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.dpop.HttpMethod;
import no.helseid.exceptions.HelseIdException;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretEndpoint;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretSuccessResponse;
import no.helseid.signing.Algorithm;
import no.helseid.signing.RSAKeyReference;
import no.helseid.signing.Util;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.ZonedDateTime;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class ClientSecretEndpointTest {
  private static String CLIENT_SECRET_PATH = "/v1/client-secret";
  private WireMockServer wms;

  private final DPoPProofCreator dPoPProofCreator = new DPoPProofCreator() {
    @Override
    public String createDPoPProofWithNonce(URI htu, HttpMethod htm, String nonce) {
      throw new RuntimeException("Not implemented");
    }

    @Override
    public String createDPoPProof(URI htu, HttpMethod htm, String accessToken) {
      return "my.dpop.proof";
    }

    @Override
    public String getKeyId() {
      return "my-key-id";
    }
  };

  @BeforeEach
  void setup() {
    wms = new WireMockServer(WireMockConfiguration.options().dynamicPort());
    wms.start();
  }

  @Test
  public void should_recieve_expected_token_on_client_credentials_grant() throws HelseIdException {
    var endpoint = URI.create(wms.baseUrl() + CLIENT_SECRET_PATH);
    var accessToken = "my.access.token";
    var dpopProofBoundToAccessToken = "my.dpop.proof";
    var jwk = Util.createJWKFromKeyReference(RSAKeyReference.generate(Algorithm.PS256));
    var expiration = ZonedDateTime.now().plusMonths(1);

    wms.stubFor(post(urlPathEqualTo(CLIENT_SECRET_PATH))
        .withHeader("Authorization", equalTo("DPoP " + accessToken))
        .withHeader("DPoP", equalTo(dpopProofBoundToAccessToken))
        .withHeader("Content-Type", equalTo("application/json"))
        .withHeader("Accept", equalTo("application/json"))
        .willReturn(ok()
            .withHeader("Content-Type", "application/json")
            .withBody(new JSONObject().appendField("expiration", expiration.toString()).toJSONString())
        ));

    ClientSecretResponse clientSecretResponse = ClientSecretEndpoint.sendRequest(
        endpoint,
        dPoPProofCreator,
        accessToken,
        jwk
    );

    assertInstanceOf(ClientSecretSuccessResponse.class, clientSecretResponse);

    assertEquals(expiration, ((ClientSecretSuccessResponse) clientSecretResponse).expiration());

    wms.verify(1, postRequestedFor(urlPathEqualTo(CLIENT_SECRET_PATH)));
  }
}