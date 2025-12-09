package org.delcom.app.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "oleh_oleh")
public class OlehOleh {

    // ======= 4 Atribut Wajib =======
    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // ======= 8 Atribut Custom (Minimal 4) =======
    @Column(name = "nama_oleh_oleh", nullable = false, length = 200)
    private String namaOlehOleh;

    @Column(name = "asal_daerah", nullable = false, length = 100)
    private String asalDaerah;

    @Column(name = "provinsi", nullable = false, length = 100)
    private String provinsi;

    @Column(name = "kategori", nullable = false, length = 50)
    private String kategori; // makanan, kerajinan, pakaian, minuman, dll

    @Column(name = "deskripsi", nullable = false, columnDefinition = "TEXT")
    private String deskripsi;

    @Column(name = "harga_perkiraan", nullable = true)
    private Double hargaPerkiraan;

    @Column(name = "tempat_beli", nullable = true, length = 200)
    private String tempatBeli;

    @Column(name = "rating", nullable = true)
    private Integer rating; // 1-5 bintang

    @Column(name = "foto_path", nullable = true)
    private String fotoPath; // untuk upload gambar

    @Column(name = "is_rekomendasi", nullable = false, columnDefinition = "boolean default false")
    private Boolean isRekomendasi = false; // apakah direkomendasikan atau tidak

    // ======= Constructors =======
    public OlehOleh() {
    }

    public OlehOleh(UUID userId, String namaOlehOleh, String asalDaerah, String provinsi, 
                    String kategori, String deskripsi, Double hargaPerkiraan, 
                    String tempatBeli, Integer rating, Boolean isRekomendasi) {
        this.userId = userId;
        this.namaOlehOleh = namaOlehOleh;
        this.asalDaerah = asalDaerah;
        this.provinsi = provinsi;
        this.kategori = kategori;
        this.deskripsi = deskripsi;
        this.hargaPerkiraan = hargaPerkiraan;
        this.tempatBeli = tempatBeli;
        this.rating = rating;
        this.isRekomendasi = isRekomendasi;
    }

    // ======= Getters & Setters =======
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getNamaOlehOleh() {
        return namaOlehOleh;
    }

    public void setNamaOlehOleh(String namaOlehOleh) {
        this.namaOlehOleh = namaOlehOleh;
    }

    public String getAsalDaerah() {
        return asalDaerah;
    }

    public void setAsalDaerah(String asalDaerah) {
        this.asalDaerah = asalDaerah;
    }

    public String getProvinsi() {
        return provinsi;
    }

    public void setProvinsi(String provinsi) {
        this.provinsi = provinsi;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public Double getHargaPerkiraan() {
        return hargaPerkiraan;
    }

    public void setHargaPerkiraan(Double hargaPerkiraan) {
        this.hargaPerkiraan = hargaPerkiraan;
    }

    public String getTempatBeli() {
        return tempatBeli;
    }

    public void setTempatBeli(String tempatBeli) {
        this.tempatBeli = tempatBeli;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFotoPath() {
        return fotoPath;
    }

    public void setFotoPath(String fotoPath) {
        this.fotoPath = fotoPath;
    }

    public Boolean getIsRekomendasi() {
        return isRekomendasi;
    }

    public void setIsRekomendasi(Boolean isRekomendasi) {
        this.isRekomendasi = isRekomendasi;
    }

    // ======= @PrePersist & @PreUpdate =======
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}