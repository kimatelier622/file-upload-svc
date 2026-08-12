package com.example.fileupload.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.fileupload.storage.UploadStorage;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

class UploadServiceTest {
  private final UploadStorage storage = (id, content) -> { };
  private final ContentTypeDetector detector = new ContentTypeDetector();
  private final UploadService service = new UploadService(detector, storage, "http://localhost:8080/api/files");

  @Test void rejectsOversizeFile() {
    MockMultipartFile file = new MockMultipartFile("file", "large.pdf", "application/pdf", new byte[(int) UploadService.MAX_BYTES + 1]);
    UploadValidationException ex = assertThrows(UploadValidationException.class, () -> service.upload(file));
    assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, ex.status());
  }

  @Test void rejectsUnsupportedBytesRegardlessOfExtension() {
    MockMultipartFile file = new MockMultipartFile("file", "spoofed.pdf", "application/pdf", "not a pdf".getBytes());
    UploadValidationException ex = assertThrows(UploadValidationException.class, () -> service.upload(file));
    assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.status());
  }
}
