# Quickstart: Authenticated File Upload Validation

Use [the API contract](contracts/openapi.yaml) and [the data model](data-model.md) as the expected
behavior for every check.

## Prerequisites

- Java 21 and Node.js LTS are installed.
- The configured JWT issuer/JWK Set is reachable, or an approved test issuer is configured.
- A valid test JWT is available without placing it in source control or logs.
- Fixtures include a small JPEG, PNG, PDF, non-allowed file, and files at 10 MiB and 10 MiB plus
  one byte.

## Generate contract bindings

From the repository root, run the backend and frontend generation tasks. They must consume
`specs/001-authenticated-upload/contracts/openapi.yaml` to produce Spring server bindings and the
TypeScript fetch client. Generation must fail for an invalid OpenAPI 3.1 document.

## Run the application

1. Configure the backend JWT issuer and JWK Set for the test issuer.
2. Start Spring Boot, then start the React frontend and open its upload screen.

## Validate API behavior

1. Submit one allowed fixture below 10 MiB with a valid JWT. Expect HTTP 201 and non-empty
   `file_id` and `access_url`.
2. Submit an allowed fixture exactly 10 MiB with a valid JWT. Expect HTTP 201.
3. Submit an allowed fixture without a token and with an invalid or expired token. Expect HTTP 401
   and no persisted file reference.
4. Submit the 10 MiB plus one-byte fixture with a valid JWT. Expect HTTP 413 and no persisted file.
5. Submit a non-allowed fixture and an allowed-extension spoof with a valid JWT. Expect HTTP 415
   and no persisted file.
6. Submit zero or more than one file. Expect HTTP 400 and no persisted file.

## Validate the frontend

1. Submit a permitted file with a valid JWT and confirm the UI displays the returned ID and URL.
2. Repeat unauthenticated, oversize, and unsupported-type requests and confirm safe error display.

## Automated checks

Run backend integration tests and frontend tests for every scenario. Run generator verification and
confirm it reports no stale generated-code differences.
