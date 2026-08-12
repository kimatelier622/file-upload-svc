package com.example.fileupload.api;

import com.example.fileupload.domain.UploadResult;
import com.example.fileupload.service.UploadService;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class UploadApiController {
  private final UploadService uploadService;
  public UploadApiController(UploadService uploadService) { this.uploadService = uploadService; }
  @PostMapping(path = "/upload", consumes = "multipart/form-data")
  @org.springframework.web.bind.annotation.ResponseStatus(HttpStatus.CREATED)
  Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
    UploadResult result = uploadService.upload(file);
    return Map.of("file_id", result.fileId(), "access_url", result.accessUrl());
  }
}
