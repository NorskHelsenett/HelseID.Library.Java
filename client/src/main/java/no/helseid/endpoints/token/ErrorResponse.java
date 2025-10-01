package no.helseid.endpoints.token;

/**
 * A representation of an error response from the token-endpoint in HelseID
 */
public final class ErrorResponse extends TokenResponse {
  private final String error;
  private final String errorDescription;

  /**
   * Create a representation of an error response from the token-endpoint in HelseID
   * @param error the error code returned from HelseID
   * @param errorDescription a description of the error
   * @param statusCode the HTTP status-code returned
   * @param rawResponse the raw body of the HTTO response
   */
  public ErrorResponse(String error, String errorDescription, int statusCode, String rawResponse) {
    super(rawResponse, statusCode);
    this.error = error;
    this.errorDescription = errorDescription;
  }

  /**
   * Access the error code returned from HelseID
   * @return the error code returned from HelseID
   */
  public String error() {
    return error;
  }

  /**
   * Access the description of the error
   * @return a description of the error
   */
  public String errorDescription() {
    return errorDescription;
  }

}

