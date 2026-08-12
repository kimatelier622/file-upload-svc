package com.example.fileupload.service;

import org.springframework.http.HttpStatus;

public class UploadValidationException extends RuntimeException {
  private final HttpStatus status;
  private final String code;
  public UploadValidationException(HttpStatus status, String code, String message) {
    super(message); this.status = status; this.code = code;
  }
  public HttpStatus status() { return status; }
  public String code() { return code; }
}
