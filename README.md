# AI-Driven API Contract Definition Development New Model — Full Demo Notes

>This document records the complete SDD practice process for building an "authenticated file upload service" from scratch, consisting of four acts: Specification Definition → Contract-Driven → Engineering Circuit Breaker → Governance Metrics.

---

## Prerequisites

| Tool | Version Requirement | Installation Method |
|------|---------|----------|
| Python | 3.11+ | [python.org](https://python.org) |
| Git | 2.30+ | [git-scm.com](https://git-scm.com) |
| Node.js | 20+ (for Codex CLI) | `nvm install 20` or [nodejs.org](https://nodejs.org) |
| uv | Latest | `curl -LsSf https://astral.sh/uv/install.sh | sh` |
| specify-cli | Latest | `uv tool install specify-cli --from git+https://github.com/github/spec-kit.git` |
| Codex CLI | Latest | `npm install -g @openai/codex` |
| Schemathesis | Latest | `pipx install schemathesis` |
| oasdiff | 1.28+ | Download Windows binary or `go install github.com/oasdiff/oasdiff@latest` |
| Tessl CLI | Latest | `winget install tessl.tessl` |

> **Windows Tip**: After installation, ensure the paths of the above tools are added to the system environment variable `PATH` (the installer usually handles this automatically).

---

## OpenAPI Contract (Core Specification)

Throughout the demo, `openapi.yaml` is the **single source of truth**. Below is an example OpenAPI 3.1 specification for the "authenticated file upload service":

yaml
openapi: 3.1.0
info:
title: File Upload Service
version: 1.0.0
paths:
/upload:
post:
summary: Upload file
operationId: uploadFile
security:
▪ bearerAuth: []

      requestBody:
        required: true
        content:
          multipart/form-data:
            schema:
              type: object
              properties:
                file:
                  type: string
                  format: binary
                  maxLength: 10485760  # 10MB
                folder:
                  type: string
                  default: 'general'
              required:
                ▪ file

      responses:
        '201':
          description: Upload successful
          content:
            application/json:
              schema:
                type: object
                properties:
                  file_id:
                    type: string
                  access_url:
                    type: string
                    format: uri
                required:
                  ▪ file_id

                  ▪ access_url

        '401':
          description: Unauthenticated
        '413':
          description: File too large
        '415':
          description: Unsupported file type
components:
securitySchemes:
bearerAuth:
type: http
scheme: bearer
bearerFormat: JWT


This contract defines:
- **Authentication**: Bearer Token (JWT)
- **Request**: `multipart/form-data`, containing `file` (binary) and optional `folder`
- **Response**: Successful return of `file_id` and `access_url`
- **Error codes**: 401 (Unauthenticated), 413 (File too large), 415 (Type not supported)

---

## Act One: Spec Kit Five-Step Specification Generation

### Environment Setup

powershell
Install uv (Python package manager)

curl -LsSf https://astral.sh/uv/install.sh | sh

Install specify-cli (Spec Kit CLI)

uv tool install specify-cli --from git+https://github.com/github/spec-kit.git

Verify

specify --version


### Initialize Project (Codex Integration, Skills Mode)

```powershell
cd E:\journey\spec-driven
specify init file-upload-svc --integration codex --integration-options="--skills"
cd file-upload-svc
```


### Five-Step Specification Generation (Executed in Codex CLI)

```powershell
## Start Codex CLI

codex

## 1. Constitution (Immutable Principles)

$speckit-constitution Create principles for a secure file upload service:
• All endpoints require Bearer Token authentication
• Security: Max 10MB file size, only allow jpg/jpeg/png/pdf
• OpenAPI 3.1 is the single source of truth

## 2. Specify (User Stories & Acceptance Criteria)

$speckit-specify Build an authenticated file upload API:
• POST /upload: Accepts a single file with JWT in Authorization header
• Returns JSON: { "file_id": "...", "access_url": "..." }
• Validation: Reject files larger than 10MB or wrong mime type

## 3. Plan (Technical Solution + OpenAPI Contract)

$speckit-plan Use Spring Boot (backend) and React+TS (frontend).
Adopt OpenAPI Generator for scaffolding. The backend must use Spring Security for JWT validation.

## 4. Tasks (Atomic Task List)

$speckit-tasks

## 5. Implement (Generate Code by Task)

$speckit-implement
```

**Output files**:
- `constitution.md`
- `spec.md`
- `plan.md` (contains OpenAPI 3.1 contract)
- `tasks.md`
- Backend code (Spring Boot), Frontend code (React + TypeScript)

**SDD Quickstart**

```powershell
# Replace vX.Y.Z with the latest release tag, keeping the leading v.

uv tool install specify-cli --from git+https://github.com/github/spec-kit.git@vX.Y.Z
specify init my-project --integration copilot
cd my-project
Launch your coding agent in the project directory, then:

0. Establish your project principles once (/speckit-constitution). This is a one-time step per project.
1. Specify what you want to build (/speckit-specify).
2. Plan how you will build it (/speckit-plan).
3. Break down the plan into actionable tasks (/speckit-tasks).
4. Implement the tasks (/speckit-implement).
5. Converge the implementation against the spec, plan, and tasks (/speckit-converge).
```
>Note
>Repeat steps 4 and 5 until /speckit-converge reports Converged.

---

## Act Two: Contract-Driven — Testing & Drift Detection

### 1. Start Backend Service

Before running tests, ensure the backend service is started:

```powershell
# Start Spring Boot backend (assuming in backend directory)

cd backend
./mvnw spring-boot:run
# Service defaults to listening on http://localhost:8080
```


### 2. Schemathesis Contract Testing

```powershell
## Install Schemathesis

pipx install schemathesis

## Run contract test (specify backend address)

st run http://localhost:8080/openapi.yaml --checks all --max-examples 50

## Or use local OpenAPI file + specify base URL

st run specs/001-authenticated-upload/contracts/openapi.yaml --base-url http://localhost:8080 --checks all --max-examples 50
```

**Parameter explanation**:
- `--url` or `--base-url`: Specify backend service address (default reads from OpenAPI's `servers` field)
- `--checks all`: Enable all checks (including status code, response body, headers, etc.)
- `--max-examples 50`: Generate up to 50 test cases per endpoint

**Expected result**: All test cases pass, no 5xx errors.

### 3. oasdiff Drift Detection

#### Set Environment Variables (PowerShell)

- Temporarily add oasdiff.exe directory to PATH

```powershell
$env:Path += ";E:\journey\spec-driven\file-upload-svc\tools"
```

- Permanently add (requires admin privileges)

```powershell
[Environment]::SetEnvironmentVariable("Path", $env:Path + ";E:\journey\spec-driven\file-upload-svc\tools", [EnvironmentVariableTarget]::User)
```

#### Run Drift Detection

###### Prepare baseline and modified files

Copy-Item specs/001-authenticated-upload/contracts/openapi.yaml openapi-baseline.yaml
Copy-Item specs/001-authenticated-upload/contracts/openapi.yaml openapi-modified.yaml

Intentionally break: change file_id type from string to integer (edit openapi-modified.yaml with editor)

###### Run breaking change detection

```shell
oasdiff breaking openapi-baseline.yaml openapi-modified.yaml
```

###### Check exit code (0 = no breaking, 1 = breaking)
```shell
echo $LASTEXITCODE
```

**Expected output**: Lists breaking changes, e.g., `modified type: string -> integer (breaking)`.

---

## Act Three: Git Engineering — PR Circuit Breaker & Branch Protection

### 1. Create GitHub Repository and Push

```shell
git init
git add .
git commit -m "chore: initial spec-driven file upload service"
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/file-upload-svc.git
git push -u origin main
```


### 2. Configure CI Workflow

Create `.github/workflows/oasdiff.yaml`:

```yaml
name: OpenAPI Breaking Change Check

on:
  pull_request:
    branches: [ "main" ]
    paths:
      - 'specs/**/contracts/openapi.yaml'

jobs:
  oasdiff:
    runs-on: ubuntu-latest
    permissions:
      contents: read
      pull-requests: write
    steps:
      - uses: actions/checkout@v4
        with:
          fetch-depth: 0   # 必须：完整历史，否则取不到 origin/main 的引用

      - name: Fetch base branch
        run: git fetch --depth=1 origin ${{ github.base_ref }}

      - name: Run oasdiff breaking check
        uses: oasdiff/oasdiff-action/breaking@v0
        with:
          base: 'origin/${{ github.base_ref }}:specs/001-authenticated-upload/contracts/openapi.yaml'
          revision: 'HEAD:specs/001-authenticated-upload/contracts/openapi.yaml'
          fail-on: WARN
        env:
          GITHUB_TOKEN: ${{ github.token }}
```

Push to main branch.

### 3. Demonstrate Breaking Change Being Blocked

```powershell
git checkout -b feat/break-contract
## Modify openapi.yaml: change file_id type to integer

git add .
git commit -m "feat: break contract (intentional)"
git push -u origin feat/break-contract
```

Create PR → main on GitHub, observe Actions run → `breaking-changes` job fails (red ❌), Merge button becomes gray.

### 4. Demonstrate Legitimate Change Passing

```powershell
git checkout main
git checkout -b feat/add-field
# Add an optional field in openapi.yaml (non-breaking)

git add .
git commit -m "feat: add optional field"
git push -u origin feat/add-field
```

Create PR, Actions passes (green ✅), Merge button available.

### 5. Configure Branch Protection Rules (Optional)

In GitHub repository Settings → Branches → Add rule:
- Branch name pattern: `main`
- ✅ Require a pull request before merging
- ✅ Require status checks to pass before merging → search for `breaking-changes` and select it
- ✅ Include administrators
- Save changes

---

## Act Four: Tessl Governance — Publishing Plugin & Evaluation

### 1. Install Tessl CLI

```powershell
# Uninstall old npm version if any

npm uninstall -g @tessl/cli

# Install using winget (recommended)

winget install tessl.tessl

# Verify

tessl --help
```

### 2. Initialize and Login

```powershell
cd E:\journey\spec-driven\file-upload-svc
tessl init
tessl login
tessl whoami
```

### 3. Create Workspace

```powershell
tessl workspace create kimatelier622
tessl workspace list
```

### 4. Install Official SDD Skill

```powershell
tessl install kevin-ryan-io/spec-driven-development
```

### 5. Package Local Specification as Plugin

```powershell
## Create plugin skeleton (already under plugins/file-upload-svc)

## Edit plugin.json and SKILL.md (optional)

## Validate structure

tessl skill lint ./plugins/file-upload-svc/skills/file-upload-svc

## Quality review

tessl skill review ./plugins/file-upload-svc

## Generate evaluation scenarios

tessl eval generate ./plugins/file-upload-svc

## Run evaluation

tessl eval run ./plugins/file-upload-svc

## Publish to personal workspace

tessl skill publish ./plugins/file-upload-svc --workspace kimatelier622
```

**Successful publish output**:


√ Published kimatelier622/file-upload-svc@0.1.0 to https://tessl.io/registry/kimatelier622/file-upload-svc/0.1.0


### 6. View Registry (Optional)

Visit `https://tessl.io/registry/kimatelier622/file-upload-svc/0.1.0` (may take a few minutes to sync).

---

## Appendix: Complete Command Quick Reference Table

| Stage | Command | Purpose |
|-------|---------|---------|
| Act One | `specify init ...` | Initialize Spec Kit project |
| Act One | `$speckit-constitution` | Generate constitution |
| Act One | `$speckit-specify` | Generate specification |
| Act One | `$speckit-plan` | Generate plan (includes OpenAPI) |
| Act One | `$speckit-tasks` | Generate tasks |
| Act One | `$speckit-implement` | AI implement code |
| Act Two | `st run --base-url http://localhost:8080 openapi.yaml` | Schemathesis contract testing |
| Act Two | `oasdiff breaking base.yaml rev.yaml` | Local drift detection |
| Act Three | `.github/workflows/oasdiff.yaml` | CI circuit breaker workflow |
| Act Three | GitHub branch protection rules | Prevent direct pushes to main |
| Act Four | `tessl skill lint` | Validate plugin structure |
| Act Four | `tessl skill review` | Quality review |
| Act Four | `tessl eval generate/run` | Generate/run evaluations |
| Act Four | `tessl skill publish` | Publish to registry |

---

---
### Full Tessl Chain
```shell
# ========== 1. Installation and Login ==========
winget install tessl.tessl --source winget
tessl login
tessl whoami

# ========== 2. Workspace ==========
tessl workspace create myteam
tessl workspace list

# ========== 3. Project init ==========
cd your-repo
tessl init --agent claude-code

# ========== 4. Install Official SDD Skill ==========
tessl install kevin-ryan-io/spec-driven-development
# 或者 tessl install tessl-labs/spec-driven-development

# ========== 5. Plugin-first Skeleton ==========
tessl plugin new `
  --name myteam/file-upload-svc `
  --summary "文件上传服务规范与流程" `
  --workspace myteam `
  --skill `
  --skill-name file-upload-svc `
  --skill-description "When adding or modifying file upload functionality" `
  --path ./plugins/file-upload-svc

# ========== 6. Write SKILL.md ==========
# Edit ./plugins/file-upload-svc/skills/file-upload-svc/SKILL.md

# ========== 7. Quality Assurance ==========
tessl plugin lint ./plugins/file-upload-svc
tessl review run ./plugins/file-upload-svc --workspace myteam
tessl review fix ./plugins/file-upload-svc --workspace myteam   # 分数低时自动修复
tessl eval ./plugins/file-upload-svc/skills/file-upload-svc    # 发布前证明有效性

# ========== 8. Publish ==========
tessl plugin publish ./plugins/file-upload-svc --workspace myteam

# ========== 9. Future Updates ==========
tessl plugin publish ./plugins/file-upload-svc --workspace myteam --bump patch

# ========== 10. Installing teammate ==========
tessl install myteam/file-upload-svc
```
---

> **Summary**: From Spec Kit defining specifications → Codex implementing according to rules → Schemathesis verifying → oasdiff circuit breaking → Tessl governance metrics, forming a complete "contract-driven development" closed loop. Specifications are no longer documents, but executable, testable, and governable intelligent assets.
