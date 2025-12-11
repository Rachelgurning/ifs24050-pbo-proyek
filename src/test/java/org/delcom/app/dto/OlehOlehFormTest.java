package org.delcom.app.dto;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class OlehOlehFormTest {

    // ==========================================
    // 1. TEST GETTERS & SETTERS (Standard POJO)
    // ==========================================
    @Test
    void testGettersAndSetters() {
        OlehOlehForm form = new OlehOlehForm();
        UUID id = UUID.randomUUID();

        // Set Values
        form.setId(id);
        form.setNamaOlehOleh("Bakpia");
        form.setAsalDaerah("Jogja");
        form.setProvinsi("DIY");
        form.setKategori("Makanan");
        form.setDeskripsi("Enak");
        form.setHargaPerkiraan(50000.0);
        form.setTempatBeli("Pasar Beringharjo");
        form.setRating(5);
        form.setIsRekomendasi(true);
        form.setConfirmNamaOlehOleh("Bakpia");

        // Assert Values
        assertEquals(id, form.getId());
        assertEquals("Bakpia", form.getNamaOlehOleh());
        assertEquals("Jogja", form.getAsalDaerah());
        assertEquals("DIY", form.getProvinsi());
        assertEquals("Makanan", form.getKategori());
        assertEquals("Enak", form.getDeskripsi());
        assertEquals(50000.0, form.getHargaPerkiraan());
        assertEquals("Pasar Beringharjo", form.getTempatBeli());
        assertEquals(5, form.getRating());
        assertTrue(form.getIsRekomendasi());
        assertEquals("Bakpia", form.getConfirmNamaOlehOleh());
    }

    // ==========================================
    // 2. TEST LOGIC: isValid()
    // ==========================================
    // Logic: Semua field String utama tidak boleh Null dan tidak boleh Empty/Blank
    
    @Test
    void testIsValid_Success() {
        OlehOlehForm form = new OlehOlehForm();
        form.setNamaOlehOleh("Valid");
        form.setAsalDaerah("Valid");
        form.setProvinsi("Valid");
        form.setKategori("Valid");
        form.setDeskripsi("Valid");
        
        // Semua terisi = TRUE
        assertTrue(form.isValid());
    }

    @Test
    void testIsValid_Failures() {
        OlehOlehForm form = new OlehOlehForm();
        
        // 1. Fail di Nama (Null & Empty)
        // Setup field lain valid agar isolasi error di Nama
        form.setAsalDaerah("V"); form.setProvinsi("V"); form.setKategori("V"); form.setDeskripsi("V");
        
        form.setNamaOlehOleh(null);
        assertFalse(form.isValid());
        form.setNamaOlehOleh("");
        assertFalse(form.isValid());
        form.setNamaOlehOleh("   "); // Trim check
        assertFalse(form.isValid());
        
        // 2. Fail di Asal Daerah (Null & Empty)
        form.setNamaOlehOleh("Valid"); // Fix nama
        
        form.setAsalDaerah(null);
        assertFalse(form.isValid());
        form.setAsalDaerah("");
        assertFalse(form.isValid());
        form.setAsalDaerah("   ");
        assertFalse(form.isValid());

        // 3. Fail di Provinsi (Null & Empty)
        form.setAsalDaerah("Valid"); // Fix asal
        
        form.setProvinsi(null);
        assertFalse(form.isValid());
        form.setProvinsi("");
        assertFalse(form.isValid());
        form.setProvinsi("   ");
        assertFalse(form.isValid());

        // 4. Fail di Kategori (Null & Empty)
        form.setProvinsi("Valid"); // Fix provinsi
        
        form.setKategori(null);
        assertFalse(form.isValid());
        form.setKategori("");
        assertFalse(form.isValid());
        form.setKategori("   ");
        assertFalse(form.isValid());

        // 5. Fail di Deskripsi (Null & Empty)
        form.setKategori("Valid"); // Fix kategori
        
        form.setDeskripsi(null);
        assertFalse(form.isValid());
        form.setDeskripsi("");
        assertFalse(form.isValid());
        form.setDeskripsi("   ");
        assertFalse(form.isValid());
    }

    // ==========================================
    // 3. TEST LOGIC: isRatingValid()
    // ==========================================
    // Logic: Null boleh, ATAU (>=1 DAN <=5)

    @Test
    void testIsRatingValid() {
        OlehOlehForm form = new OlehOlehForm();

        // 1. Null (Allowed)
        form.setRating(null);
        assertTrue(form.isRatingValid());

        // 2. Valid Boundaries (1 and 5)
        form.setRating(1);
        assertTrue(form.isRatingValid());
        form.setRating(5);
        assertTrue(form.isRatingValid());
        form.setRating(3);
        assertTrue(form.isRatingValid());

        // 3. Invalid (Too Low)
        form.setRating(0);
        assertFalse(form.isRatingValid());
        form.setRating(-1);
        assertFalse(form.isRatingValid());

        // 4. Invalid (Too High)
        form.setRating(6);
        assertFalse(form.isRatingValid());
    }

    // ==========================================
    // 4. TEST LOGIC: isHargaValid()
    // ==========================================
    // Logic: Null boleh, ATAU >= 0

    @Test
    void testIsHargaValid() {
        OlehOlehForm form = new OlehOlehForm();

        // 1. Null (Allowed)
        form.setHargaPerkiraan(null);
        assertTrue(form.isHargaValid());

        // 2. Valid (Zero and Positive)
        form.setHargaPerkiraan(0.0);
        assertTrue(form.isHargaValid());
        form.setHargaPerkiraan(10000.0);
        assertTrue(form.isHargaValid());

        // 3. Invalid (Negative)
        form.setHargaPerkiraan(-1.0);
        assertFalse(form.isHargaValid());
        form.setHargaPerkiraan(-0.01);
        assertFalse(form.isHargaValid());
    }
}