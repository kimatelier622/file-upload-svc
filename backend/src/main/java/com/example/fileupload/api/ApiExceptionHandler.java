package com.example.fileupload.api;

import com.example.fileupload.service.UploadValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(UploadValidationException.class)
  ResponseEntity<ErrorResponse> uploadValidation(UploadValidationException ex) {
    return ResponseEntity.status(ex.status()).body(new ErrorResponse(ex.code(), ex.getMessage()));
  }
}
