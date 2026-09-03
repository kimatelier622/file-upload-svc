# Add Authentication Middleware to the Upload Service

## Problem/Feature Description

The platform team at MediaVault runs an internal HTTP file storage service used by several product teams to store images and documents. The service currently has no access controls — any caller can upload files. After a recent internal security review, the team has been asked to gate the upload endpoint behind an authentication layer before the next quarterly release.

The auth platform team issues short-lived, signed access tokens to registered service accounts. These tokens encode the caller's identity and have an expiry time built in. Your task is to write an authentication middleware module that integrates with the existing HTTP server framework and validates each incoming token before the request reaches the upload handler. The middleware must correctly handle the three main failure cases — a request with no credentials at all, a request carrying a token that is structurally wrong or has been tampered with, and a request carrying a token that was once valid but has since expired.

The security team has a strict policy: authentication failures must be logged so the SIEM can detect credential-stuffing or replay attacks, but the logs must never contain raw credential data. Dashboards and log archives are accessible to a broader set of employees, so leaking sensitive credential data into logs would violate the company's data-handling policy.

## Output Specification

Produce the following files:
- `middleware/auth.js` (or `.ts`, `.py`, or equivalent in your chosen language and framework): the authentication middleware implementation
- `middleware/README.md`: brief integration documentation that includes:
  - How to wire the middleware into an HTTP server
  - A concrete example showing the exact shape of an error response body returned to the caller on authentication failure
  - A concrete example showing what a failed-authentication log line looks like (so the security team can validate log format)

Do not implement the full file upload service — only the authentication middleware and its documentation. Any signing secret or configuration value can be hardcoded to a placeholder for this standalone module.
