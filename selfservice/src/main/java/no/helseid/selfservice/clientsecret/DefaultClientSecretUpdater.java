package no.helseid.selfservice.clientsecret;

import com.nimbusds.jose.jwk.JWK;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretEndpoint;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretSuccessResponse;
import no.helseid.signing.Algorithm;
import no.helseid.signing.RSAKeyReference;
import no.helseid.signing.Util;

import java.net.URI;
import java.util.List;


/**
 * Default implementation of a ClientSecretUpdater
 */
public class DefaultClientSecretUpdater implements ClientSecretUpdater {
  private final URI selfServiceClientSecretEndpoint;
  private final ClientCredentials clientCredentials;
  private final DPoPProofCreator dPoPProofCreator;
  private final List<String> clientSecretScope;

  /**
   * Creates the default implementation of the ClientSecretUpdater
   * @param selfServiceClientSecretEndpoint the full endpoint for updating client secrets
   * @param clientCredentials a client credentials instance for the client
   * @param clientSecretScope the scope(s) required for the selfServiceClientSecretEndpoint
   */
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
    UpdatedClientSecretResult result = updateClientSecret();

    if (result instanceof UpdatedClientSecretSuccess updatedClientSecretSuccess) {
      return updatedClientSecretSuccess;
    }

    throw new HelseIdException("Update of client secret failed");
  }

  public UpdatedClientSecretResult updateClientSecret() throws HelseIdException {
    TokenRequestDetails requestDetails = new TokenRequestDetails.Builder()
        .addMultipleScope(clientSecretScope)
        .build();
    TokenResponse tokenResponse = clientCredentials.getAccessToken(requestDetails);

    if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
      JWK jwk = Util.createJWKFromKeyReference(RSAKeyReference.generate(Algorithm.PS256));
      URI clientSecretEndpoint = URI.create(selfServiceClientSecretEndpoint.toString());
      ClientSecretResponse clientSecretResponse = ClientSecretEndpoint.sendRequest(
          clientSecretEndpoint,
          dPoPProofCreator,
          accessTokenResponse.accessToken(),
          jwk
      );

      if (clientSecretResponse instanceof ClientSecretSuccessResponse clientSecretSuccessResponse) {
        return new UpdatedClientSecretSuccess(jwk.toJSONString(), clientSecretSuccessResponse.expiration());
      }

      return new UpdatedClientSecretError(null, clientSecretResponse);
    }

    return new UpdatedClientSecretError(tokenResponse, null);
  }
}