package no.helseid.selfservice.clientsecret;

import no.helseid.endpoints.token.TokenResponse;
import no.helseid.selfservice.endpoints.clientsecret.ClientSecretResponse;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * The representation of a failed update of client secret
 */
@NullMarked
public final class UpdatedClientSecretError extends UpdatedClientSecretResult {
  private final @Nullable TokenResponse tokenResponse;
  private final @Nullable ClientSecretResponse clientSecretResponse;

  /**
   * Creates a UpdatedClientSecretError
   * @param tokenResponse a token error response if it occurred
   * @param clientSecretResponse a client secret error response if it occurred
   */
  public UpdatedClientSecretError(
      final @Nullable TokenResponse tokenResponse,
      final @Nullable ClientSecretResponse clientSecretResponse
  ) {
    this.tokenResponse = tokenResponse;
    this.clientSecretResponse = clientSecretResponse;
  }

  /**
   * Access the token response
   * @return the token response
   */
  public @Nullable TokenResponse tokenResponse() {
    return tokenResponse;
  }

  /**
   * Access the client secret response
   * @return the client secret response
   */
  public @Nullable ClientSecretResponse clientSecretResponse() {
    return clientSecretResponse;
  }
}
