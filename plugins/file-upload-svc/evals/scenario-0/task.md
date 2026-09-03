# File Upload Microservice

## Problem/Feature Description

Meridian Labs is building a cloud-based document management platform where field teams can submit photos and reports from remote sites. The engineering team needs a simple HTTP microservice that accepts file uploads from authenticated users. The service will sit behind a reverse proxy and only trusted clients with valid credentials should be able to submit files — unauthenticated requests must be turned away cleanly.

The platform only deals with images and PDF documents; the service should reject anything else. To prevent abuse and keep storage costs predictable, the service must also enforce a ceiling on how large an individual upload can be. The product team wants well-documented APIs so that mobile and web clients can integrate without back-and-forth with the backend team.

## Output Specification

Implement a minimal but production-ready file upload service. You may choose any programming language and framework you are comfortable with.

Produce:

- An API specification file (`openapi.yaml` or `openapi.json`) describing the upload endpoint, its authentication requirements, accepted inputs, and all relevant error responses.
- Service implementation source code (e.g. `server.js`, `app.py`, `main.go`, or equivalent) that implements the upload endpoint described in the spec.
- A `README.md` explaining how to run the service locally and how clients should authenticate and send files.

The service does not need to be running at submission time — the source files alone are sufficient. Keep the scope minimal: a single upload endpoint is all that is required. Avoid leaving any large downloaded files or build artifacts in the working directory when you are done.
