package no.helseid.examples;

import no.helseid.configuration.Client;
import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import no.helseid.selfservice.clientsecret.*;
import no.helseid.signing.Algorithm;
import no.helseid.signing.JWKKeyReference;
import no.helseid.signing.RSAKeyReference;

import java.time.ZonedDateTime;

import static no.helseid.examples.constants.ExampleConstants.*;

public class SelfServiceClientSecretRotationExample {
  public static void main(String[] args) throws HelseIdException {
    Client initialClient = new Client(CLIENT_ID, JWKKeyReference.parse(JWK), SELF_SERVICE_CLIENT_SCOPE);

    ClientCredentials initialClientCredentials = new ClientCredentials.Builder(AUTHORITY)
        .withClient(initialClient)
        .build();

    ClientSecretUpdater clientSecretUpdater = new DefaultClientSecretUpdater(
        SELF_SERVICE_ENDPOINT,
        initialClientCredentials,
        SELF_SERVICE_CLIENT_SCOPE
    );

    // ALTERNATIVE 1:
    // Generates a new client secret and treats a failure as exception
    try {
      UpdatedClientSecretSuccess result = clientSecretUpdater.generateNewClientSecret();
    } catch (HelseIdException e) {
      // Handle failure
    }

    // ALTERNATIVE 2:
    // For more detailed control use the flow below
    UpdatedClientSecretResult updatedClientSecretResult = clientSecretUpdater.updateClientSecret(RSAKeyReference.generate(Algorithm.PS256));

    if (updatedClientSecretResult instanceof UpdatedClientSecretSuccess updatedClientSecretSuccess) {
      String updatedJwk = updatedClientSecretSuccess.jsonWebKey();
      ZonedDateTime expiration = updatedClientSecretSuccess.expiration();
      // Securely store the updated jwk and renew within the expiration
    } else if (updatedClientSecretResult instanceof UpdatedClientSecretError updatedClientSecretError) {
      if (updatedClientSecretError.tokenErrorResponse() != null) {
        // Handle failed request to the token endpoint
      } else if (updatedClientSecretError.clientSecretErrorResponse() != null) {
        // Handle failed request to the client secret endpoint
      }
    }
  }
}
