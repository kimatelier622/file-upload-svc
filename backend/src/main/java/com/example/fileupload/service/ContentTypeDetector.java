package com.example.fileupload.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeDetector {
  private static final Set<String> ALLOWED = Set.of("image/jpeg", "image/png", "application/pdf");
  private final Tika tika = new Tika();
  public String detect(InputStream content) throws IOException { return tika.detect(content); }
  public boolean isAllowed(String contentType) { return ALLOWED.contains(contentType); }
}
