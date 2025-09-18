package no.helseid.selfservice.endpoints.clientsecret;

import java.time.ZonedDateTime;

public class ClientSecretSuccessResponse extends ClientSecretResponse {
  private final ZonedDateTime expiration;

  public ClientSecretSuccessResponse(
      final ZonedDateTime expiration
  ) {
    this.expiration = expiration;
  }

  public ZonedDateTime expiration() {
    return expiration;
  }
}
