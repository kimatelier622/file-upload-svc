'use strict';

const express = require('express');
const multer = require('multer');
const { v4: uuidv4 } = require('uuid');

const app = express();

const ALLOWED_MIME_TYPES = new Set(['image/jpeg', 'image/png', 'application/pdf']);
const MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MiB

const upload = multer({
  storage: multer.memoryStorage(),
  limits: { fileSize: MAX_FILE_SIZE },
  fileFilter(_req, file, cb) {
    if (ALLOWED_MIME_TYPES.has(file.mimetype)) {
      cb(null, true);
    } else {
      cb(Object.assign(new Error('Unsupported file type'), { status: 415 }));
    }
  },
});

// In-memory file store (demonstration only — not persisted across restarts)
const fileStore = [];

function requireBearer(req, res, next) {
  const auth = req.headers['authorization'] || '';
  if (!auth.startsWith('Bearer ')) {
    return res.status(401).json({ error: 'Unauthorized' });
  }
  // In production: validate JWT signature and expiry here.
  next();
}

// POST /upload — upload a single file
app.post('/upload', requireBearer, upload.single('file'), (req, res) => {
  if (!req.file) {
    return res.status(400).json({ error: 'No file provided' });
  }

  const record = {
    fileId: uuidv4(),
    filename: req.file.originalname,
    size: req.file.size,
    uploadedAt: new Date().toISOString(),
  };
  fileStore.push(record);

  res.status(200).json(record);
});

// Multer / file-validation error handler
app.use((err, _req, res, _next) => {
  if (err.status === 415) {
    return res.status(415).json({ error: 'Unsupported media type' });
  }
  if (err.code === 'LIMIT_FILE_SIZE') {
    return res.status(413).json({ error: 'File too large' });
  }
  res.status(500).json({ error: 'Internal server error' });
});

const PORT = process.env.PORT || 3000;
if (require.main === module) {
  app.listen(PORT, () => {
    console.log(`File upload service listening on port ${PORT}`);
  });
}

module.exports = { app, fileStore };
