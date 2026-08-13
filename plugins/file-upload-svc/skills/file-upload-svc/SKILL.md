---
name: file-upload-svc
description: |
  File upload service with Bearer Token authentication.
  Enforces 10MB limit, jpg/jpeg/png/pdf only, contract-first OpenAPI 3.1.
---

# File Upload Service

## Constraints

When implementing or modifying file upload related functionality, the AI agent MUST follow these rules:

### 1. Bearer Authentication
- All `/upload` and related endpoints MUST require a valid Bearer Token (JWT)
- Missing, malformed, or expired tokens MUST return 401
- Token values MUST NOT be exposed in error messages or logs

### 2. File Type Allowlist
- Only allowed types: `jpg`, `jpeg`, `png`, `pdf`
- Validation MUST be based on detected Content-Type, not just file extension
- Rejected types MUST return 415

### 3. File Size Limit
- Maximum file size: 10 MiB (10,485,760 bytes)
- MUST be enforced before persistent storage
- Exceeded limit MUST return 413

### 4. Contract First
- OpenAPI 3.1 is the single source of truth
- Any API change MUST first update `openapi.yaml`, then the implementation
- Implementation MUST match the OpenAPI contract exactly

### 5. Verifiability
- Each security control (auth, type, size) MUST have automated tests
- Tests MUST cover both permitted and rejected scenarios
- Reviews MUST verify alignment between OpenAPI contract and implementation