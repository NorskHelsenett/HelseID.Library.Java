package no.helseid.examples;

import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.configuration.Client;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.dpop.DefaultDPoPProofCreator;
import no.helseid.dpop.HttpMethod;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import no.helseid.signing.JWKKeyReference;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

import static no.helseid.examples.constants.ExampleConstants.*;

public class ClientCredentialsExample {
  private static final Set<String> SCOPE = Collections.singleton("nhn:kjernejorunal/api");

  public static void main(String[] args) throws HelseIdException {
    Client client = new Client(CLIENT_ID, JWKKeyReference.parse(JWK), SCOPE);

    DPoPProofCreator dPoPProofCreator = new DefaultDPoPProofCreator(client.keyReference());

    ClientCredentials clientCredentials = new ClientCredentials.Builder(AUTHORITY)
        .withClient(client)
        .setCustomDPoPProofCreator(dPoPProofCreator)
        .build();

    TokenRequestDetails tokenRequestDetails = new TokenRequestDetails.Builder()
        .withTenancy(TENANCY)
        .withParentOrganizationNumber("994598759")
        .withChildOrganizationNumber("994598759")
        .build();
    TokenResponse tokenResponse = clientCredentials.getAccessToken(tokenRequestDetails);

    if (tokenResponse instanceof ErrorResponse errorResponse) {
      handleErrorResponse(errorResponse);
    }

    if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
      String accessToken = accessTokenResponse.accessToken();

      // You can use the access token
      String dPoPProof = dPoPProofCreator.createDPoPProof(
          URI.create("https://api.no/"),
          HttpMethod.GET,
          accessToken);
    }
  }

  private static void handleErrorResponse(ErrorResponse errorResponse) {
    System.out.printf("Something went wrong: %s\n%s\n\n%s", errorResponse.error(), errorResponse.errorDescription(), errorResponse.rawResponseBody());
    System.exit(1);
  }
}
