package no.helseid.selfservice.endpoints.clientsecret;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.JSONObjectUtils;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.dpop.HttpMethod;
import no.helseid.exceptions.HelseIdException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * An implementation for communicating with the client secret endpoint in HelseID Self-Service
 */
public interface ClientSecretEndpoint {
  /**
   * Upload a public json web key to  HelseID Self-Service
   * @param endpoint the endpoint
   * @param dPoPProofCreator a dpop proof creator
   * @param accessToken an access token bound to the private key in the dpop proof creator
   * @param jwk a private jwk
   * @return the result of an upload
   * @throws HelseIdException if a request was unable to send or response was unparsable
   */
  static ClientSecretResponse sendRequest(
      URI endpoint,
      DPoPProofCreator dPoPProofCreator,
      String accessToken,
      JWK jwk
  ) throws HelseIdException {
    HttpResponse<String> httpResponse;
    try {
      HttpClient httpclient = HttpClient.newHttpClient();
      HttpRequest httpRequest = HttpRequest.newBuilder(endpoint)
          .POST(HttpRequest.BodyPublishers.ofString(jwk.toPublicJWK().toJSONString()))
          .header("Authorization", "DPoP " + accessToken)
          .header("DPoP", dPoPProofCreator.createDPoPProof(endpoint, HttpMethod.POST, accessToken))
          .header("Content-Type", "application/json")
          .header("Accept", "application/json")
          .build();

      httpResponse = httpclient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

      if (httpResponse.statusCode() >= 500) {
        throw new HelseIdException("Unexpected error from self service");
      }

      Map<String, Object> parsed = JSONObjectUtils.parse(httpResponse.body());
      if (httpResponse.statusCode() >= 400) {
        return handleError(parsed);
      }
      return handleSuccess(parsed);
    } catch (ParseException | IOException e) {
      throw new HelseIdException("Unable to process the response", e);
    } catch (InterruptedException e) {
      throw new HelseIdException("Request interrupted", e);
    }
  }

  private static ClientSecretErrorResponse handleError(Map<String, Object> errorObject) {
    return new ClientSecretErrorResponse(
        (String) errorObject.get("type"),
        (String) errorObject.get("title"),
        (Long) errorObject.get("status"),
        (String) errorObject.get("detail"),
        (String) errorObject.get("instance")
    );
  }

  private static ClientSecretSuccessResponse handleSuccess(Map<String, Object> responseObject) {
    return new ClientSecretSuccessResponse(
        ZonedDateTime.parse((String) responseObject.get("expiration"))
    );
  }
}
