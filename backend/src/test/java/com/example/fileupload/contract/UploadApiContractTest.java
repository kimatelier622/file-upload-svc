package com.example.fileupload.contract;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class UploadApiContractTest {
  @Test void contractDefinesAuthenticatedUploadResponse() throws Exception {
    String contract = Files.readString(Path.of("../specs/001-authenticated-upload/contracts/openapi.yaml"));
    assertTrue(contract.contains("openapi: 3.1.0"));
    assertTrue(contract.contains("/upload:"));
    assertTrue(contract.contains("bearerAuth:"));
    assertTrue(contract.contains("file_id"));
    assertTrue(contract.contains("access_url"));
  }
}
