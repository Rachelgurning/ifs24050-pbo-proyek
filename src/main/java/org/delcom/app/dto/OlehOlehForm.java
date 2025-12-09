package org.delcom.app.dto;

import java.util.UUID;

public class OlehOlehForm {

    private UUID id;

    private String namaOlehOleh;

    private String asalDaerah;

    private String provinsi;

    private String kategori;

    private String deskripsi;

    private Double hargaPerkiraan;

    private String tempatBeli;

    private Integer rating; // 1-5

    private Boolean isRekomendasi = false;

    private String confirmNamaOlehOleh; // untuk konfirmasi delete

    // Constructor
    public OlehOlehForm() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public Boolean getIsRekomendasi() {
        return isRekomendasi;
    }

    public void setIsRekomendasi(Boolean isRekomendasi) {
        this.isRekomendasi = isRekomendasi;
    }

    public String getConfirmNamaOlehOleh() {
        return confirmNamaOlehOleh;
    }

    public void setConfirmNamaOlehOleh(String confirmNamaOlehOleh) {
        this.confirmNamaOlehOleh = confirmNamaOlehOleh;
    }

    // Validation methods
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