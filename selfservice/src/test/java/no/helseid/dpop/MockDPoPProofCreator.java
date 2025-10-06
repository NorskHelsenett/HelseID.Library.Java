package no.helseid.dpop;

import no.helseid.exceptions.HelseIdException;

import java.net.URI;

public class MockDPoPProofCreator implements DPoPProofCreator {
  public final String value;
  public final String keyId;

  public MockDPoPProofCreator(String value, String keyId) {
    this.value = value;
    this.keyId = keyId;
  }

  @Override
  public String createDPoPProofWithNonce(URI htu, HttpMethod htm, String nonce) {
    return value;
  }

  @Override
  public String createDPoPProof(URI htu, HttpMethod htm, String accessToken) {
    return value;
  }

  @Override
  public String getKeyId() {
    return keyId;
  }
}
