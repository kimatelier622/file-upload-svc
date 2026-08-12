# Research: Authenticated File Upload

## JWT validation

**Decision**: Configure Spring Security OAuth2 Resource Server with JWT Bearer support, an issuer
URL, and a JWK Set URL where issuer discovery must not block startup. Validate signature, issuer,
expiration, and not-before claims.

**Rationale**: Resource-server support validates Bearer JWTs before application code runs.

**Alternatives considered**: A custom JWT filter duplicates established security behavior. Opaque
token introspection conflicts with the feature requirement for JWTs.

## Contract-first code generation

**Decision**: Maintain `contracts/openapi.yaml` as OpenAPI 3.1. Run OpenAPI Generator to create
Spring Boot server interfaces/models and a `typescript-fetch` client. Keep generated code separate
from handwritten security, storage, and UI behavior.

**Rationale**: The Spring generator supports API-first Spring Boot code generation and the
TypeScript fetch generator supplies a typed browser client from the same contract.

**Alternatives considered**: Handwritten controllers and frontend types risk API drift. Generating
the full server would obscure reviewed security and storage enforcement.

## Upload validation and persistence

**Decision**: Enforce multipart limits at the Spring boundary and re-check before writing. Detect
content from bytes; allow only `image/jpeg`, `image/png`, and `application/pdf`; store accepted
content under a server-generated opaque ID through a storage abstraction.

**Rationale**: Boundary limits reduce oversized processing, byte detection defeats spoofed
extensions, and opaque IDs avoid exposing user names or storage paths.

**Alternatives considered**: Extension-only and client MIME-type checks violate the constitution.

## Frontend upload experience

**Decision**: Provide one React form using the generated client. It displays the returned ID and
link on success and safe contract-mapped errors on rejection.

**Rationale**: This delivers a complete vertical slice without duplicating backend validation.
