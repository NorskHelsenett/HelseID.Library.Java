package no.helseid.clientassertion;

import no.helseid.dpop.DPoPProofCreator;
import no.helseid.endpoints.token.TokenRequestDetails;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.grants.ClientCredentials;

public record MockClientCredentials(TokenResponse tokenResponse,
                                    DPoPProofCreator dPoPProofCreator) implements ClientCredentials {

  @Override
  public TokenResponse getAccessToken() {
    return tokenResponse;
  }

  @Override
  public TokenResponse getAccessToken(TokenRequestDetails tokenRequestDetails) {
    return tokenResponse;
  }

  @Override
  public DPoPProofCreator getCurrentDPoPProofCreator() {
    return dPoPProofCreator;
  }
}
