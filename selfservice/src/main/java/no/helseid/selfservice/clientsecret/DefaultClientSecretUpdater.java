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
import org.jspecify.annotations.NullMarked;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Set;


/**
 * Default implementation of a ClientSecretUpdater
 */
@NullMarked
public class DefaultClientSecretUpdater implements ClientSecretUpdater {
  private final URI selfServiceClientSecretEndpoint;
  private final ClientCredentials clientCredentials;
  private final DPoPProofCreator dPoPProofCreator;
  private final Set<String> clientSecretScope;
  private final HttpClient httpClient;

  /**
   * [DEPRECATED] Use ClientSecretUpdater.Builder
   * Creates the default implementation of the ClientSecretUpdater
   *
   * @param selfServiceClientSecretEndpoint the full endpoint for updating client secrets
   * @param clientCredentials               a client credentials instance for the client
   * @param clientSecretScope               the scope(s) required for the selfServiceClientSecretEndpoint
   */
  @Deprecated
  public DefaultClientSecretUpdater(
      final URI selfServiceClientSecretEndpoint,
      final ClientCredentials clientCredentials,
      final Set<String> clientSecretScope
  ) {
    this(selfServiceClientSecretEndpoint, clientCredentials, clientSecretScope, HttpClient.newHttpClient());
  }

  /**
   * Creates the default implementation of the ClientSecretUpdater
   *
   * @param selfServiceClientSecretEndpoint the full endpoint for updating client secrets
   * @param clientCredentials               a client credentials instance for the client
   * @param clientSecretScope               the scope(s) required for the selfServiceClientSecretEndpoint
   * @param httpClient                      the http client used to send requests
   */
  DefaultClientSecretUpdater(
      final URI selfServiceClientSecretEndpoint,
      final ClientCredentials clientCredentials,
      final Set<String> clientSecretScope,
      final HttpClient httpClient
  ) {
    this.selfServiceClientSecretEndpoint = selfServiceClientSecretEndpoint;
    this.clientCredentials = clientCredentials;
    this.dPoPProofCreator = clientCredentials.getCurrentDPoPProofCreator();
    this.clientSecretScope = clientSecretScope;
    this.httpClient = httpClient;
  }

  /**
   * @return a successfully updated client secret
   * @throws HelseIdException if updating the secret fails at any point
   */
  public UpdatedClientSecretSuccess generateNewClientSecret() throws HelseIdException {
    UpdatedClientSecretResult result = updateClientSecret();

    if (result instanceof UpdatedClientSecretSuccess updatedClientSecretSuccess) {
      return updatedClientSecretSuccess;
    }

    throw new HelseIdException("Update of client secret failed");
  }

  /**
   * @return an updated client secret result, both failed and successful
   * @throws HelseIdException if configuration is wrong or error while getting tokens
   */
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
          jwk,
          httpClient
      );

      if (clientSecretResponse instanceof ClientSecretSuccessResponse clientSecretSuccessResponse) {
        return new UpdatedClientSecretSuccess(jwk.toJSONString(), clientSecretSuccessResponse.expiration());
      }

      return new UpdatedClientSecretError(null, clientSecretResponse);
    }

    return new UpdatedClientSecretError(tokenResponse, null);
  }
}