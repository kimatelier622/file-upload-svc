# AI 驱动 API 契约定义开发新模式 — 完整 Demo 笔记

本文档记录了从零搭建“带鉴权的文件上传服务”的完整 SDD 实践过程，共四幕：规范定义 → 契约驱动 → 工程化熔断 → 治理度量。

---

## 前置环境要求

| 工具 | 版本要求 | 安装方式 |
|------|---------|----------|
| Python | 3.11+ | [python.org](https://python.org) |
| Git | 2.30+ | [git-scm.com](https://git-scm.com) |
| Node.js | 20+（用于 Codex CLI） | `nvm install 20` 或 [nodejs.org](https://nodejs.org) |
| uv | 最新 | `curl -LsSf https://astral.sh/uv/install.sh | sh` |
| specify-cli | 最新 | `uv tool install specify-cli --from git+https://github.com/github/spec-kit.git` |
| Codex CLI | 最新 | `npm install -g @openai/codex` |
| Schemathesis | 最新 | `pipx install schemathesis` |
| oasdiff | 1.28+ | 下载 Windows 二进制或 `go install github.com/oasdiff/oasdiff@latest` |
| Tessl CLI | 最新 | `winget install tessl.tessl` |

> **Windows 提示**：安装后请确保上述工具的路径已添加到系统环境变量 `PATH` 中（通常安装程序会自动处理）。

---

## OpenAPI 契约（核心规范）

在整个 Demo 中，`openapi.yaml` 是**唯一的事实源**。以下是“带鉴权的文件上传服务”的 OpenAPI 3.1 规范示例：

yaml
openapi: 3.1.0
info:
title: File Upload Service
version: 1.0.0
paths:
/upload:
post:
summary: 上传文件
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
          description: 上传成功
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
          description: 未认证
        '413':
          description: 文件过大
        '415':
          description: 不支持的文件类型
components:
securitySchemes:
bearerAuth:
type: http
scheme: bearer
bearerFormat: JWT


该契约定义了：
- **鉴权**：Bearer Token（JWT）
- **请求**：`multipart/form-data`，包含 `file`（二进制）和可选 `folder`
- **响应**：成功返回 `file_id` 和 `access_url`
- **错误码**：401（未认证）、413（文件过大）、415（类型不支持）

---

## 第一幕：Spec Kit 五步生成规范

### 环境准备

powershell
安装 uv（Python 包管理器）

curl -LsSf https://astral.sh/uv/install.sh | sh

安装 specify-cli（Spec Kit CLI）

uv tool install specify-cli --from git+https://github.com/github/spec-kit.git

验证

specify --version


### 初始化项目（Codex 集成，Skills 模式）

powershell
cd E:\journey\spec-driven
specify init file-upload-svc --integration codex --integration-options="--skills"
cd file-upload-svc


### 五步规范生成（在 Codex CLI 中执行）

powershell
启动 Codex CLI

codex

1. 宪法（不可变原则）

$speckit-constitution Create principles for a secure file upload service:
• All endpoints require Bearer Token authentication

• Security: Max 10MB file size, only allow jpg/jpeg/png/pdf

• OpenAPI 3.1 is the single source of truth

2. 规格（用户故事与验收标准）

$speckit-specify Build an authenticated file upload API:
• POST /upload: Accepts a single file with JWT in Authorization header

• Returns JSON: { "file_id": "...", "access_url": "..." }

• Validation: Reject files larger than 10MB or wrong mime type

3. 计划（技术方案 + OpenAPI 契约）

$speckit-plan Use Spring Boot (backend) and React+TS (frontend).
Adopt OpenAPI Generator for scaffolding. The backend must use Spring Security for JWT validation.

4. 任务（原子化任务清单）

$speckit-tasks

5. 实现（按任务生成代码）

$speckit-implement


**产出文件**：
- `constitution.md`
- `spec.md`
- `plan.md`（内含 OpenAPI 3.1 契约）
- `tasks.md`
- 后端代码（Spring Boot）、前端代码（React + TypeScript）

---

## 第二幕：契约驱动 — 测试与漂移检测

### 1. 启动后端服务

在运行测试前，确保后端服务已启动：

powershell
启动 Spring Boot 后端（假设在 backend 目录）

cd backend
./mvnw spring-boot:run
服务默认监听 http://localhost:8080



### 2. Schemathesis 契约测试

powershell
安装 Schemathesis

pipx install schemathesis

运行契约测试（指定后端地址）

st run http://localhost:8080/openapi.yaml --checks all --max-examples 50

或者直接使用本地 OpenAPI 文件 + 指定 base URL

st run specs/001-authenticated-upload/contracts/openapi.yaml --base-url http://localhost:8080 --checks all --max-examples 50


**参数说明**：
- `--url` 或 `--base-url`：指定后端服务地址（默认为从 OpenAPI 的 `servers` 字段读取）
- `--checks all`：启用所有检查（包括状态码、响应体、Header 等）
- `--max-examples 50`：每个端点最多生成 50 个测试用例

**预期结果**：所有测试用例通过，无 5xx 错误。

### 3. oasdiff 漂移检测

#### 设置环境变量（PowerShell）

powershell
将 oasdiff.exe 所在目录添加到 PATH（临时）

$env:Path += ";E:\journey\spec-driven\file-upload-svc\tools"

永久添加（需要管理员权限）

[Environment]::SetEnvironmentVariable("Path", $env:Path + ";E:\journey\spec-driven\file-upload-svc\tools", [EnvironmentVariableTarget]::User)


#### 运行漂移检测

powershell
准备基线文件与修改文件

Copy-Item specs/001-authenticated-upload/contracts/openapi.yaml openapi-baseline.yaml
Copy-Item specs/001-authenticated-upload/contracts/openapi.yaml openapi-modified.yaml

故意破坏：将 file_id 的 type 从 string 改为 integer（用编辑器修改 openapi-modified.yaml）

运行破坏性变更检测

oasdiff breaking openapi-baseline.yaml openapi-modified.yaml

检查退出码（0=无破坏，1=有破坏）

echo $LASTEXITCODE


**预期输出**：列出破坏性变更，如 `modified type: string -> integer (breaking)`。

---

## 第三幕：Git 工程化 — PR 熔断与分支保护

### 1. 创建 GitHub 仓库并推送

powershell
git init
git add .
git commit -m "chore: initial spec-driven file upload service"
git branch -M main
git remote add origin https://github.com/<YOUR_USERNAME>/file-upload-svc.git
git push -u origin main


### 2. 配置 CI 工作流

创建 `.github/workflows/oasdiff.yaml`：

yaml
name: OpenAPI Breaking Change Check

on:
pull_request:
branches: [ "main" ]
paths:
◦ 'specs//contracts/openapi.yaml'

jobs:
breaking-changes:
runs-on: ubuntu-latest
permissions:
contents: read
pull-requests: write
steps:
◦ uses: actions/checkout@v4

        with:
          fetch-depth: 0
      ◦ name: Fetch base branch

        run: git fetch --depth=1 origin ${{ github.base_ref }}
      ◦ name: Run oasdiff breaking check

        uses: oasdiff/oasdiff-action/breaking@v0
        with:
          base: 'origin/${{ github.base_ref }}:specs/001-authenticated-upload/contracts/openapi.yaml'
          revision: 'HEAD:specs/001-authenticated-upload/contracts/openapi.yaml'
          fail-on: WARN
        env:
          GITHUB_TOKEN: ${{ github.token }}


推送至 main 分支。

### 3. 演示破坏性变更被拦截

powershell
git checkout -b feat/break-contract
修改 openapi.yaml 中 file_id 的 type 为 integer

git add .
git commit -m "feat: break contract (intentional)"
git push -u origin feat/break-contract


在 GitHub 创建 PR → main，观察 Actions 运行 → `breaking-changes` job 失败（红色❌），Merge 按钮变灰。

### 4. 演示合法变更通过

powershell
git checkout main
git checkout -b feat/add-field
在 openapi.yaml 中新增一个可选字段（非破坏性）

git add .
git commit -m "feat: add optional field"
git push -u origin feat/add-field


创建 PR，Actions 通过（绿色✅），Merge 按钮可用。

### 5. 配置分支保护规则（可选）

在 GitHub 仓库 Settings → Branches → Add rule：
- Branch name pattern: `main`
- ✅ Require a pull request before merging
- ✅ Require status checks to pass before merging → 搜索 `breaking-changes` 并选中
- ✅ Include administrators
- Save changes

---

## 第四幕：Tessl 治理 — 发布 plugin 与评估

### 1. 安装 Tessl CLI

powershell
卸载 npm 旧版本（如有）

npm uninstall -g @tessl/cli

使用 winget 安装（推荐）

winget install tessl.tessl

验证

tessl --help


### 2. 初始化与登录

powershell
cd E:\journey\spec-driven\file-upload-svc
tessl init
tessl login
tessl whoami


### 3. 创建 Workspace

powershell
tessl workspace create kimatelier622
tessl workspace list


### 4. 安装官方 SDD Skill

powershell
tessl install kevin-ryan-io/spec-driven-development


### 5. 将本地规范封装为 Plugin

powershell
创建 plugin 骨架（已在 plugins/file-upload-svc 下）

编辑 plugin.json 和 SKILL.md（可选）

校验结构

tessl skill lint ./plugins/file-upload-svc

质量审查

tessl skill review ./plugins/file-upload-svc

生成评估场景

tessl eval generate ./plugins/file-upload-svc

运行评估

tessl eval run ./plugins/file-upload-svc

发布到个人 workspace

tessl skill publish ./plugins/file-upload-svc --workspace kimatelier622


**发布成功输出**：

√ Published kimatelier622/file-upload-svc@0.1.0 to https://tessl.io/registry/kimatelier622/file-upload-svc/0.1.0


### 6. 查看注册表（可选）

访问 `https://tessl.io/registry/kimatelier622/file-upload-svc/0.1.0`（需等待几分钟同步）。

---

## 附录：完整命令速查表

| 阶段 | 命令 | 作用 |
|------|------|------|
| 第一幕 | `specify init ...` | 初始化 Spec Kit 项目 |
| 第一幕 | `$speckit-constitution` | 生成宪法 |
| 第一幕 | `$speckit-specify` | 生成规格 |
| 第一幕 | `$speckit-plan` | 生成计划（含 OpenAPI） |
| 第一幕 | `$speckit-tasks` | 生成任务 |
| 第一幕 | `$speckit-implement` | AI 实现代码 |
| 第二幕 | `st run --base-url http://localhost:8080 openapi.yaml` | Schemathesis 契约测试 |
| 第二幕 | `oasdiff breaking base.yaml rev.yaml` | 本地漂移检测 |
| 第三幕 | `.github/workflows/oasdiff.yaml` | CI 熔断工作流 |
| 第三幕 | GitHub 分支保护规则 | 禁止直接推 main |
| 第四幕 | `tessl skill lint` | 校验 plugin 结构 |
| 第四幕 | `tessl skill review` | 质量审查 |
| 第四幕 | `tessl eval generate/run` | 生成/运行评估 |
| 第四幕 | `tessl skill publish` | 发布到注册表 |

---

> **总结**：从 Spec Kit 定义规范 → Codex 按规实现 → Schemathesis 验证 → oasdiff 熔断 → Tessl 治理度量，形成完整的“契约驱动开发”闭环。规范不再是文档，而是可执行、可测试、可治理的智能资产。