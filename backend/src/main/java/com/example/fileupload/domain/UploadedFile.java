package com.example.fileupload.domain;

public record UploadedFile(String fileId, String detectedContentType, long byteSize, String accessUrl) { }
