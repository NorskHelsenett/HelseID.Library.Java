package no.helseid.selfservice.clientsecret;

import org.jspecify.annotations.NullMarked;

/**
 * A generic representation of a result from a client secret update
 */
@NullMarked
public abstract class UpdatedClientSecretResult {
  /**
   * Create a new instance of UpdatedClientSecretResult, used by subclasses
   */
  public UpdatedClientSecretResult() {
  }
}
