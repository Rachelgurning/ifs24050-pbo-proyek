package org.delcom.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${app.upload.dir:./uploads}")
    protected String uploadDir;

    // --- METHOD BARU: Simpan file secara umum (menggunakan Timestamp) ---
    /**
     * Menyimpan file dengan nama unik berbasis waktu (tanpa perlu UUID entity).
     * Cocok untuk upload cepat.
     */
    public String storeFile(MultipartFile file) throws IOException {
        createDirIfNotExist();

        // Bersihkan nama file asli
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Buat nama unik: TIMESTAMP_NAMAASLI
        String filename = System.currentTimeMillis() + "_" + originalFilename;

        // Simpan file
        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // --- METHOD EXISTING: Simpan file berbasis UUID Entity ---
    public String storeFile(MultipartFile file, UUID entityId) throws IOException {
        createDirIfNotExist();

        String fileExtension = getExtension(file.getOriginalFilename());
        
        // Nama file: foto_oleholeh_UUID.jpg
        String filename = "foto_oleholeh_" + entityId.toString() + fileExtension;

        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // --- METHOD EXISTING: Simpan file dengan Prefix Custom ---
    public String storeFileWithPrefix(MultipartFile file, String prefix, UUID entityId) throws IOException {
        createDirIfNotExist();

        String fileExtension = getExtension(file.getOriginalFilename());
        
        // Nama file: prefix_UUID.jpg
        String filename = prefix + "_" + entityId.toString() + fileExtension;

        Path filePath = Paths.get(uploadDir).resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    // --- HELPER METHODS ---

    private void createDirIfNotExist() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    private String getExtension(String originalFilename) {
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "";
    }

    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            System.err.println("Gagal menghapus file: " + filename);
            return false;
        }
    }

    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    public boolean fileExists(String filename) {
        return Files.exists(loadFile(filename));
    }

    // --- VALIDATION ---

    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        
        String contentType = file.getContentType();
        if (contentType == null) return false;

        return contentType.equals("image/jpeg") || 
               contentType.equals("image/jpg") || 
               contentType.equals("image/png") || 
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    public boolean isValidFileSize(MultipartFile file) {
        if (file == null) return false;
        long maxSize = 5 * 1024 * 1024; // 5MB
        return file.getSize() <= maxSize;
    }
}