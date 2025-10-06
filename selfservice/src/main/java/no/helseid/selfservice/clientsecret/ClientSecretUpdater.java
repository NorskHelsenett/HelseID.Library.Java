package no.helseid.selfservice.clientsecret;

import no.helseid.exceptions.HelseIdException;

/**
 * An interface for updating client secrets in HelseID Self-Service
 */
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
}
