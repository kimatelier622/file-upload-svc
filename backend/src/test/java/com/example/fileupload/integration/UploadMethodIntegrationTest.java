package com.example.fileupload.integration;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class UploadMethodIntegrationTest {
  @Autowired MockMvc mvc;

  @Test void rejectsUnsupportedMethodsWith405AfterAuthentication() throws Exception {
    for (HttpMethod method : new HttpMethod[] {HttpMethod.GET, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH, HttpMethod.TRACE}) {
      mvc.perform(request(method, "/api/upload")
          .contentType("multipart/form-data; boundary=schemathesis")
          .content("--schemathesis\r\nContent-Disposition: form-data; name=\"file\"; filename=\"test.pdf\"\r\n\r\ninvalid\r\n--schemathesis--\r\n")
          .with(jwt()))
          .andExpect(status().isMethodNotAllowed())
          .andExpect(header().string("Allow", "POST"))
          .andExpect(content().contentTypeCompatibleWith("application/json"))
          .andExpect(jsonPath("$.code").value("METHOD_NOT_ALLOWED"));
    }
  }
}
