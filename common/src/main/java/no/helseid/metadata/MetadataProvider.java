package no.helseid.metadata;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import no.helseid.exceptions.HelseIdException;

/**
 * Interface for providing metadata about HelseID STS
 */
public interface MetadataProvider {
  /**
   * Get a metadata object
   *
   * @return metadata describing the OIDC provider
   * @throws HelseIdException if failing to get the metadata
   */
  OIDCProviderMetadata getMetadata() throws HelseIdException;
}
