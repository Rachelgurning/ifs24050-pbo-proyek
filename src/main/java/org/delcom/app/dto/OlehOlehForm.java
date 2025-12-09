package org.delcom.app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public class OlehOlehForm {

    private UUID id;

    @NotBlank(message = "Nama oleh-oleh wajib diisi")
    @Size(max = 100, message = "Nama oleh-oleh maksimal 100 karakter")
    private String namaOlehOleh;

    @NotBlank(message = "Asal daerah wajib diisi")
    private String asalDaerah;

    @NotBlank(message = "Provinsi wajib diisi")
    private String provinsi;

    @NotBlank(message = "Kategori wajib diisi")
    private String kategori;

    @NotBlank(message = "Deskripsi wajib diisi")
    @Size(max = 1000, message = "Deskripsi terlalu panjang (maksimal 1000 karakter)")
    private String deskripsi;

    @PositiveOrZero(message = "Harga tidak boleh negatif")
    private Double hargaPerkiraan;

    private String tempatBeli;

    @Min(value = 1, message = "Rating minimal 1")
    @Max(value = 5, message = "Rating maksimal 5")
    private Integer rating; 

    private Boolean isRekomendasi = false;

    private String confirmNamaOlehOleh; 

    public OlehOlehForm() {
    }

    // --- Getters and Setters ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNamaOlehOleh() { return namaOlehOleh; }
    public void setNamaOlehOleh(String namaOlehOleh) { this.namaOlehOleh = namaOlehOleh; }

    public String getAsalDaerah() { return asalDaerah; }
    public void setAsalDaerah(String asalDaerah) { this.asalDaerah = asalDaerah; }

    public String getProvinsi() { return provinsi; }
    public void setProvinsi(String provinsi) { this.provinsi = provinsi; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public Double getHargaPerkiraan() { return hargaPerkiraan; }
    public void setHargaPerkiraan(Double hargaPerkiraan) { this.hargaPerkiraan = hargaPerkiraan; }

    public String getTempatBeli() { return tempatBeli; }
    public void setTempatBeli(String tempatBeli) { this.tempatBeli = tempatBeli; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Boolean getIsRekomendasi() { return isRekomendasi; }
    public void setIsRekomendasi(Boolean isRekomendasi) { this.isRekomendasi = isRekomendasi; }

    public String getConfirmNamaOlehOleh() { return confirmNamaOlehOleh; }
    public void setConfirmNamaOlehOleh(String confirmNamaOlehOleh) { this.confirmNamaOlehOleh = confirmNamaOlehOleh; }

    // --- METHOD VALIDASI MANUAL (Ditambahkan kembali agar View tidak Error) ---
    
    public boolean isValid() {
        return namaOlehOleh != null && !namaOlehOleh.trim().isEmpty() &&
                asalDaerah != null && !asalDaerah.trim().isEmpty() &&
                provinsi != null && !provinsi.trim().isEmpty() &&
                kategori != null && !kategori.trim().isEmpty() &&
                deskripsi != null && !deskripsi.trim().isEmpty();
    }

    public boolean isRatingValid() {
        return rating == null || (rating >= 1 && rating <= 5);
    }

    public boolean isHargaValid() {
        return hargaPerkiraan == null || hargaPerkiraan >= 0;
    }
}