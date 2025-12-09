package org.delcom.app.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.entities.User;
import org.delcom.app.services.OlehOlehService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/oleh-oleh")
public class OlehOlehController {
    private final OlehOlehService olehOlehService;

    @Autowired
    protected AuthContext authContext;

    public OlehOlehController(OlehOlehService olehOlehService) {
        this.olehOlehService = olehOlehService;
    }

    
    // Menambahkan oleh-oleh baru
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createOlehOleh(@RequestBody OlehOleh reqOlehOleh) {

        // Validasi input
        if (reqOlehOleh.getNamaOlehOleh() == null || reqOlehOleh.getNamaOlehOleh().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Nama oleh-oleh tidak valid", null));
        } else if (reqOlehOleh.getAsalDaerah() == null || reqOlehOleh.getAsalDaerah().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Asal daerah tidak valid", null));
        } else if (reqOlehOleh.getProvinsi() == null || reqOlehOleh.getProvinsi().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Provinsi tidak valid", null));
        } else if (reqOlehOleh.getKategori() == null || reqOlehOleh.getKategori().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kategori tidak valid", null));
        } else if (reqOlehOleh.getDeskripsi() == null || reqOlehOleh.getDeskripsi().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Deskripsi tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        OlehOleh newOlehOleh = olehOlehService.createOlehOleh(
                authUser.getId(),
                reqOlehOleh.getNamaOlehOleh(),
                reqOlehOleh.getAsalDaerah(),
                reqOlehOleh.getProvinsi(),
                reqOlehOleh.getKategori(),
                reqOlehOleh.getDeskripsi(),
                reqOlehOleh.getHargaPerkiraan(),
                reqOlehOleh.getTempatBeli(),
                reqOlehOleh.getRating(),
                reqOlehOleh.getIsRekomendasi() != null ? reqOlehOleh.getIsRekomendasi() : false);

        return ResponseEntity.ok(new ApiResponse<Map<String, UUID>>(
                "success",
                "Oleh-oleh berhasil ditambahkan",
                Map.of("id", newOlehOleh.getId())));
    }

    // Mendapatkan semua oleh-oleh dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<OlehOleh>>>> getAllOlehOleh(
            @RequestParam(required = false) String search) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<OlehOleh> olehOlehList = olehOlehService.getAllOlehOleh(authUser.getId(), search);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Daftar oleh-oleh berhasil diambil",
                Map.of("olehOleh", olehOlehList)));
    }

    // Mendapatkan oleh-oleh berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, OlehOleh>>> getOlehOlehById(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        OlehOleh olehOleh = olehOlehService.getOlehOlehById(authUser.getId(), id);
        if (olehOleh == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data oleh-oleh tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data oleh-oleh berhasil diambil",
                Map.of("olehOleh", olehOleh)));
    }

    // Memperbarui oleh-oleh berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OlehOleh>> updateOlehOleh(@PathVariable UUID id,
            @RequestBody OlehOleh reqOlehOleh) {

        // Validasi input
        if (reqOlehOleh.getNamaOlehOleh() == null || reqOlehOleh.getNamaOlehOleh().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Nama oleh-oleh tidak valid", null));
        } else if (reqOlehOleh.getAsalDaerah() == null || reqOlehOleh.getAsalDaerah().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Asal daerah tidak valid", null));
        } else if (reqOlehOleh.getProvinsi() == null || reqOlehOleh.getProvinsi().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Provinsi tidak valid", null));
        } else if (reqOlehOleh.getKategori() == null || reqOlehOleh.getKategori().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Kategori tidak valid", null));
        } else if (reqOlehOleh.getDeskripsi() == null || reqOlehOleh.getDeskripsi().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Deskripsi tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        OlehOleh updatedOlehOleh = olehOlehService.updateOlehOleh(
                authUser.getId(),
                id,
                reqOlehOleh.getNamaOlehOleh(),
                reqOlehOleh.getAsalDaerah(),
                reqOlehOleh.getProvinsi(),
                reqOlehOleh.getKategori(),
                reqOlehOleh.getDeskripsi(),
                reqOlehOleh.getHargaPerkiraan(),
                reqOlehOleh.getTempatBeli(),
                reqOlehOleh.getRating(),
                reqOlehOleh.getIsRekomendasi());

        if (updatedOlehOleh == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data oleh-oleh tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Data oleh-oleh berhasil diperbarui", null));
    }

    // Menghapus oleh-oleh berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOlehOleh(@PathVariable UUID id) {
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = olehOlehService.deleteOlehOleh(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data oleh-oleh tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data oleh-oleh berhasil dihapus",
                null));
    }

    // ======= Filter Endpoints =======

    // Filter berdasarkan kategori
    @GetMapping("/filter/kategori/{kategori}")
    public ResponseEntity<ApiResponse<Map<String, List<OlehOleh>>>> getByKategori(@PathVariable String kategori) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<OlehOleh> olehOlehList = olehOlehService.getByKategori(authUser.getId(), kategori);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data oleh-oleh kategori " + kategori + " berhasil diambil",
                Map.of("olehOleh", olehOlehList)));
    }

    // Filter berdasarkan provinsi
    @GetMapping("/filter/provinsi/{provinsi}")
    public ResponseEntity<ApiResponse<Map<String, List<OlehOleh>>>> getByProvinsi(@PathVariable String provinsi) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<OlehOleh> olehOlehList = olehOlehService.getByProvinsi(authUser.getId(), provinsi);
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data oleh-oleh dari provinsi " + provinsi + " berhasil diambil",
                Map.of("olehOleh", olehOlehList)));
    }

    // Daftar rekomendasi
    @GetMapping("/rekomendasi")
    public ResponseEntity<ApiResponse<Map<String, List<OlehOleh>>>> getRekomendasi() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<OlehOleh> olehOlehList = olehOlehService.getRekomendasi(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Daftar oleh-oleh rekomendasi berhasil diambil",
                Map.of("olehOleh", olehOlehList)));
    }

    // ======= Chart Data Endpoints =======

    // Data chart kategori
    @GetMapping("/chart/kategori")
    public ResponseEntity<ApiResponse<Map<String, List<Object[]>>>> getChartKategori() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<Object[]> chartData = olehOlehService.getCountByKategori(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data chart kategori berhasil diambil",
                Map.of("data", chartData)));
    }

    // Data chart provinsi
    @GetMapping("/chart/provinsi")
    public ResponseEntity<ApiResponse<Map<String, List<Object[]>>>> getChartProvinsi() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<Object[]> chartData = olehOlehService.getCountByProvinsi(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data chart provinsi berhasil diambil",
                Map.of("data", chartData)));
    }

    // Data chart harga rata-rata
    @GetMapping("/chart/harga")
    public ResponseEntity<ApiResponse<Map<String, List<Object[]>>>> getChartHarga() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<Object[]> chartData = olehOlehService.getAvgHargaByKategori(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data chart harga rata-rata berhasil diambil",
                Map.of("data", chartData)));
    }

    // Data chart rating
    @GetMapping("/chart/rating")
    public ResponseEntity<ApiResponse<Map<String, List<Object[]>>>> getChartRating() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<Object[]> chartData = olehOlehService.getCountByRating(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data chart rating berhasil diambil",
                Map.of("data", chartData)));
    }

    
}