package no.helseid.selfservice.clientsecret;

import no.helseid.endpoints.token.ErrorResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretErrorResponse;

public final class UpdatedClientSecretError extends UpdatedClientSecretResult {
  private final ErrorResponse tokenErrorResponse;
  private final ClientSecretErrorResponse clientSecretErrorResponse;

  public UpdatedClientSecretError(
      final ErrorResponse tokenErrorResponse,
      final ClientSecretErrorResponse clientSecretErrorResponse
  ) {
    this.tokenErrorResponse = tokenErrorResponse;
    this.clientSecretErrorResponse = clientSecretErrorResponse;
  }

  public ErrorResponse tokenErrorResponse() {
    return tokenErrorResponse;
  }

  public ClientSecretErrorResponse clientSecretErrorResponse() {
    return clientSecretErrorResponse;
  }
}
