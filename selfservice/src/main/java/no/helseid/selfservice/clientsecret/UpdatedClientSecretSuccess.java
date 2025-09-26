package no.helseid.selfservice.clientsecret;

import java.time.ZonedDateTime;

/**
 * The representation of a successful update of client secret
 */
public final class UpdatedClientSecretSuccess extends UpdatedClientSecretResult {
  private final String jsonWebKey;
  private final ZonedDateTime expiration;

  /**
   * Creates a UpdatedClientSecretSuccess
   * @param jsonWebKey the private jsonWebKey where the public part was published to HelseID Self-Service
   * @param expiration the expiration date returned from HelseID Self-Service
   */
  public UpdatedClientSecretSuccess(
      final String jsonWebKey,
      final ZonedDateTime expiration
  ) {
    this.jsonWebKey = jsonWebKey;
    this.expiration = expiration;
  }

  /**
   * Access the json web key
   * @return the json web key
   */
  public String jsonWebKey() {
    return jsonWebKey;
  }

  /**
   * Access the expiration date
   * @return the expiration date
   */
  public ZonedDateTime expiration() {
    return expiration;
  }
}
