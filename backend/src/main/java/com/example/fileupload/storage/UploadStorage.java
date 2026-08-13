package com.example.fileupload.storage;

import java.io.InputStream;

public interface UploadStorage {
  void store(String fileId, InputStream content) throws Exception;
  InputStream read(String fileId) throws Exception;
}
