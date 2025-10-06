package no.helseid.selfservice.endpoints.clientsecret;

/**
 * A representation of a failed client secret update
 */
public final class ClientSecretErrorResponse extends ClientSecretResponse  {
  private final String type;
  private final String title;
  private final Long status;
  private final String detail;
  private final String instance;

  /**
   * Create a ClientSecretErrorResponse
   * @param type error type
   * @param title title of the error
   * @param status the http status
   * @param detail details of the error
   * @param instance the instance returning the error
   */
  public ClientSecretErrorResponse(String type, String title, Long status, String detail, String instance) {
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
  }

  /**
   * Access the type
   * @return the type
   */
  public String type() {
    return type;
  }

  /**
   * Access the title
   * @return the title
   */
  public String title() {
    return title;
  }

  /**
   * Access the status
   * @return the status
   */
  public Long status() {
    return status;
  }

  /**
   * Access the details
   * @return the details
   */
  public String detail() {
    return detail;
  }

  /**
   * Access the instance
   * @return the instance
   */
  public String instance() {
    return instance;
  }
}
