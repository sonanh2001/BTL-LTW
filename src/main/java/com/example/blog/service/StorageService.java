package com.example.blog.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {
    String getStoredFileName(MultipartFile file, String id);

    void store(MultipartFile file, String storedFilename);

    Resource loadAsResource(String filename);

    Path load(String filename);

    void delete(String storedFilename) throws IOException;

    void init();
}
