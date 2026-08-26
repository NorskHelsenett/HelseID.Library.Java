package no.helseid.selfservice.clientsecret;

import no.helseid.exceptions.HelseIdException;
import no.helseid.grants.ClientCredentials;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * An interface for updating client secrets in HelseID Self-Service
 */
@NullMarked
public interface ClientSecretUpdater {
  /**
   * Generates a new key reference and uploads it to HelseID Self-Service
   * @return the result of the update, UpdatedClientSecretSuccess or UpdatedClientSecretError
   * @throws HelseIdException only if an unexpected error occurs during the upload
   */
  UpdatedClientSecretResult updateClientSecret() throws HelseIdException;

  /**
   * Generates a new key reference and uploads it to HelseID Self-Service
   * @return the key reference with a date for expiration
   * @throws HelseIdException if the upload is not successful
   */
  UpdatedClientSecretSuccess generateNewClientSecret() throws HelseIdException;

  /**
   * Builder class for Client Secret Updater
   */
  class Builder {
    private final URI endpoint;
    private final Set<String> clientSecretScope = new HashSet<>();
    private @Nullable ClientCredentials clientCredentials;
    private @Nullable HttpClient httpClient;

    /**
     * Initialize a builder class for client secret updater
     * @param endpoint the endpoint to communicate with
     */
    public Builder(final URI endpoint) {
      this.endpoint = endpoint;
    }

    /**
     * Add a scope to the request
     * @param scope a scope to include in the request
     * @return the current builder
     */
    public ClientSecretUpdater.Builder addScope(final String scope) {
      this.clientSecretScope.add(scope);
      return this;
    }


    /**
     * Add scopes to the request
     * @param scopes a collection of scopes to include in the request
     * @return the current builder
     */
    public ClientSecretUpdater.Builder addScopes(final Collection<String> scopes) {
      this.clientSecretScope.addAll(scopes);
      return this;
    }

    /**
     * Assign the client credentials to use
     * @param clientCredentials the client credentials configuration used
     * @return the current builder
     */
    public ClientSecretUpdater.Builder withClientCredentials(final ClientCredentials clientCredentials) {
      this.clientCredentials = clientCredentials;
      return this;
    }

    /**
     * Assign a custom HttpClient, preconfigured with eventual proxies or so
     * @param httpClient a custom HttpClient
     * @return the current builder
     */
    public ClientSecretUpdater.Builder setCustomHttpClient(final HttpClient httpClient) {
      this.httpClient = httpClient;
      return this;
    }

    /**
     * Build the client secret updater
     * @return a default implementation of client secret updater
     * @throws HelseIdException if misconfigured
     */
    public ClientSecretUpdater build() throws HelseIdException {
      if (clientCredentials == null) {
        throw new HelseIdException("No client credentials is provided");
      }

      if (httpClient == null) {
        httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
      }

      if (clientSecretScope.isEmpty()) {
        throw new HelseIdException("No client secret scope is provided");
      }

      return new DefaultClientSecretUpdater(
          this.endpoint,
          this.clientCredentials,
          this.clientSecretScope,
          this.httpClient
      );
    }
  }
}
