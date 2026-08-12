package com.example.fileupload.service;

import com.example.fileupload.domain.UploadResult;
import com.example.fileupload.storage.UploadStorage;
import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadService {
  public static final long MAX_BYTES = 10_485_760L;
  private final ContentTypeDetector detector;
  private final UploadStorage storage;
  private final String publicBaseUrl;
  public UploadService(ContentTypeDetector detector, UploadStorage storage,
      @Value("${app.upload.public-base-url}") String publicBaseUrl) {
    this.detector = detector; this.storage = storage; this.publicBaseUrl = publicBaseUrl;
  }
  public UploadResult upload(MultipartFile file) {
    if (file == null || file.isEmpty()) throw invalid(HttpStatus.BAD_REQUEST, "invalid_upload", "Exactly one non-empty file is required.");
    if (file.getSize() > MAX_BYTES) throw invalid(HttpStatus.PAYLOAD_TOO_LARGE, "file_too_large", "The file exceeds the 10 MiB limit.");
    try {
      byte[] bytes = file.getBytes();
      String detected = detector.detect(new ByteArrayInputStream(bytes));
      if (!detector.isAllowed(detected)) throw invalid(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "unsupported_media_type", "Only JPEG, PNG, and PDF files are allowed.");
      String id = UUID.randomUUID().toString();
      storage.store(id, new ByteArrayInputStream(bytes));
      return new UploadResult(id, URI.create(publicBaseUrl.endsWith("/") ? publicBaseUrl + id : publicBaseUrl + "/" + id).toString());
    } catch (UploadValidationException exception) { throw exception;
    } catch (Exception exception) { throw new UploadValidationException(HttpStatus.INTERNAL_SERVER_ERROR, "upload_unavailable", "The upload could not be made available."); }
  }
  private UploadValidationException invalid(HttpStatus status, String code, String message) { return new UploadValidationException(status, code, message); }
}
