package no.helseid.metadata;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import no.helseid.exceptions.HelseIdException;
import org.jspecify.annotations.NullMarked;

/**
 * Interface for providing metadata about HelseID STS
 */
@NullMarked
public interface MetadataProvider {
  /**
   * Get a metadata object
   *
   * @return metadata describing the OIDC provider
   * @throws HelseIdException if failing to get the metadata
   */
  OIDCProviderMetadata getMetadata() throws HelseIdException;
}
