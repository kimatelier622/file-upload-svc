# File Upload Service Enhancement: File Retrieval Endpoint

## Background

The Nexus Systems internal tooling team built a lightweight file upload service that several engineering squads rely on for sharing build artifacts, design assets, and documentation. The service currently supports a single operation: uploading a file. The existing API definition lives in `inputs/openapi.yaml` and the Express.js implementation is in `inputs/server.js`.

Over the past sprint, three separate teams filed the same complaint: once they upload a file, they have no way to retrieve a list of what's been stored. Every time they need to reference a previous upload they have to dig through their own logs. The product owner has prioritised adding a **file listing endpoint** — one that returns the metadata for all uploaded files — before the next release window closes.

Your job is to extend the service with this new capability. The team is strict about keeping their API specification and implementation in step with one another: any change to the API surface must be reflected consistently across both the spec and the code. Use the existing specification and implementation as your starting point.

## Output Specification

Produce the following files in your working directory:

- `openapi.yaml` — the updated API specification including the new endpoint
- `server.js` — the updated implementation that provides the new endpoint
- `CHANGES.md` — a brief log describing what you added, which files you modified, and the order in which you made those changes
