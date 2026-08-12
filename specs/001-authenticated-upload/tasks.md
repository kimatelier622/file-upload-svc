---

description: "Task list for authenticated file upload"
---

# Tasks: Authenticated File Upload

**Input**: Design documents from `/specs/001-authenticated-upload/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/openapi.yaml,
quickstart.md

**Tests**: Required by Constitution Principle V and FR-009. Write test tasks before the related
implementation tasks and verify they fail before implementation.

**Organization**: Tasks are grouped by user story so each increment is independently testable.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel because it changes a different file and has no incomplete-task
  dependency.
- **[Story]**: User story identifier for story-specific work.

## Path Conventions

- Backend: `backend/src/main/java/`, `backend/src/main/resources/`, and `backend/src/test/java/`
- Frontend: `frontend/src/` and `frontend/src/**/*.test.tsx`
- Contract: `specs/001-authenticated-upload/contracts/openapi.yaml`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the backend/frontend projects and reproducible OpenAPI generation paths.

- [X] T001 Create Spring Boot Java 21 project and Maven configuration in `backend/pom.xml`
- [X] T002 Create React TypeScript project and package configuration in `frontend/package.json`
- [X] T003 [P] Configure OpenAPI Generator Spring server generation in `backend/openapi/openapi-generator-config.yaml`
- [X] T004 [P] Configure OpenAPI Generator TypeScript fetch client generation in `frontend/openapi/openapi-generator-config.yaml`
- [X] T005 Add backend generation task consuming `specs/001-authenticated-upload/contracts/openapi.yaml` in `backend/pom.xml`
- [X] T006 Add frontend generation task consuming `specs/001-authenticated-upload/contracts/openapi.yaml` in `frontend/package.json`
- [ ] T007 [P] Add backend formatting and test configuration in `backend/pom.xml`
- [ ] T008 [P] Add frontend formatting, Vitest, and React Testing Library configuration in `frontend/package.json`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Build the contract, generated bindings, global security, and shared error handling that
all stories require.

**⚠️ CRITICAL**: No story implementation begins until this phase is complete.

- [X] T009 Validate and finalize the OpenAPI 3.1 source-of-truth contract in `specs/001-authenticated-upload/contracts/openapi.yaml`
- [ ] T010 Generate and commit Spring API interfaces/models from the contract in `backend/target/generated-sources/openapi/`
- [ ] T011 Generate and commit TypeScript fetch client/models from the contract in `frontend/src/api/`
- [X] T012 Implement Spring Security resource-server JWT configuration requiring authentication for every route in `backend/src/main/java/com/example/fileupload/config/SecurityConfig.java`
- [X] T013 Add JWT issuer, JWK Set, multipart limit, and upload-storage configuration in `backend/src/main/resources/application.yml`
- [X] T014 [P] Implement safe API error response mapping for authentication and validation failures in `backend/src/main/java/com/example/fileupload/api/ApiExceptionHandler.java`
- [X] T015 [P] Define upload storage abstraction and local filesystem implementation in `backend/src/main/java/com/example/fileupload/storage/UploadStorage.java` and `backend/src/main/java/com/example/fileupload/storage/LocalFileUploadStorage.java`
- [X] T016 [P] Configure the generated TypeScript client base URL and Bearer-token injection in `frontend/src/api/client.ts`
- [ ] T017 Verify contract generation stays current in `backend/pom.xml` and `frontend/package.json`

**Checkpoint**: Contract generation, universal JWT protection, bounded multipart processing, storage,
and standardized errors are ready.

---

## Phase 3: User Story 1 - Upload an approved file (Priority: P1) 🎯 MVP

**Goal**: An authenticated client can upload exactly one valid JPG/JPEG, PNG, or PDF of 10 MiB or
less and receive `file_id` and `access_url`.

**Independent Test**: Submit one valid allowed fixture and a valid JWT; receive HTTP 201 with
non-empty `file_id` and `access_url`, then confirm a file was stored.

### Tests for User Story 1

- [ ] T018 [P] [US1] Create OpenAPI contract test for the successful `POST /upload` response in `backend/src/test/java/com/example/fileupload/contract/UploadApiContractTest.java`
- [ ] T019 [P] [US1] Create backend integration test for valid JPEG, PNG, PDF, and exactly-10-MiB uploads in `backend/src/test/java/com/example/fileupload/integration/ValidUploadIntegrationTest.java`
- [ ] T020 [P] [US1] Create React upload success-state test using the generated client in `frontend/src/features/upload/UploadForm.test.tsx`

### Implementation for User Story 1

- [X] T021 [P] [US1] Create `UploadedFile` domain record and `UploadResult` response mapping in `backend/src/main/java/com/example/fileupload/domain/UploadedFile.java` and `backend/src/main/java/com/example/fileupload/domain/UploadResult.java`
- [X] T022 [P] [US1] Implement byte-based content-type detector with allowed JPEG, PNG, and PDF values in `backend/src/main/java/com/example/fileupload/service/ContentTypeDetector.java`
- [X] T023 [US1] Implement upload service that enforces exactly one non-empty allowed file, 10,485,760-byte maximum, atomic storage, opaque ID creation, and access URL construction in `backend/src/main/java/com/example/fileupload/service/UploadService.java`
- [ ] T024 [US1] Implement the generated upload API interface and map successful uploads to HTTP 201 in `backend/src/main/java/com/example/fileupload/api/UploadApiController.java`
- [X] T025 [US1] Implement the single-file selection and submit form using `frontend/src/api/` in `frontend/src/features/upload/UploadForm.tsx`
- [X] T026 [US1] Render the returned file ID and access URL in `frontend/src/features/upload/UploadResult.tsx`
- [X] T027 [US1] Compose the upload form into the application page in `frontend/src/App.tsx`
- [ ] T028 [US1] Run and correct the US1 contract, backend integration, and frontend success tests in `backend/src/test/java/com/example/fileupload/` and `frontend/src/features/upload/UploadForm.test.tsx`

**Checkpoint**: A valid authenticated upload completes end-to-end and returns the specified JSON.

---

## Phase 4: User Story 2 - Reject unauthenticated uploads (Priority: P2)

**Goal**: Missing, malformed, invalid, and expired JWT Bearer Tokens are rejected before file
processing and no file is stored.

**Independent Test**: Submit an otherwise valid file with no token and invalid/expired tokens;
each request returns HTTP 401 and leaves storage unchanged.

### Tests for User Story 2

- [ ] T029 [P] [US2] Add contract tests for documented HTTP 401 and `WWW-Authenticate` behavior in `backend/src/test/java/com/example/fileupload/contract/UploadAuthenticationContractTest.java`
- [ ] T030 [P] [US2] Add backend integration tests proving missing, malformed, invalid, and expired JWTs cannot persist files in `backend/src/test/java/com/example/fileupload/integration/UploadAuthenticationIntegrationTest.java`
- [ ] T031 [P] [US2] Add React tests for safe unauthenticated-upload error display in `frontend/src/features/upload/UploadForm.test.tsx`

### Implementation for User Story 2

- [ ] T032 [US2] Configure JWT validation failure handling to return the contract-safe HTTP 401 response in `backend/src/main/java/com/example/fileupload/config/SecurityConfig.java`
- [X] T033 [US2] Map HTTP 401 responses from the generated client to a safe UI message in `frontend/src/features/upload/uploadErrorMessage.ts`
- [X] T034 [US2] Integrate authentication failure display into the upload form in `frontend/src/features/upload/UploadForm.tsx`
- [ ] T035 [US2] Run and correct the US2 contract, backend integration, and frontend error tests in `backend/src/test/java/com/example/fileupload/` and `frontend/src/features/upload/UploadForm.test.tsx`

**Checkpoint**: Every unauthenticated upload is rejected before application persistence and the UI
does not report success.

---

## Phase 5: User Story 3 - Reject unsafe uploads (Priority: P3)

**Goal**: Authenticated clients receive documented errors for oversize, unsupported-type, empty,
and multiple-file requests; no rejected file is persisted.

**Independent Test**: With a valid JWT, submit an over-limit file, unsupported bytes with an
allowed-looking name, no file, and more than one file; verify the documented error and no storage.

### Tests for User Story 3

- [ ] T036 [P] [US3] Add contract tests for HTTP 400, 413, and 415 responses in `backend/src/test/java/com/example/fileupload/contract/UploadValidationContractTest.java`
- [ ] T037 [P] [US3] Add backend integration tests for oversized, spoofed-type, empty, and multiple-file requests in `backend/src/test/java/com/example/fileupload/integration/UploadValidationIntegrationTest.java`
- [ ] T038 [P] [US3] Add React tests for oversize and unsupported-type error display in `frontend/src/features/upload/UploadForm.test.tsx`

### Implementation for User Story 3

- [X] T039 [US3] Add oversize, missing/multiple-file, and unsupported-content exceptions with safe codes in `backend/src/main/java/com/example/fileupload/service/UploadValidationException.java`
- [X] T040 [US3] Map upload validation exceptions to the contract's HTTP 400, 413, and 415 responses in `backend/src/main/java/com/example/fileupload/api/ApiExceptionHandler.java`
- [X] T041 [US3] Add client-side preflight guidance for single-file, size, and allowed-extension selection in `frontend/src/features/upload/UploadForm.tsx`
- [X] T042 [US3] Extend safe error-message mapping for HTTP 400, 413, and 415 in `frontend/src/features/upload/uploadErrorMessage.ts`
- [ ] T043 [US3] Run and correct the US3 contract, backend integration, and frontend validation tests in `backend/src/test/java/com/example/fileupload/` and `frontend/src/features/upload/UploadForm.test.tsx`

**Checkpoint**: Unsafe uploads are consistently rejected from API to UI without persisted files.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final contract alignment, observability, and end-to-end verification.

- [ ] T044 [P] Add structured security-safe upload outcome logging without tokens or file content in `backend/src/main/java/com/example/fileupload/service/UploadService.java`
- [ ] T045 [P] Add an access-URL construction configuration test in `backend/src/test/java/com/example/fileupload/service/UploadServiceTest.java`
- [ ] T046 Verify OpenAPI Generator configuration and generated code have no stale differences in `backend/pom.xml` and `frontend/package.json`
- [ ] T047 Execute every quickstart scenario and record results in `specs/001-authenticated-upload/quickstart.md`
- [ ] T048 Review implementation against the OpenAPI 3.1 contract and constitution in `specs/001-authenticated-upload/contracts/openapi.yaml` and `.specify/memory/constitution.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- Phase 1 has no dependencies.
- Phase 2 depends on Phase 1 and blocks all stories.
- US1, US2, and US3 all depend on Phase 2; implement in priority order for incremental delivery.
- Phase 6 depends on the selected user-story scope being complete.

