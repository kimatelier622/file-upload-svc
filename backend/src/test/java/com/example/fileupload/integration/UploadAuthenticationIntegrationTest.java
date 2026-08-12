package com.example.fileupload.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UploadAuthenticationIntegrationTest {
  @Autowired MockMvc mvc;
  @Test void rejectsUploadWithoutBearerToken() throws Exception {
    mvc.perform(multipart("/api/upload").file(new MockMultipartFile("file", "x.pdf", "application/pdf", "%PDF".getBytes())))
        .andExpect(status().isUnauthorized());
  }
}
