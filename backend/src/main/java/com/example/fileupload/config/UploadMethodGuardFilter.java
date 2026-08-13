package com.example.fileupload.config;

import com.example.fileupload.api.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Stops unsupported upload methods before Spring's multipart resolver can inspect their body.
 * This filter is deliberately placed after authorization: no-token requests still receive 401.
 */
@Component
public class UploadMethodGuardFilter extends OncePerRequestFilter {
  private static final String UPLOAD_PATH = "/api/upload";
  private final ObjectMapper objectMapper;

  public UploadMethodGuardFilter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    if (UPLOAD_PATH.equals(request.getRequestURI()) && !"POST".equals(request.getMethod())) {
      response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      response.setHeader(HttpHeaders.ALLOW, "POST");
      objectMapper.writeValue(response.getOutputStream(),
          new ErrorResponse("METHOD_NOT_ALLOWED", "Method not allowed."));
      return;
    }
    filterChain.doFilter(request, response);
  }
}
