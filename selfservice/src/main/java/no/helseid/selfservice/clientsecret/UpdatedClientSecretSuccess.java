package no.helseid.selfservice.clientsecret;

import java.time.ZonedDateTime;

public final class UpdatedClientSecretSuccess extends UpdatedClientSecretResult {
  private final String jsonWebKey;
  private final ZonedDateTime expiration;

  public UpdatedClientSecretSuccess(
      final String jsonWebKey,
      final ZonedDateTime expiration
  ) {
    this.jsonWebKey = jsonWebKey;
    this.expiration = expiration;
  }

  public String jsonWebKey() {
    return jsonWebKey;
  }

  public ZonedDateTime expiration() {
    return expiration;
  }
}
