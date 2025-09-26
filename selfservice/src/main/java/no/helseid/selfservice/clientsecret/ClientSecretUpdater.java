package no.helseid.selfservice.clientsecret;

import no.helseid.exceptions.HelseIdException;
import no.helseid.signing.KeyReference;

/**
 * An interface for updating client secrets in HelseID Self-Service
 */
public interface ClientSecretUpdater {
  /**
   * Update HelseID Self-Service with the provided key reference
   * @param keyReference the key reference to be uploaded
   * @return the result of the update, UpdatedClientSecretSuccess or UpdatedClientSecretError
   * @throws HelseIdException only if an unexpected error occurs during the upload
   */
  UpdatedClientSecretResult updateClientSecret(KeyReference keyReference) throws HelseIdException;

  /**
   * Generates a new key reference and uploads it to HelseID Self-Service
   * @return the key reference with a date for expiration
   * @throws HelseIdException if the upload is not successful
   */
  UpdatedClientSecretSuccess generateNewClientSecret() throws HelseIdException;
}
