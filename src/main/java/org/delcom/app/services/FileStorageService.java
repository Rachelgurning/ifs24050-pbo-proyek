package org.delcom.app.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * Menyimpan file foto oleh-oleh
     * @param file MultipartFile yang diupload
     * @param olehOlehId UUID oleh-oleh
     * @return nama file yang tersimpan
     */
    public String storeFile(MultipartFile file, UUID olehOlehId) throws IOException {
        // Buat directory jika belum ada
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = "foto_oleholeh_" + olehOlehId.toString() + fileExtension;

        // Simpan file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    /**
     * Menyimpan file dengan nama custom (untuk cover user atau lainnya)
     * @param file MultipartFile yang diupload
     * @param prefix prefix nama file (misal: "cover", "profile")
     * @param entityId UUID entity terkait
     * @return nama file yang tersimpan
     */
    public String storeFileWithPrefix(MultipartFile file, String prefix, UUID entityId) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String filename = prefix + "_" + entityId.toString() + fileExtension;

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    /**
     * Menghapus file dari storage
     * @param filename nama file yang akan dihapus
     * @return true jika berhasil dihapus
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Load file path
     * @param filename nama file
     * @return Path object
     */
    public Path loadFile(String filename) {
        return Paths.get(uploadDir).resolve(filename);
    }

    /**
     * Cek apakah file ada
     * @param filename nama file
     * @return true jika file ada
     */
    public boolean fileExists(String filename) {
        return Files.exists(loadFile(filename));
    }

    /**
     * Validasi file yang diupload
     * @param file MultipartFile
     * @return true jika valid
     */
    public boolean isValidImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        // Cek tipe file
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        // Hanya terima file gambar
        return contentType.equals("image/jpeg") || 
               contentType.equals("image/jpg") || 
               contentType.equals("image/png") || 
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    /**
     * Validasi ukuran file (max 5MB)
     * @param file MultipartFile
     * @return true jika ukuran valid
     */
    public boolean isValidFileSize(MultipartFile file) {
        if (file == null) {
            return false;
        }
        
        // Max 5MB
        long maxSize = 5 * 1024 * 1024; // 5MB in bytes
        return file.getSize() <= maxSize;
    }
}