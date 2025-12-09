package org.delcom.app.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.List;
import java.util.UUID; // Jangan lupa import UUID

public class FotoOlehOlehForm {

    // Konstanta
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024; // 5MB
    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    // FIELD ID YANG DIBUTUHKAN OLEHOLEHVIEW
    private UUID id;

    @NotNull(message = "File foto tidak boleh kosong")
    private MultipartFile fotoFile;

    public FotoOlehOlehForm() {
    }

    public FotoOlehOlehForm(MultipartFile fotoFile) {
        this.fotoFile = fotoFile;
    }

    // --- Getters and Setters ---
    
    // Getter Setter ID (PENTING AGAR ERROR HILANG)
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

    // --- Helper & Validation Methods ---

    public boolean isEmpty() {
        return fotoFile == null || fotoFile.isEmpty();
    }

    public boolean isValidImage() {
        if (this.isEmpty()) return false;
        String contentType = fotoFile.getContentType();
        return contentType != null && ALLOWED_TYPES.contains(contentType.toLowerCase());
    }

    public boolean isSizeValid() {
        return fotoFile != null && fotoFile.getSize() <= MAX_SIZE_BYTES;
    }

    public String getValidationError() {
        if (isEmpty()) return "File foto belum dipilih.";
        if (!isValidImage()) return "Format file tidak valid. Gunakan: JPG, JPEG, PNG, GIF, atau WebP.";
        if (!isSizeValid()) return "Ukuran file terlalu besar. Maksimal 5MB. Ukuran Anda: " + getFileSizeFormatted();
        return null;
    }

    public String getFileSizeFormatted() {
        long size = (fotoFile != null) ? fotoFile.getSize() : 0;
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }
}