# Client tools
The client tools are useful for preparing authenticated requests to a Resource Server.
Examples of resources requiring authentication with HelseID is found at [utviklerportal.nhn.no](https://utviklerportal.nhn.no/).

## Installation
```xml
<dependency>
    <groupId>no.helseid</groupId>
    <artifactId>client</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Getting started

### Client private key
HelseID requires use of a signing key to do client authentication. The public key is registered via HelseID Selvbetjening while the private key is protected and is only accessible for use by the application itself.
```java
import no.helseid.signing.JWKKeyReference;

KeyReference myPrivateKeyReference = JWKKeyReference.parse("""
  {"alg":"PS256","key_ops":["sign"],"kty":"RSA","d":"...","dp":"...","dq":"...","e":"...","n":"...","p":"...","q":"...","qi":"...","kid":"..."}
""");
```

### Configuration of a client
The reference to the private key is passed to a `Client` object alongside the client-id and a list of scopes.
```java
import no.helseid.configuration.Client;

// Configure your client
Client myClient = new Client(
    "my-client-id",
    myPrivateKeyReference,
    List.of("nhn:api/scope1", "nhn:api/scope2")
);
```

### Client Credentials Grant
The client credentials grant is suited for authentication where no user is involved.

```java
import no.helseid.grants.ClientCredentials;
import no.helseid.endpoints.token.TokenResponse;
import no.helseid.endpoints.token.AccessTokenResponse;
import no.helseid.endpoints.token.ErrorResponse;

ClientCredentials clientCredentials = new ClientCredentials.Builder("https://helseid-sts.test.nhn.no")
    .withClient(myClient)
    .build();

TokenResponse tokenResponse = clientCredentials.getAccessToken();

if (tokenResponse instanceof ErrorResponse errorResponse) {
  // Handle error
}

if (tokenResponse instanceof AccessTokenResponse accessTokenResponse) {
  // Perform request to resourceServer
}
```

### Token request details
When more details is needed in the context you may construct a `TokenRequestDetails` object. In the details you can specify the tenancy, relevant scopes of the request, organization numbers and sfm-journal-id.
When using a single tenant client the child organization number can optionally be provided, the parent organization number is not specified since the client is bound to a single parent organization already known by HelseID.
```java
TokenRequestDetails tokenRequestDetails = new TokenRequestDetails.Builder()
    .withTenancy(TENANCY)
    .withParentOrganizationNumber("994598759")
    .withChildOrganizationNumber("994598759")
    .withSfmJournalId("sfm-id")
    .addScope("nhn:api/scope2")
    .addScope("nhn:sfm/scope1")
    .build();
```

### Accessing an API using DPoP 
If DPoP is required when accessing an API, a `DPoPProofCreator` can be retrieved from the `ClientCredentials` context.
A DPoP-proof can be created by passing the endpoint, http-method and an access-token to the `createDPoPProof` method, returning a proof bound to the access token provided.

```java
import no.helseid.dpop.DPoPProofCreator;

DPoPProofCreator dPoPProofCreator = clientCredentials.getCurrentDPoPProofCreator();
var accessToken = accessTokenResponse.accessToken();
var endpoint = URI.create("https://api.no/");

HttpRequest httpRequest = HttpRequest.newBuilder(endpoint)
    .GET()
    .header("Authorization", "DPoP " + accessToken)
    .header("DPoP", dPoPProofCreator.createDPoPProof(endpoint, HttpMethod.GET, accessToken))
    .build();
```


An example of a more complete implementation for a [multi-tenant client](../examples/src/main/java/no/helseid/examples/ClientCredentialsExample.java) is found in the examples module.