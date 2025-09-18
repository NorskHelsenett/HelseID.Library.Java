package no.helseid.selfservice.clientsecret;

import com.nimbusds.jose.jwk.JWK;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretEndpoint;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretErrorResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretSuccessResponse;
import no.helseid.signing.Algorithm;
import no.helseid.signing.KeyReference;
import no.helseid.signing.RSAKeyReference;
import no.helseid.signing.Util;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;


public class DefaultClientSecretUpdater implements ClientSecretUpdater {
  private final URI selfServiceClientSecretEndpoint;
  private final ClientCredentials clientCredentials;
  private final DPoPProofCreator dPoPProofCreator;
  private final List<String> clientSecretScope;

  public DefaultClientSecretUpdater(
      final URI selfServiceClientSecretEndpoint,
      final ClientCredentials clientCredentials,
      final List<String> clientSecretScope
  ) {
    this.selfServiceClientSecretEndpoint = selfServiceClientSecretEndpoint;
    this.clientCredentials = clientCredentials;
    this.dPoPProofCreator = clientCredentials.getCurrentDPoPProofCreator();
    this.clientSecretScope = clientSecretScope;
  }

  public UpdatedClientSecretSuccess generateNewClientSecret() throws HelseIdException {
    UpdatedClientSecretResult result = updateClientSecret(RSAKeyReference.generate(Algorithm.PS256));

    if (result instanceof UpdatedClientSecretSuccess updatedClientSecretSuccess) {
      return updatedClientSecretSuccess;
    }

    throw new HelseIdException("Update of client secret failed");
  }

  public UpdatedClientSecretResult updateClientSecret(KeyReference newKeyReference) throws HelseIdException {
    TokenResponse tokenResponse = clientCredentials.getAccessToken(clientSecretScope);

    if (tokenResponse instanceof ErrorResponse errorResponse) {
      return new UpdatedClientSecretError(errorResponse, null);
    }

    if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
      JWK jwk = Util.createJWKFromKeyReference(newKeyReference);
      URI clientSecretEndpoint = URI.create(selfServiceClientSecretEndpoint.toString());
      ClientSecretResponse clientSecretResponse = ClientSecretEndpoint.sendRequest(
          clientSecretEndpoint,
          dPoPProofCreator,
          accessTokenResponse.accessToken(),
          jwk
      );

      if (clientSecretResponse instanceof ClientSecretErrorResponse clientSecretErrorResponse) {
        return new UpdatedClientSecretError(null, clientSecretErrorResponse);
      }

      if (clientSecretResponse instanceof ClientSecretSuccessResponse clientSecretSuccessResponse) {
        return new UpdatedClientSecretSuccess(jwk.toJSONString(), clientSecretSuccessResponse.expiration());
      }
    }

    throw new HelseIdException("Unrecognized response class: " + tokenResponse.getClass());
  }
}