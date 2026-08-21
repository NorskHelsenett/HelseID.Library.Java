package no.helseid.selfservice.endpoints.clientsecret;

import org.jspecify.annotations.NullMarked;

/**
 * A generic representation of a client secret response
 */
@NullMarked
public abstract class ClientSecretResponse {
  /**
   * Create a new instance of a ClientSecretResponse, used by subclasses
   */
  public ClientSecretResponse() {
  }
}