### User Story Dependencies

- **US1 (P1)**: Starts after Phase 2 and delivers the MVP.
- **US2 (P2)**: Starts after Phase 2; hardens the shared security configuration without depending on
  US1 completion.
- **US3 (P3)**: Starts after Phase 2; extends shared validation behavior and can proceed in parallel
  with US2, though priority delivery places it after US1 and US2.

### Parallel Opportunities

- T003, T004, T007, and T008 can run concurrently after their project roots exist.
- T014, T015, and T016 can run concurrently after the contract generation direction is established.
- Within each story, all `[P]` test tasks can be authored in parallel before implementation.
- T021 and T022 can run in parallel; US2 and US3 tests can run in parallel after Phase 2.

## Parallel Example: User Story 1

```text
Task: "Create contract success test in backend/src/test/java/com/example/fileupload/contract/UploadApiContractTest.java"
Task: "Create valid upload integration test in backend/src/test/java/com/example/fileupload/integration/ValidUploadIntegrationTest.java"
Task: "Create React success test in frontend/src/features/upload/UploadForm.test.tsx"
```

## Implementation Strategy

### MVP First

1. Complete Setup and Foundational phases.
2. Complete US1 and run T028.
3. Demo a valid authenticated upload and its returned `file_id` and `access_url`.

### Incremental Delivery

1. Add US2 to establish and demonstrate rejection before persistence.
2. Add US3 to enforce unsafe-upload rejection end to end.
3. Complete Phase 6 before release.
