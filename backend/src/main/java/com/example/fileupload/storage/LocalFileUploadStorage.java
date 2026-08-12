package com.example.fileupload.storage;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalFileUploadStorage implements UploadStorage {
  private final Path directory;
  public LocalFileUploadStorage(@Value("${app.upload.directory}") String directory) { this.directory = Path.of(directory); }
  @Override public void store(String fileId, InputStream content) throws Exception {
    Path base = directory.toAbsolutePath().normalize();
    Files.createDirectories(base);
    Path target = base.resolve(fileId).normalize();
    if (!target.getParent().equals(base)) throw new IllegalArgumentException("Invalid file ID");
    Files.copy(content, target);
  }
}
