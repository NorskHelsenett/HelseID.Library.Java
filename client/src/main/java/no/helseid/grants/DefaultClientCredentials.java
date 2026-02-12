package no.helseid.grants;

import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import no.helseid.cache.ExpiringCache;
import no.helseid.clientassertion.AssertionDetails;
import no.helseid.clientassertion.ClientAssertion;
import no.helseid.configuration.Client;
import no.helseid.dpop.DPoPProofCreator;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.TokenEndpoint;
import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.exceptions.HelseIdException;
import no.helseid.metadata.MetadataProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;

import java.util.Collections;
import java.util.Set;

/**
 * Default implementation of Client Credentials
 */
public final class DefaultClientCredentials implements ClientCredentials {
  private final Client client;
  private final MetadataProvider metadataProvider;
  private final ExpiringCache<AccessTokenResponse> tokenCache;
  private final DPoPProofCreator dPoPProofCreator;

  /**
   * @param client the client preforming the client credentials flow
   * @param metadataProvider a metadata provider accessing and caching the discovery endpoint
   * @param tokenCache a cache containing token responses
   * @param dPoPProofCreator a dpop proof creator
   */
  DefaultClientCredentials(
      Client client,
      MetadataProvider metadataProvider,
      ExpiringCache<AccessTokenResponse> tokenCache,
      DPoPProofCreator dPoPProofCreator
  ) {
    this.client = client;
    this.metadataProvider = metadataProvider;
    this.tokenCache = tokenCache;
    this.dPoPProofCreator = dPoPProofCreator;
  }

  @Override
  public DPoPProofCreator getCurrentDPoPProofCreator() {
    return dPoPProofCreator;
  }

  @Override
  public TokenResponse getAccessToken() throws HelseIdException {
    return getAccessToken(null);
  }

  @Override
  public TokenResponse getAccessToken(TokenRequestDetails tokenRequestDetails) throws HelseIdException {
    var metadata = metadataProvider.getMetadata();
    var scopeSet = getCurrentScope(client, tokenRequestDetails);

    var cacheKey = createCacheKey(client, tokenRequestDetails, scopeSet);

    TokenResponse helseIdTokenResponse = tokenCache.get(cacheKey);

    if (helseIdTokenResponse != null) {
      return helseIdTokenResponse;
    }

    AuthorizationGrant clientGrant = new ClientCredentialsGrant();
    SignedJWT clientAssertion = ClientAssertion.createClientAssertionSignedJWT(
        metadata.getIssuer().getValue(),
        client,
        tokenRequestDetails == null ? null : AssertionDetails.fromTokenRequestDetails(tokenRequestDetails)
    );

    TokenResponse tokenResponse = TokenEndpoint.sendRequest(
        metadata.getTokenEndpointURI(),
        dPoPProofCreator,
        clientAssertion,
        clientGrant,
        Scope.parse(client.scope()),
        Collections.emptyList(),
        null
    );

    if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
      var expireAtEpochMillisecond = Instant.now().plusSeconds(accessTokenResponse.expiresInSeconds()).toEpochMilli();
      tokenCache.put(cacheKey, accessTokenResponse, expireAtEpochMillisecond);
    }

    return tokenResponse;
  }

  private String createCacheKey(Client client, TokenRequestDetails tokenRequestDetails, Set<String> scopeSet) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }

    StringBuilder builder = new StringBuilder();
    builder.append(client.keyReference().getKeyId());

    if (tokenRequestDetails == null) {
      return new String(digest.digest(builder.toString().getBytes()));
    }

    if (tokenRequestDetails.parentOrganizationNumber() != null) {
      builder.append(tokenRequestDetails.parentOrganizationNumber());
    }
    if (tokenRequestDetails.childOrganizationNumber() != null) {
      builder.append(tokenRequestDetails.childOrganizationNumber());
    }
    if (tokenRequestDetails.sfmJournalId() != null) {
      builder.append(tokenRequestDetails.sfmJournalId());
    }
    if (scopeSet != null) {
      scopeSet.forEach(builder::append);
    }

    return new String(digest.digest(builder.toString().getBytes()));
  }

  private Set<String> getCurrentScope(Client client, TokenRequestDetails tokenRequestDetails) {
    if (tokenRequestDetails == null || tokenRequestDetails.scope().isEmpty()) {
      return client.scope();
    }
    return tokenRequestDetails.scope();
  }
}
