package no.helseid.clientassertion;

import no.helseid.dpop.DPoPProofCreator;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.grants.ClientCredentials;

import java.util.List;

public record MockClientCredentials(TokenResponse tokenResponse,
                                    DPoPProofCreator dPoPProofCreator) implements ClientCredentials {

  @Override
  public TokenResponse getAccessToken() {
    return tokenResponse;
  }

  @Override
  public TokenResponse getAccessToken(List<String> scope) {
    return tokenResponse;
  }

  @Override
  public TokenResponse getAccessToken(AssertionDetails assertionDetails) {
    return tokenResponse;
  }

  @Override
  public TokenResponse getAccessToken(List<String> scope, AssertionDetails assertionDetails) {
    return tokenResponse;
  }

  @Override
  public DPoPProofCreator getCurrentDPoPProofCreator() {
    return dPoPProofCreator;
  }
}
