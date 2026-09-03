# Security Audit: Write a Test Suite for the Document Upload Service

## Problem/Feature Description

Meridian Capital is a mid-size lending platform that collects supporting documents from borrowers during the loan application process. Loan officers and applicants upload materials such as proof of income, identification, and financial statements through a REST API endpoint (`POST /upload`). The service has been developed by a small backend team and includes middleware layers to enforce several protective controls during upload processing.

Before the service is promoted to production, the security engineering lead has asked for a professional-grade automated test suite. The team needs confidence that the upload endpoint's security controls behave correctly — both accepting legitimate requests and rejecting invalid or potentially malicious ones.

The service source is available in the `inputs/` directory. It is a Node.js/Express application exposing the upload endpoint. The middleware pipeline is in place but internal implementation details are intentionally omitted from the source — your tests should reflect the expected security specification of the service, not what the current stub does.

## Output Specification

Write your test suite to `tests/upload.test.js`. You may add supporting utility files under `tests/` as needed.

Use any Node.js test framework you prefer (Jest, Mocha, etc.). The `supertest` library is available for making HTTP assertions against the Express app.

Install any required packages as needed. Once finished, the `tests/` directory should be self-contained and reviewable by a grader without running a live server.
