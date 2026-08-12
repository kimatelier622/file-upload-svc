<!--
Sync Impact Report
- Version change: template → 1.0.0
- Modified principles: None (initial adoption)
- Added sections: Core Principles; Upload Security Requirements; API Contract and Delivery Workflow;
  Governance
- Removed sections: None
- Follow-up TODOs: None
-->
# File Upload Service Constitution

## Core Principles

### I. Bearer Authentication Required
Every endpoint MUST require a valid Bearer Token. Endpoints MUST reject missing, malformed, or
invalid tokens before performing application operations. Authentication exceptions require an
explicit amendment to this constitution. Rationale: a consistent authentication boundary prevents
unintended public access.

### II. Strict Upload Allowlist
The service MUST accept uploads only when the supplied file is a JPG, JPEG, PNG, or PDF. Validation
MUST use the file's detected content type and MUST NOT rely solely on its filename or extension.
Rejected formats MUST receive a documented client error without persisting the upload. Rationale:
an allowlist minimizes the executable and parser attack surface.

### III. Ten-Megabyte Upload Limit
The service MUST reject any uploaded file larger than 10 MiB (10,485,760 bytes). It MUST enforce
this limit before durable storage and must not bypass it for a client, endpoint, or content type.
Rationale: bounded uploads protect service availability and storage capacity.

### IV. Contract-First OpenAPI
OpenAPI 3.1 is the single source of truth for every HTTP endpoint, including authentication,
request and response schemas, status codes, file constraints, and error responses. Implementations,
tests, and client integrations MUST conform to the approved OpenAPI document; contract changes
MUST be made there before implementation changes. Rationale: one authoritative contract prevents
API drift.

### V. Verifiable Enforcement
Each authentication, file-type, size-limit, and API-contract change MUST include automated tests
that demonstrate permitted and rejected behavior. Reviews MUST verify that the OpenAPI 3.1
definition and its implementation remain aligned. Rationale: security controls only provide
reliable protection when their enforcement is continuously verified.

## Upload Security Requirements

All upload paths MUST apply authentication, detected-content-type validation, and the 10 MiB limit.
The service MUST return documented errors for authentication, type, and size failures, and MUST NOT
expose token values or sensitive validation internals in error messages or logs.

## API Contract and Delivery Workflow

The approved OpenAPI 3.1 document MUST define the Bearer authentication scheme and each endpoint's
security requirement. It MUST define upload request media types, the 10 MiB maximum, the JPG/JPEG,
PNG, and PDF allowlist, and error responses. Before release, maintainers MUST validate the contract
against the implementation and run the relevant automated tests.

## Governance

This constitution supersedes conflicting project practices. Amendments MUST be documented in this
file, reviewed by project maintainers, and include any necessary migration or compatibility plan.
Versioning follows semantic versioning: MAJOR for incompatible governance changes, MINOR for added
or materially expanded principles, and PATCH for clarifications. Every review and release check
MUST assess compliance with this constitution, with particular attention to the OpenAPI 3.1
contract and upload-security controls.

**Version**: 1.0.0 | **Ratified**: 2026-08-12 | **Last Amended**: 2026-08-12
