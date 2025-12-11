package org.delcom.app.dto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class FotoOlehOlehFormTest {

    @Mock
    private MultipartFile mockFile;

    // ==========================================
    // 1. TEST CONSTRUCTORS, GETTERS & SETTERS
    // ==========================================
    @Test
    void testBasicPojo() {
        // Test Empty Constructor & Setters
        FotoOlehOlehForm form = new FotoOlehOlehForm();
        UUID id = UUID.randomUUID();
        
        form.setId(id);
        form.setFotoFile(mockFile);

        assertEquals(id, form.getId());
        assertEquals(mockFile, form.getFotoFile());

        // Test Constructor with Args
        FotoOlehOlehForm formArgs = new FotoOlehOlehForm(mockFile);
        assertEquals(mockFile, formArgs.getFotoFile());
    }

    // ==========================================
    // 2. TEST isEmpty()
    // ==========================================
    @Test
    void testIsEmpty() {
        FotoOlehOlehForm form = new FotoOlehOlehForm();

        // Case 1: FotoFile is Null -> True
        form.setFotoFile(null);
        assertTrue(form.isEmpty());

        // Case 2: FotoFile exists but isEmpty() is true -> True
        form.setFotoFile(mockFile);
        when(mockFile.isEmpty()).thenReturn(true);
        assertTrue(form.isEmpty());

        // Case 3: FotoFile exists and has content -> False
        when(mockFile.isEmpty()).thenReturn(false);
        assertFalse(form.isEmpty());
    }

    // ==========================================
    // 3. TEST isValidImage()
    // ==========================================
    @Test
    void testIsValidImage() {
        FotoOlehOlehForm form = new FotoOlehOlehForm(mockFile);

        // Case 1: File Empty -> False
        when(mockFile.isEmpty()).thenReturn(true);
        assertFalse(form.isValidImage());

        // Case 2: File Not Empty, but ContentType Null -> False
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn(null);
        assertFalse(form.isValidImage());

        // Case 3: File Not Empty, ContentType Invalid (e.g. PDF) -> False
        when(mockFile.getContentType()).thenReturn("application/pdf");
        assertFalse(form.isValidImage());

        // Case 4: Valid Types -> True
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        assertTrue(form.isValidImage());
        
        when(mockFile.getContentType()).thenReturn("image/png");
        assertTrue(form.isValidImage());
    }

    // ==========================================
    // 4. TEST isSizeValid()
    // ==========================================
    @Test
    void testIsSizeValid() {
        FotoOlehOlehForm form = new FotoOlehOlehForm(mockFile);
        long max = 5 * 1024 * 1024; // 5MB

        // Case 1: Valid Size (Exactly 5MB) -> True
        when(mockFile.getSize()).thenReturn(max);
        assertTrue(form.isSizeValid());

        // Case 2: Valid Size (Small) -> True
        when(mockFile.getSize()).thenReturn(100L);
        assertTrue(form.isSizeValid());

        // Case 3: Invalid Size (5MB + 1 Byte) -> False
        when(mockFile.getSize()).thenReturn(max + 1);
        assertFalse(form.isSizeValid());
        
        // Case 4: File is Null -> False (sesuai logic: fotoFile != null && ...)
        form.setFotoFile(null);
        assertFalse(form.isSizeValid());
    }

    // ==========================================
    // 5. TEST getValidationError() (Integration Logic)
    // ==========================================
    @Test
    void testGetValidationError() {
        FotoOlehOlehForm form = new FotoOlehOlehForm(mockFile);

        // Branch 1: Empty -> Error Message
        when(mockFile.isEmpty()).thenReturn(true);
        assertEquals("File foto belum dipilih.", form.getValidationError());

        // Branch 2: Invalid Type -> Error Message
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getContentType()).thenReturn("text/plain");
        assertEquals("Format file tidak valid. Gunakan: JPG, JPEG, PNG, GIF, atau WebP.", form.getValidationError());

        // Branch 3: Invalid Size -> Error Message
        when(mockFile.getContentType()).thenReturn("image/jpeg");
        when(mockFile.getSize()).thenReturn((5L * 1024 * 1024) + 1024); // 5MB + 1KB
        assertTrue(form.getValidationError().contains("Ukuran file terlalu besar"));

        // Branch 4: All Valid -> Null
        when(mockFile.getSize()).thenReturn(1024L);
        assertNull(form.getValidationError());
    }

    // ==========================================
    // 6. TEST getFileSizeFormatted()
    // ==========================================
    @Test
    void testGetFileSizeFormatted() {
        FotoOlehOlehForm form = new FotoOlehOlehForm();

        // Case 1: File Null -> "0 B" (Branch: fotoFile != null ? ... : 0)
        assertEquals("0 B", form.getFileSizeFormatted());

        form.setFotoFile(mockFile);

        // Case 2: Size < 1024 Bytes (Branch: size < 1024)
        when(mockFile.getSize()).thenReturn(500L);
        assertEquals("500 B", form.getFileSizeFormatted());

        // Case 3: Size < 1 MB (Branch: size < 1024 * 1024)
        when(mockFile.getSize()).thenReturn(10240L); // 10 KB
        assertEquals("10.00 KB", form.getFileSizeFormatted());

        // Case 4: Size >= 1 MB (Branch: else)
        when(mockFile.getSize()).thenReturn(2L * 1024 * 1024); // 2 MB
        assertEquals("2.00 MB", form.getFileSizeFormatted());
    }
}