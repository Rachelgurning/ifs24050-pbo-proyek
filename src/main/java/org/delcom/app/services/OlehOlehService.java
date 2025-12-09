package org.delcom.app.services;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.delcom.app.dto.FotoOlehOlehForm; // Pastikan Import ini
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.repositories.OlehOlehRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OlehOlehService {
    private final OlehOlehRepository olehOlehRepository;
    private final FileStorageService fileStorageService;

    public OlehOlehService(OlehOlehRepository olehOlehRepository, FileStorageService fileStorageService) {
        this.olehOlehRepository = olehOlehRepository;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public OlehOleh createOlehOleh(UUID userId, String namaOlehOleh, String asalDaerah, 
                                   String provinsi, String kategori, String deskripsi, 
                                   Double hargaPerkiraan, String tempatBeli, 
                                   Integer rating, Boolean isRekomendasi) {
        OlehOleh olehOleh = new OlehOleh(userId, namaOlehOleh, asalDaerah, provinsi, 
                                         kategori, deskripsi, hargaPerkiraan, 
                                         tempatBeli, rating, isRekomendasi);
        return olehOlehRepository.save(olehOleh);
    }

    // --- FITUR UPLOAD FOTO (FIXED) ---
    @Transactional
    public void uploadFoto(UUID id, FotoOlehOlehForm form) throws IOException {
        // 1. Cari Data
        OlehOleh olehOleh = olehOlehRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Data Oleh-oleh tidak ditemukan"));

        // 2. Hapus file lama jika ada
        if (olehOleh.getFotoPath() != null && !olehOleh.getFotoPath().isEmpty()) {
            fileStorageService.deleteFile(olehOleh.getFotoPath());
        }

        // 3. Simpan file baru (Nama file disesuaikan dengan ID)
        String filename = fileStorageService.storeFile(form.getFotoFile(), id);

        // 4. Update Database
        olehOleh.setFotoPath(filename);
        olehOlehRepository.save(olehOleh);
    }

    public List<OlehOleh> getAllOlehOleh(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return olehOlehRepository.findByKeyword(userId, search);
        }
        return olehOlehRepository.findAllByUserId(userId);
    }

    public OlehOleh getOlehOlehById(UUID userId, UUID id) {
        return olehOlehRepository.findByUserIdAndId(userId, id).orElse(null);
    }

    @Transactional
    public OlehOleh updateOlehOleh(UUID userId, UUID id, String namaOlehOleh, 
                                   String asalDaerah, String provinsi, String kategori, 
                                   String deskripsi, Double hargaPerkiraan, 
                                   String tempatBeli, Integer rating, Boolean isRekomendasi) {
        OlehOleh olehOleh = olehOlehRepository.findByUserIdAndId(userId, id).orElse(null);
        if (olehOleh != null) {
            olehOleh.setNamaOlehOleh(namaOlehOleh);
            olehOleh.setAsalDaerah(asalDaerah);
            olehOleh.setProvinsi(provinsi);
            olehOleh.setKategori(kategori);
            olehOleh.setDeskripsi(deskripsi);
            olehOleh.setHargaPerkiraan(hargaPerkiraan);
            olehOleh.setTempatBeli(tempatBeli);
            olehOleh.setRating(rating);
            olehOleh.setIsRekomendasi(isRekomendasi);
            return olehOlehRepository.save(olehOleh);
        }
        return null;
    }

    @Transactional
    public boolean deleteOlehOleh(UUID userId, UUID id) {
        OlehOleh olehOleh = olehOlehRepository.findByUserIdAndId(userId, id).orElse(null);
        if (olehOleh == null) {
            return false;
        }

        // Hapus foto jika ada
        if (olehOleh.getFotoPath() != null) {
            fileStorageService.deleteFile(olehOleh.getFotoPath());
        }

        olehOlehRepository.deleteById(id);
        return true;
    }

    // ======= Filter Methods =======
    public List<OlehOleh> getByKategori(UUID userId, String kategori) {
        return olehOlehRepository.findByKategori(userId, kategori);
    }

    public List<OlehOleh> getByProvinsi(UUID userId, String provinsi) {
        return olehOlehRepository.findByProvinsi(userId, provinsi);
    }

    public List<OlehOleh> getByAsalDaerah(UUID userId, String asalDaerah) {
        return olehOlehRepository.findByAsalDaerah(userId, asalDaerah);
    }

    public List<OlehOleh> getRekomendasi(UUID userId) {
        return olehOlehRepository.findRekomendasi(userId);
    }

    public List<OlehOleh> getByRating(UUID userId, Integer rating) {
        return olehOlehRepository.findByRating(userId, rating);
    }

    // ======= Chart Data Methods =======
    public List<Object[]> getCountByKategori(UUID userId) {
        return olehOlehRepository.countByKategori(userId);
    }

    public List<Object[]> getCountByProvinsi(UUID userId) {
        return olehOlehRepository.countByProvinsi(userId);
    }

    public List<Object[]> getAvgHargaByKategori(UUID userId) {
        return olehOlehRepository.avgHargaByKategori(userId);
    }

    public List<Object[]> getCountByRating(UUID userId) {
        return olehOlehRepository.countByRating(userId);
    }
}