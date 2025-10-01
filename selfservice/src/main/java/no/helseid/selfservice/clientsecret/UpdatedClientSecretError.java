package no.helseid.selfservice.clientsecret;

import no.helseid.endpoints.token.TokenResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretResponse;

/**
 * The representation of a failed update of client secret
 */
public final class UpdatedClientSecretError extends UpdatedClientSecretResult {
  private final TokenResponse tokenResponse;
  private final ClientSecretResponse clientSecretResponse;

  /**
   * Creates a UpdatedClientSecretError
   * @param tokenResponse a token error response if it occurred
   * @param clientSecretResponse a client secret error response if it occurred
   */
  public UpdatedClientSecretError(
      final TokenResponse tokenResponse,
      final ClientSecretResponse clientSecretResponse
  ) {
    this.tokenResponse = tokenResponse;
    this.clientSecretResponse = clientSecretResponse;
  }

  /**
   * Access the token response
   * @return the token response
   */
  public TokenResponse tokenResponse() {
    return tokenResponse;
  }

  /**
   * Access the client secret response
   * @return the client secret response
   */
  public ClientSecretResponse clientSecretResponse() {
    return clientSecretResponse;
  }
}
