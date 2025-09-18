package no.helseid.selfservice.endpoints.clientsecret;

public final class ClientSecretErrorResponse extends ClientSecretResponse  {
  private final String type;
  private final String title;
  private final Long status;
  private final String detail;
  private final String instance;

  public ClientSecretErrorResponse(String type, String title, Long status, String detail, String instance) {
    this.type = type;
    this.title = title;
    this.status = status;
    this.detail = detail;
    this.instance = instance;
  }

  public String type() {
    return type;
  }

  public String title() {
    return title;
  }

  public Long status() {
    return status;
  }

  public String detail() {
    return detail;
  }

  public String instance() {
    return instance;
  }
}
