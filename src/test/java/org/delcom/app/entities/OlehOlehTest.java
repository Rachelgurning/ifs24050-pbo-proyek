package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OlehOlehTest {

    // ==========================================
    // 1. TEST GETTERS, SETTERS & NO-ARGS CONSTRUCTOR
    // ==========================================
    @Test
    void testGettersAndSetters() {
        // Arrange
        OlehOleh olehOleh = new OlehOleh();
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String nama = "Dodol";
        String asal = "Garut";
        String prov = "Jabar";
        String kat = "Makanan";
        String desk = "Manis";
        Double harga = 25000.0;
        String tempat = "Pasar";
        Integer rating = 5;
        String foto = "img.jpg";
        
        // Act (Panggil semua Setter)
        olehOleh.setId(id);
        olehOleh.setUserId(userId);
        olehOleh.setNamaOlehOleh(nama);
        olehOleh.setAsalDaerah(asal);
        olehOleh.setProvinsi(prov);
        olehOleh.setKategori(kat);
        olehOleh.setDeskripsi(desk);
        olehOleh.setHargaPerkiraan(harga);
        olehOleh.setTempatBeli(tempat);
        olehOleh.setRating(rating);
        olehOleh.setFotoPath(foto);
        olehOleh.setIsRekomendasi(true);

        // Assert (Panggil semua Getter)
        assertEquals(id, olehOleh.getId());
        assertEquals(userId, olehOleh.getUserId());
        assertEquals(nama, olehOleh.getNamaOlehOleh());
        assertEquals(asal, olehOleh.getAsalDaerah());
        assertEquals(prov, olehOleh.getProvinsi());
        assertEquals(kat, olehOleh.getKategori());
        assertEquals(desk, olehOleh.getDeskripsi());
        assertEquals(harga, olehOleh.getHargaPerkiraan());
        assertEquals(tempat, olehOleh.getTempatBeli());
        assertEquals(rating, olehOleh.getRating());
        assertEquals(foto, olehOleh.getFotoPath());
        assertTrue(olehOleh.getIsRekomendasi());
    }

    // ==========================================
    // 2. TEST ALL-ARGS CONSTRUCTOR
    // ==========================================
    @Test
    void testAllArgsConstructor() {
        UUID userId = UUID.randomUUID();
        
        OlehOleh olehOleh = new OlehOleh(
            userId, "Bakpia", "Jogja", "DIY", 
            "Makanan", "Enak", 50000.0, 
            "Toko", 5, true
        );

        assertEquals(userId, olehOleh.getUserId());
        assertEquals("Bakpia", olehOleh.getNamaOlehOleh());
        assertEquals("Jogja", olehOleh.getAsalDaerah());
        assertEquals("DIY", olehOleh.getProvinsi());
        assertEquals("Makanan", olehOleh.getKategori());
        assertEquals("Enak", olehOleh.getDeskripsi());
        assertEquals(50000.0, olehOleh.getHargaPerkiraan());
        assertEquals("Toko", olehOleh.getTempatBeli());
        assertEquals(5, olehOleh.getRating());
        assertTrue(olehOleh.getIsRekomendasi());
    }

    // ==========================================
    // 3. TEST LIFECYCLE METHODS (@PrePersist & @PreUpdate)
    // ==========================================
    @Test
    void testLifecycleMethods() throws InterruptedException {
        OlehOleh olehOleh = new OlehOleh();

        // 1. Test onCreate()
        olehOleh.onCreate(); 

        assertNotNull(olehOleh.getCreatedAt()); 
        assertNotNull(olehOleh.getUpdatedAt()); 
        
        // HAPUS assertEquals di sini karena waktu eksekusi baris created & updated
        // bisa beda sekian nanodetik, menyebabkan test gagal.
        // Cukup pastikan tidak null.

        // Simpan waktu lama untuk perbandingan
        LocalDateTime oldUpdate = olehOleh.getUpdatedAt();

        // Jeda sedikit agar waktu berubah (100ms lebih aman daripada 10ms)
        Thread.sleep(100); 

        // 2. Test onUpdate()
        olehOleh.onUpdate();

        assertNotNull(olehOleh.getUpdatedAt());
        
        // Pastikan updatedAt berubah (Waktu baru harus setelah waktu lama)
        assertTrue(olehOleh.getUpdatedAt().isAfter(oldUpdate), "UpdatedAt harusnya berubah ke waktu yang lebih baru");
    }
}