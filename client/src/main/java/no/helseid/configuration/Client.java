package no.helseid.configuration;

import no.helseid.signing.KeyReference;

import java.util.Set;

/**
 * Representation of a HelseId Client
 * @param clientId the client id listed in HelseId Selv-Service
 * @param keyReference a key reference to
 * @param scope a set of scopes requested for the client
 */
public record Client(String clientId, KeyReference keyReference, Set<String> scope) {
}
