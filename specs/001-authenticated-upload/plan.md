# Implementation Plan: Authenticated File Upload

**Branch**: `001-authenticated-upload` | **Date**: 2026-08-12 | **Spec**: [spec.md](spec.md)

## Summary

Deliver `POST /upload` with a contract-first OpenAPI 3.1 workflow. OpenAPI Generator produces
Spring server interfaces/models and a TypeScript fetch client. Spring Boot implements the generated
interface and uses Spring Security Resource Server JWT validation before upload processing. React
uses the generated client to submit a single file and display the result or safe error.

## Technical Context

**Language/Version**: Java 21 / Spring Boot 3.x; TypeScript 5.x / React 18+

**Primary Dependencies**: Spring Boot Web; Spring Security OAuth2 Resource Server; OpenAPI
Generator; React; generated TypeScript fetch client

**Storage**: Local filesystem storage behind an upload-storage abstraction for this release

**Testing**: JUnit 5 Spring MVC integration and contract tests; Vitest and React Testing Library

**Target Platform**: Linux-compatible server runtime and modern browsers

**Project Type**: Web application

**Performance Goals**: A valid upload of 10 MiB or less receives a result within 5 seconds under
normal service conditions; the UI updates without a page reload.

**Constraints**: Valid Bearer JWT on every endpoint; exactly one file; JPEG, PNG, or PDF only;
10 MiB maximum (10,485,760 bytes); no storage for rejection; OpenAPI 3.1 is authoritative.

**Scale/Scope**: One upload endpoint and one upload UI flow. Token issuance, file listing,
deletion, sharing, retention policy, and external object storage are excluded.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Gate | Design response | Status |
| --- | --- | --- |
| Bearer authentication | Spring Security authenticates all backend routes before controller execution. | Pass |
| Allowed types | Byte-based content detection allows only `image/jpeg`, `image/png`, and `application/pdf`. | Pass |
| Size limit | Multipart and service validation enforce 10,485,760 bytes before durable storage. | Pass |
| Contract-first OpenAPI | `contracts/openapi.yaml` is OpenAPI 3.1 and drives both server and client generation. | Pass |
| Verifiable enforcement | Contract, backend integration, and frontend tests cover acceptance and rejection. | Pass |

**Post-design re-check**: Pass. The contract, data model, and quickstart include each mandatory
security control and no design violates the constitution.

## Project Structure

### Documentation (this feature)

```text
specs/001-authenticated-upload/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── openapi.yaml
└── tasks.md
```

### Source Code (repository root)

```text
backend/
├── openapi/                 # Generator configuration and generated server sources
├── src/main/java/           # Security config and generated-interface implementation
├── src/main/resources/
└── src/test/java/           # Contract and upload integration tests

frontend/
├── src/api/                 # OpenAPI-generated TypeScript fetch client
├── src/features/upload/     # File form and result/error display
└── src/**/*.test.tsx        # UI and client tests
```

**Structure Decision**: Separate Spring Boot and React applications consume generated code from the
same contract, avoiding hand-maintained endpoint signatures and duplicated API models.

## Complexity Tracking

No constitution violations require justification.
