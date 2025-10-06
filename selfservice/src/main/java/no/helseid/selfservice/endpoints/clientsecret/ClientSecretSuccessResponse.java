package no.helseid.selfservice.endpoints.clientsecret;

import java.time.ZonedDateTime;

/**
 * A representation of a successful update of a client secret
 */
public class ClientSecretSuccessResponse extends ClientSecretResponse {
  private final ZonedDateTime expiration;

  /**
   * Creates a ClientSecretSuccessResponse
   * @param expiration the expiration returned from HelseID Self-Service
   */
  public ClientSecretSuccessResponse(
      final ZonedDateTime expiration
  ) {
    this.expiration = expiration;
  }

  /**
   * Access the expiration
   * @return the expiration
   */
  public ZonedDateTime expiration() {
    return expiration;
  }
}
