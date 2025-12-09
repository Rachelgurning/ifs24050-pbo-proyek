package org.delcom.app.dto;

import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

public class FotoOlehOlehForm {

    private UUID id;

    @NotNull(message = "Foto tidak boleh kosong")
    private MultipartFile fotoFile;

    // Constructor
    public FotoOlehOlehForm() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MultipartFile getFotoFile() {
        return fotoFile;
    }

    public void setFotoFile(MultipartFile fotoFile) {
        this.fotoFile = fotoFile;
    }

    // Helper methods
    public boolean isEmpty() {
        return fotoFile == null || fotoFile.isEmpty();
    }

    public String getOriginalFilename() {
        return fotoFile != null ? fotoFile.getOriginalFilename() : null;
    }

    public long getFileSize() {
        return fotoFile != null ? fotoFile.getSize() : 0;
    }

    // Validation methods
    public boolean isValidImage() {
        if (this.isEmpty()) {
            return false;
        }

        String contentType = fotoFile.getContentType();
        return contentType != null &&
                (contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("image/gif") ||
                        contentType.equals("image/webp"));
    }

    public boolean isSizeValid(long maxSize) {
        return fotoFile != null && fotoFile.getSize() <= maxSize;
    }

    public boolean isSizeValid() {
        // Default max size: 5MB
        long maxSize = 5 * 1024 * 1024; // 5MB in bytes
        return isSizeValid(maxSize);
    }

    public String getFileSizeFormatted() {
        long size = getFileSize();
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.2f KB", size / 1024.0);
        } else {
            return String.format("%.2f MB", size / (1024.0 * 1024.0));
        }
    }

    public String getFileExtension() {
        String filename = getOriginalFilename();
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    // Validation error messages
    public String getValidationError() {
        if (isEmpty()) {
            return "File foto tidak boleh kosong";
        }
        if (!isValidImage()) {
            return "Format file tidak valid. Gunakan format: JPEG, JPG, PNG, GIF, atau WebP";
        }
        if (!isSizeValid()) {
            return "Ukuran file terlalu besar. Maksimal 5MB. Ukuran file Anda: " + getFileSizeFormatted();
        }
        return null;
    }

    public boolean isValid() {
        return !isEmpty() && isValidImage() && isSizeValid();
    }
}