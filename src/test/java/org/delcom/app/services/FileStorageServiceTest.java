package org.delcom.app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @Mock
    private MultipartFile mockFile;

    @BeforeEach
    void setUp() {
        // Default: set uploadDir ke tempDir yang sudah dibuat oleh JUnit
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    // ==========================================
    // 1. TEST STORE FILE (GENERAL & DIR CREATION)
    // ==========================================

    @Test
    void testStoreFile_General_Success() throws IOException {
        String originalName = "test.jpg";
        when(mockFile.getOriginalFilename()).thenReturn(originalName);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("content".getBytes()));

        String storedFilename = fileStorageService.storeFile(mockFile);

        assertNotNull(storedFilename);
        assertTrue(storedFilename.contains("_test.jpg"));
        assertTrue(Files.exists(tempDir.resolve(storedFilename)));
    }

    // --- TEST CASE TAMBAHAN UNTUK CREATE DIR (Menutup Celah 70% Coverage) ---
    @Test
    void testStoreFile_CreatesDirectory_IfNotExist() throws IOException {
        // Arahkan uploadDir ke subfolder yang BELUM ADA
        Path nonExistentPath = tempDir.resolve("folder_baru_belum_ada");
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", nonExistentPath.toString());

        String originalName = "new_dir_test.jpg";
        when(mockFile.getOriginalFilename()).thenReturn(originalName);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        // Saat ini dipanggil, kode Files.createDirectories() akan dijalankan
        fileStorageService.storeFile(mockFile);

        // Assert folder benar-benar terbuat
        assertTrue(Files.exists(nonExistentPath));
    }

    // ==========================================
    // 2. TEST STORE FILE (UUID & PREFIX)
    // ==========================================

    @Test
    void testStoreFile_UUID_Success() throws IOException {
        UUID uuid = UUID.randomUUID();
        String originalName = "profile.png";
        when(mockFile.getOriginalFilename()).thenReturn(originalName);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String storedFilename = fileStorageService.storeFile(mockFile, uuid);

        String expectedName = "foto_oleholeh_" + uuid.toString() + ".png";
        assertEquals(expectedName, storedFilename);
        assertTrue(Files.exists(tempDir.resolve(storedFilename)));
    }

    @Test
    void testStoreFileWithPrefix_Success() throws IOException {
        UUID uuid = UUID.randomUUID();
        String prefix = "produk";
        String originalName = "barang.jpeg";
        when(mockFile.getOriginalFilename()).thenReturn(originalName);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String storedFilename = fileStorageService.storeFileWithPrefix(mockFile, prefix, uuid);

        String expectedName = "produk_" + uuid.toString() + ".jpeg";
        assertEquals(expectedName, storedFilename);
        assertTrue(Files.exists(tempDir.resolve(storedFilename)));
    }

    // ==========================================
    // 3. TEST EXTENSION (EDGE CASES)
    // ==========================================

    @Test
    void testStoreFile_NoExtension() throws IOException {
        UUID uuid = UUID.randomUUID();
        when(mockFile.getOriginalFilename()).thenReturn("filename_without_extension");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String storedFilename = fileStorageService.storeFile(mockFile, uuid);

        String expectedName = "foto_oleholeh_" + uuid.toString();
        assertEquals(expectedName, storedFilename);
    }
    
    @Test
    void testStoreFile_NullFilename() throws IOException {
        UUID uuid = UUID.randomUUID();
        when(mockFile.getOriginalFilename()).thenReturn(null);
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));

        String storedFilename = fileStorageService.storeFile(mockFile, uuid);

        String expectedName = "foto_oleholeh_" + uuid.toString();
        assertEquals(expectedName, storedFilename);
    }

    // ==========================================
    // 4. TEST DELETE & FILE OPERATIONS
    // ==========================================

    @Test
    void testDeleteFile_Success() throws IOException {
        String filename = "todelete.txt";
        Path path = tempDir.resolve(filename);
        Files.createFile(path);

        boolean result = fileStorageService.deleteFile(filename);

        assertTrue(result);
        assertFalse(Files.exists(path));
    }

    @Test
    void testDeleteFile_NotFound() {
        boolean result = fileStorageService.deleteFile("ghost.txt");
        assertFalse(result);
    }

    // --- TEST CASE TAMBAHAN UNTUK EXCEPTION DELETE (Menutup Celah 61% Coverage) ---
    @Test
    void testDeleteFile_IOException_HitCatchBlock() throws IOException {
        // Trik: Buat direktori yang tidak kosong, lalu coba hapus pakai deleteFile.
        // Files.deleteIfExists akan melempar DirectoryNotEmptyException (turunan IOException)
        // Ini akan memaksa masuk ke blok catch(IOException e)
        
        String dirName = "folder_berisi";
        Path subDir = tempDir.resolve(dirName);
        Files.createDirectories(subDir);
        Files.createFile(subDir.resolve("file_didalam.txt")); // Isi folder agar gagal dihapus

        // Act
        boolean result = fileStorageService.deleteFile(dirName);

        // Assert: Harus return false karena masuk catch block
        assertFalse(result);
        assertTrue(Files.exists(subDir)); // Pastikan folder masih ada
    }

    @Test
    void testLoadFile() {
        Path path = fileStorageService.loadFile("test.txt");
        assertEquals(tempDir.resolve("test.txt"), path);
    }

    @Test
    void testFileExists() throws IOException {
        String filename = "exist.txt";
        Files.createFile(tempDir.resolve(filename));

        assertTrue(fileStorageService.fileExists(filename));
        assertFalse(fileStorageService.fileExists("nope.txt"));
    }

    // ==========================================
    // 5. TEST VALIDATION
    // ==========================================

    @Test
    void testIsValidImageFile_NullOrEmpty() {
        assertFalse(fileStorageService.isValidImageFile(null));
        
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        assertFalse(fileStorageService.isValidImageFile(emptyFile));
    }

    @Test
    void testIsValidImageFile_NullContentType() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn(null);
        assertFalse(fileStorageService.isValidImageFile(mockFile));
    }

    @Test
    void testIsValidImageFile_AllTypes() {
        when(mockFile.isEmpty()).thenReturn(false);

        String[] types = {"image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"};
        for (String type : types) {
            when(mockFile.getContentType()).thenReturn(type);
            assertTrue(fileStorageService.isValidImageFile(mockFile), "Failed for type: " + type);
        }
    }

    @Test
    void testIsValidImageFile_InvalidType() {
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        assertFalse(fileStorageService.isValidImageFile(mockFile));
    }

    @Test
    void testIsValidFileSize() {
        assertFalse(fileStorageService.isValidFileSize(null));

        long fiveMB = 5 * 1024 * 1024;
        when(mockFile.getSize()).thenReturn(fiveMB);
        assertTrue(fileStorageService.isValidFileSize(mockFile));

        when(mockFile.getSize()).thenReturn(fiveMB + 1);
        assertFalse(fileStorageService.isValidFileSize(mockFile));
    }
}