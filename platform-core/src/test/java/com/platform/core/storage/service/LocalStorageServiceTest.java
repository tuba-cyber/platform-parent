package com.platform.core.storage.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.multipart.MultipartFile;

class LocalStorageServiceTest {

    private LocalStorageService storageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        storageService = new LocalStorageService();
        setField(storageService, "basePath", tempDir.toString());
        setField(storageService, "baseUrl", "http://localhost:8081/files");
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    @DisplayName("Dosya başarıyla kaydedilmeli")
    void shouldStoreFile() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("test-image.png");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes()));

        String result = storageService.store(mockFile, "company1", "images");

        assertThat(result).startsWith("company1/images/");
        assertThat(result).endsWith(".png");

        // Verify file exists on disk
        Path storedFile = tempDir.resolve(result);
        assertThat(Files.exists(storedFile)).isTrue();
    }

    @Test
    @DisplayName("Dosya başarıyla okunabilmeli")
    void shouldRetrieveFile() throws Exception {
        // First store a file
        Path dir = tempDir.resolve("company1/docs");
        Files.createDirectories(dir);
        Path file = dir.resolve("test.txt");
        Files.writeString(file, "test content");

        InputStream is = storageService.retrieve("company1/docs/test.txt");

        assertThat(is).isNotNull();
        String content = new String(is.readAllBytes());
        assertThat(content).isEqualTo("test content");
    }

    @Test
    @DisplayName("Dosya başarıyla silinebilmeli")
    void shouldDeleteFile() throws Exception {
        // First store a file
        Path dir = tempDir.resolve("company1/docs");
        Files.createDirectories(dir);
        Path file = dir.resolve("test.txt");
        Files.writeString(file, "test content");

        storageService.delete("company1/docs/test.txt");

        assertThat(Files.exists(file)).isFalse();
    }

    @Test
    @DisplayName("Dosya URL'i doğru dönmeli")
    void shouldGetFileUrl() {
        String url = storageService.getFileUrl("company1/images/test.png");
        assertThat(url).isEqualTo("http://localhost:8081/files/company1/images/test.png");
    }

    @Test
    @DisplayName("Olmayan dosya okuma hatası fırlatmalı")
    void shouldThrowWhenFileNotFound() {
        assertThatThrownBy(() -> storageService.retrieve("nonexistent/file.txt"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Dosya bulunamadı");
    }

    @Test
    @DisplayName("Uzantısız dosya kaydedilmeli")
    void shouldStoreFileWithoutExtension() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("noextension");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        String result = storageService.store(mockFile, "company1", "files");

        assertThat(result).startsWith("company1/files/");
        assertThat(result).doesNotContain(".");
    }
}
