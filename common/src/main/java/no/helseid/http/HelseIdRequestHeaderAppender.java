package no.helseid.http;

import java.net.http.HttpRequest;

/**
 * A class for appending header values to the requests made to HelseId
 */
public class HelseIdRequestHeaderAppender {
  private static final String LIBRARY_HEADER_NAME = "hesleid-lib-version";
  private static final String LIBRARY_HEADER_VALUE = "1.0.3";

  /**
   * Hides constructor on a static class
   */
  private HelseIdRequestHeaderAppender() {
  }

  /**
   * Appends http request for helseid library, for tracking of versions used
   * @param builder the http request builder
   * @return the same http request builder
   */
  public static HttpRequest.Builder AppendHelseIdLibraryVersionHeader(HttpRequest.Builder builder) {
    builder.header(LIBRARY_HEADER_NAME, LIBRARY_HEADER_VALUE);
    return builder;
  }
}