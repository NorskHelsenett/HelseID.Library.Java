package no.helseid.selfservice.clientsecret;

import no.helseid.exceptions.HelseIdException;
import no.helseid.signing.KeyReference;

public interface ClientSecretUpdater {
  UpdatedClientSecretResult updateClientSecret(KeyReference keyReference) throws HelseIdException;
  UpdatedClientSecretSuccess generateNewClientSecret() throws HelseIdException;
}
