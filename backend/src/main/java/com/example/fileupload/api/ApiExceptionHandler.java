package com.example.fileupload.api;

import com.example.fileupload.service.UploadValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(UploadValidationException.class)
  ResponseEntity<ErrorResponse> uploadValidation(UploadValidationException ex) {
    return ResponseEntity.status(ex.status()).body(new ErrorResponse(ex.code(), ex.getMessage()));
  }

  /**
   * Spring MVC reaches this handler after a request has passed authentication but no controller
   * method accepts its HTTP method. Returning 405 here prevents it from being translated into a
   * generic 400 response by the default error handling path.
   */
  @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
  ResponseEntity<ErrorResponse> methodNotAllowed(HttpRequestMethodNotSupportedException ex) {
    HttpHeaders headers = new HttpHeaders();
    headers.setAllow(ex.getSupportedHttpMethods());
    return new ResponseEntity<>(new ErrorResponse("METHOD_NOT_ALLOWED", "Method not allowed."),
        headers, HttpStatus.METHOD_NOT_ALLOWED);
  }

  /** Return a JSON 404 only for paths that do not exist; valid paths with invalid methods use 405. */
  @ExceptionHandler(NoHandlerFoundException.class)
  ResponseEntity<ErrorResponse> noHandler(NoHandlerFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ErrorResponse("NOT_FOUND", "Resource not found."));
  }
}
