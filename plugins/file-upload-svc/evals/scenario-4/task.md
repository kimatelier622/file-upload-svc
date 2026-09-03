# Build a File Validation Module for a Document Ingestion Service

## Problem Description

A growing SaaS platform operates an internal document ingestion service that allows partner integrations to submit user-generated files (photos, scanned forms, and reports) through a REST API. Files land at an ingest endpoint and are eventually handed off to a storage layer — but several incidents have exposed a gap in the pipeline: files are being accepted and written to storage before any meaningful validation occurs. This has caused oversized uploads to exhaust disk quotas and unexpected file formats to corrupt downstream processing.

The engineering team needs a standalone **file validation module** — a function or class that can be dropped into the ingest pipeline and run *before* any storage operation is triggered. The module must inspect each incoming file for both type acceptability and size compliance, and communicate the outcome via standard HTTP status codes so the calling endpoint can respond appropriately to clients.

To help future maintainers understand the design, include a short `DESIGN.md` that documents the validation approach: what types are accepted, what the size threshold is, and how the type-checking mechanism works.

## Output Specification

Produce the following files:

- **`validator.py`** (or `validator.js` / `validator.ts` if you prefer Node) — the validation module containing at least one callable entry point (e.g. `validate_file(content: bytes, filename: str) -> ValidationResult`). The module must be self-contained and importable/requireable without a running server.
- **`DESIGN.md`** — a brief document (bullet points are fine) explaining what file types the validator accepts, what the size threshold is, how type detection works, and why that approach was chosen.

Do not start a web server or create a full API implementation — the validation logic alone is what's needed. The module will be integrated into the existing endpoint by another team member.
