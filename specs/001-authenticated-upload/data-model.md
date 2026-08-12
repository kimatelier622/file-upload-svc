# Data Model: Authenticated File Upload

## Upload request

| Field | Description | Validation |
| --- | --- | --- |
| Authorization | JWT supplied with the Bearer scheme | Required and valid before file processing |
| file | One submitted binary file | Required, exactly one, non-empty, and at most 10,485,760 bytes |

## Uploaded file

| Field | Description | Validation / lifecycle |
| --- | --- | --- |
| fileId | Server-generated opaque unique identifier | Created only after authentication and validation pass |
| detectedContentType | Type derived from file bytes | `image/jpeg`, `image/png`, or `application/pdf` |
| byteSize | Number of content bytes | 1 through 10,485,760 inclusive |
| accessUrl | URL returned to identify the accepted file | Non-empty; created atomically with its reference |
| storedContent | Accepted file content | Written only after all validations pass |

## Validation result

| Outcome | Trigger | Persistence effect |
| --- | --- | --- |
| Accepted | Valid JWT and one allowed file within limit | Create Uploaded File and return its reference |
| Unauthenticated | Missing, malformed, expired, or invalid JWT | No file is written |
| Invalid request | No file or more than one file | No file is written |
| Unsupported type | Detected content type is not allowed | No file is written |
| Oversize | File exceeds 10,485,760 bytes | No file is written |
| Storage failure | Valid file cannot be made available | No incomplete file is accessible |

## State transitions

`Received` → `Authenticated` → `Validated` → `Stored` → `Reference returned`

Any rejection transitions directly to `Rejected` and must not reach `Stored`.
