package no.helseid.endpoints.token;

/**
 * A generic representation of a response from the token endpoint in HelseID
 */
public abstract class TokenResponse {
  protected final String rawResponseBody;
  private final int statusCode;

  /**
   * Used by subclasses
   */
  protected TokenResponse(String rawResponseBody, Integer statusCode) {
    this.statusCode = statusCode;
    this.rawResponseBody = rawResponseBody;
  }

  /**
   * Access the raw body of the HTTP response
   * @return the raw body of the HTTP response
   */
  public String rawResponseBody() {
    return rawResponseBody;
  }

  /**
   * Access the HTTP status-code returned
   * @return the HTTP status-code returned
   */
  public int statusCode() {
    return statusCode;
  }
}
