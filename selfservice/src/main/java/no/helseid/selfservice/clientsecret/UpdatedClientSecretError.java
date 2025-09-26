package no.helseid.selfservice.clientsecret;

import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretErrorResponse;

/**
 * The representation of a failed update of client secret
 */
public final class UpdatedClientSecretError extends UpdatedClientSecretResult {
  private final ErrorResponse tokenErrorResponse;
  private final ClientSecretErrorResponse clientSecretErrorResponse;

  /**
   * Creates a UpdatedClientSecretError
   * @param tokenErrorResponse a token error response if it occurred
   * @param clientSecretErrorResponse a client secret error response if it occurred
   */
  public UpdatedClientSecretError(
      final ErrorResponse tokenErrorResponse,
      final ClientSecretErrorResponse clientSecretErrorResponse
  ) {
    this.tokenErrorResponse = tokenErrorResponse;
    this.clientSecretErrorResponse = clientSecretErrorResponse;
  }

  /**
   * Access the token error response
   * @return the token error response
   */
  public ErrorResponse tokenErrorResponse() {
    return tokenErrorResponse;
  }

  /**
   * Access the client secret error response
   * @return the client secret error response
   */
  public ClientSecretErrorResponse clientSecretErrorResponse() {
    return clientSecretErrorResponse;
  }
}
