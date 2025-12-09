package org.delcom.app.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.OlehOleh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OlehOlehRepository extends JpaRepository<OlehOleh, UUID> {
    
    // Cari berdasarkan keyword (nama, daerah, kategori, deskripsi)
    @Query("SELECT o FROM OlehOleh o WHERE (LOWER(o.namaOlehOleh) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.asalDaerah) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.provinsi) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.kategori) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(o.deskripsi) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findByKeyword(UUID userId, String keyword);

    // Tampilkan semua oleh-oleh berdasarkan userId
    @Query("SELECT o FROM OlehOleh o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findAllByUserId(UUID userId);

    // Cari oleh-oleh spesifik berdasarkan id dan userId
    @Query("SELECT o FROM OlehOleh o WHERE o.id = :id AND o.userId = :userId")
    Optional<OlehOleh> findByUserIdAndId(UUID userId, UUID id);

    // Filter berdasarkan kategori
    @Query("SELECT o FROM OlehOleh o WHERE o.kategori = :kategori AND o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findByKategori(UUID userId, String kategori);

    // Filter berdasarkan provinsi
    @Query("SELECT o FROM OlehOleh o WHERE o.provinsi = :provinsi AND o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findByProvinsi(UUID userId, String provinsi);

    // Filter berdasarkan asal daerah
    @Query("SELECT o FROM OlehOleh o WHERE o.asalDaerah = :asalDaerah AND o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findByAsalDaerah(UUID userId, String asalDaerah);

    // Filter oleh-oleh yang direkomendasikan
    @Query("SELECT o FROM OlehOleh o WHERE o.isRekomendasi = true AND o.userId = :userId ORDER BY o.rating DESC, o.createdAt DESC")
    List<OlehOleh> findRekomendasi(UUID userId);

    // Filter berdasarkan rating
    @Query("SELECT o FROM OlehOleh o WHERE o.rating = :rating AND o.userId = :userId ORDER BY o.createdAt DESC")
    List<OlehOleh> findByRating(UUID userId, Integer rating);

    // Query untuk Chart - Hitung jumlah oleh-oleh per kategori
    @Query("SELECT o.kategori, COUNT(o) FROM OlehOleh o WHERE o.userId = :userId GROUP BY o.kategori")
    List<Object[]> countByKategori(UUID userId);

    // Query untuk Chart - Hitung jumlah oleh-oleh per provinsi
    @Query("SELECT o.provinsi, COUNT(o) FROM OlehOleh o WHERE o.userId = :userId GROUP BY o.provinsi ORDER BY COUNT(o) DESC")
    List<Object[]> countByProvinsi(UUID userId);

    // Query untuk Chart - Hitung rata-rata harga per kategori
    @Query("SELECT o.kategori, AVG(o.hargaPerkiraan) FROM OlehOleh o WHERE o.userId = :userId AND o.hargaPerkiraan IS NOT NULL GROUP BY o.kategori")
    List<Object[]> avgHargaByKategori(UUID userId);

    // Query untuk Chart - Distribusi rating
    @Query("SELECT o.rating, COUNT(o) FROM OlehOleh o WHERE o.userId = :userId AND o.rating IS NOT NULL GROUP BY o.rating ORDER BY o.rating")
    List<Object[]> countByRating(UUID userId);
}