package com.platform.core.storage.service;

import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String store(MultipartFile file, String companyId, String folder);

    InputStream retrieve(String filePath);

    void delete(String filePath);

    String getFileUrl(String filePath);
}