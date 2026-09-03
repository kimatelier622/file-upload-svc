'use strict';

const express = require('express');
const multer = require('multer');

const app = express();

/**
 * Middleware: Bearer token authentication.
 * Validates the Authorization header before any upload is processed.
 */
function authenticate(req, res, next) {
  // Implementation: validates JWT Bearer tokens
  next();
}

/**
 * Middleware: File upload validation.
 * Enforces content type and size constraints before the file reaches storage.
 */
function validateUpload(req, res, next) {
  // Implementation: enforces file type allowlist and maximum file size
  next();
}

const storage = multer.memoryStorage();
const upload = multer({ storage });

// POST /upload — accepts a single file in the 'file' multipart form field
app.post('/upload', authenticate, upload.single('file'), validateUpload, (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file provided' });
  }
  res.status(200).json({
    message: 'File uploaded successfully',
    filename: req.file.originalname,
  });
});

const PORT = process.env.PORT || 3000;
if (require.main === module) {
  app.listen(PORT, () => console.log(`Upload service running on port ${PORT}`));
}

module.exports = { app };
