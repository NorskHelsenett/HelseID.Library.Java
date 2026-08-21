package no.helseid.http;

import com.nimbusds.oauth2.sdk.http.HTTPRequestSender;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.http.ReadOnlyHTTPRequest;
import com.nimbusds.oauth2.sdk.http.ReadOnlyHTTPResponse;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static no.helseid.http.HelseIdRequestHeaderAppender.AppendHelseIdLibraryVersionHeader;

/**
 * Default sender for request to HelseId
 */
@ThreadSafe
public class HelseIdHttpSender implements HTTPRequestSender {
  private final HttpClient httpclient;

  /**
   * Create a HelseIdHttpSender
   */
  public HelseIdHttpSender() {
    this(HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build());
  }

  /**
   * Create a HelseIdHttpSender
   * @param httpclient the http client a sender should use
   */
  public HelseIdHttpSender(HttpClient httpclient) {
    this.httpclient = httpclient;
  }

  /**
   * Mapping a nimbus http request to a generic http request
   * @param readOnlyHTTPRequest a nimbus http request
   * @return a generic http request
   */
  private static HttpRequest mapRequest(ReadOnlyHTTPRequest readOnlyHTTPRequest) {

    HttpRequest.Builder builder = HttpRequest.newBuilder(readOnlyHTTPRequest.getURI());
    builder.method(readOnlyHTTPRequest.getMethod().name(), HttpRequest.BodyPublishers.ofString(readOnlyHTTPRequest.getBody()));
    readOnlyHTTPRequest.getHeaderMap()
        .forEach((name, values) -> values.forEach(value -> builder.header(name, value)));

    AppendHelseIdLibraryVersionHeader(builder);

    return builder.build();
  }

  /**
   * Mapping a generic http response a nimbus http response
   * @param httpResponse a generic http request
   * @return a nimbus http response
   */
  private static ReadOnlyHTTPResponse mapResponse(HttpResponse<String> httpResponse) {
    HTTPResponse response = new HTTPResponse(httpResponse.statusCode());
    httpResponse.headers().map()
        .forEach((name, values) -> response.setHeader(name, values.toArray(new String[0])));
    response.setBody(httpResponse.body());

    return response;
  }

  /**
   * Send a nimbus http request using a generic http client
   * @param readOnlyHTTPRequest a nimbus http request
   * @return a nimbus http response
   * @throws IOException when the request is unparsable
   */
  @Override
  public ReadOnlyHTTPResponse send(ReadOnlyHTTPRequest readOnlyHTTPRequest) throws IOException {
    try {
      return mapResponse(httpclient.send(mapRequest(readOnlyHTTPRequest), HttpResponse.BodyHandlers.ofString()));
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}