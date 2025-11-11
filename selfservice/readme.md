# Self service

## Installation
```xml
<dependency>
    <groupId>no.helseid</groupId>
    <artifactId>selfservice</artifactId>
</dependency>
```

## Setup
When setting up `DefaultClientSecretUpdater` a `ClientCredentials` instance is required, [read how to setup client credentials here](../client/readme.md).

```java
import no.helseid.selfservice.clientsecret.*;

ClientCredentials clientCredentials = ...;

ClientSecretUpdater clientSecretUpdater = new DefaultClientSecretUpdater(
    URI.create("https://api.selvbetjening.test.nhn.no/v1/client-secret"),
    clientCredentials,
    Collections.singleton("nhn:selvbetjening/client")
);
```

## Updating a client secret
When updating a client secret the updated private jwk and it's expiration is returned as a response.

```java
UpdatedClientSecretResult updatedClientSecretResult = clientSecretUpdater.updateClientSecret();

if (updatedClientSecretResult instanceof UpdatedClientSecretSuccess updatedClientSecretSuccess) {
  String updatedJwk = updatedClientSecretSuccess.jsonWebKey();
  ZonedDateTime expiration = updatedClientSecretSuccess.expiration();
  
  // Securely store the updated jwk and renew within the expiration
}
```

## Error handling
Any error is wrapped in a `UpdatedClientSecretError`, it is recommended to handle the wrapped errors individually.
```java
if (updatedClientSecretResult instanceof UpdatedClientSecretError updatedClientSecretError) {
  if (updatedClientSecretError.tokenResponse() != null) {
    // Handle failed request to the token endpoint
  } else if (updatedClientSecretError.clientSecretResponse() != null) {
    // Handle failed request to the client secret endpoint
  }
}
```

An example of a more complete implementation for [updating a client secret](../examples/src/main/java/no/helseid/examples/SelfServiceClientSecretRotationExample.java) is found in the examples module.