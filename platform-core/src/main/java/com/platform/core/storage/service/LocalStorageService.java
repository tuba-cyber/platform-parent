package com.platform.core.storage.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LocalStorageService implements StorageService {

    @Value("${storage.local.base-path:uploads}")
    private String basePath;

    @Value("${storage.base-url:http://localhost:8081/files}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file, String companyId, String folder) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = getExtension(originalFilename);
            String newFilename = UUID.randomUUID() + extension;

            // Klasör yapısı: uploads/companyId/folder/filename
            Path dirPath = Paths.get(basePath, companyId, folder);
            Files.createDirectories(dirPath);

            Path filePath = dirPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

            String relativePath = companyId + "/" + folder + "/" + newFilename;
            log.info("Dosya kaydedildi: {}", relativePath);
            return relativePath;

        } catch (IOException e) {
            log.error("Dosya kaydedilemedi: {}", e.getMessage());
            throw new RuntimeException("Dosya yüklenemedi: " + e.getMessage());
        }
    }

    @Override
    public InputStream retrieve(String filePath) {
        try {
            Path path = Paths.get(basePath, filePath);
            return Files.newInputStream(path);
        } catch (IOException e) {
            log.error("Dosya okunamadı: {}", e.getMessage());
            throw new RuntimeException("Dosya bulunamadı: " + filePath);
        }
    }

    @Override
    public void delete(String filePath) {
        try {
            Path path = Paths.get(basePath, filePath);
            Files.deleteIfExists(path);
            log.info("Dosya silindi: {}", filePath);
        } catch (IOException e) {
            log.error("Dosya silinemedi: {}", e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return baseUrl + "/" + filePath;
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf("."));
    }
}