package org.delcom.app.views;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.delcom.app.dto.FotoOlehOlehForm;
import org.delcom.app.dto.OlehOlehForm;
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.entities.User;
import org.delcom.app.services.FileStorageService;
import org.delcom.app.services.OlehOlehService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/oleh-oleh")
public class OlehOlehView {

    private final OlehOlehService olehOlehService;
    private final FileStorageService fileStorageService;

    public OlehOlehView(OlehOlehService olehOlehService, FileStorageService fileStorageService) {
        this.olehOlehService = olehOlehService;
        this.fileStorageService = fileStorageService;
    }

    // ========================================================================
    // 1. TAMBAH OLEH-OLEH + FOTO (Menggantikan method add yang lama)
    // ========================================================================
    @PostMapping("/add-with-photo")
    public String postAddOlehOlehWithPhoto(
            @Valid @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            @RequestParam(value = "fotoFile", required = false) MultipartFile fotoFile,
            RedirectAttributes redirectAttributes) {

        // Cek Autentikasi
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken) || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) authentication.getPrincipal();

        // Validasi Manual Form Data
        if (olehOlehForm.getNamaOlehOleh() == null || olehOlehForm.getNamaOlehOleh().isBlank()) {
            return handleError(redirectAttributes, "Nama oleh-oleh tidak boleh kosong", "addOlehOlehModalOpen");
        }
        if (!olehOlehForm.isValid()) {
            return handleError(redirectAttributes, "Lengkapi semua data wajib (Asal Daerah, Provinsi, Kategori, Deskripsi)", "addOlehOlehModalOpen");
        }
        if (!olehOlehForm.isRatingValid()) {
            return handleError(redirectAttributes, "Rating harus antara 1-5", "addOlehOlehModalOpen");
        }
        if (!olehOlehForm.isHargaValid()) {
            return handleError(redirectAttributes, "Harga tidak valid", "addOlehOlehModalOpen");
        }

        // Validasi Foto (Jika ada yang diupload)
        FotoOlehOlehForm fotoForm = null;
        if (fotoFile != null && !fotoFile.isEmpty()) {
            fotoForm = new FotoOlehOlehForm(fotoFile);
            if (!fotoForm.isValidImage()) {
                return handleError(redirectAttributes, "Format foto harus JPG, PNG, GIF, atau WebP", "addOlehOlehModalOpen");
            }
            if (!fotoForm.isSizeValid()) {
                return handleError(redirectAttributes, "Ukuran foto maksimal 5MB", "addOlehOlehModalOpen");
            }
        }

        // Simpan Data Teks ke Database
        var entity = olehOlehService.createOlehOleh(
                authUser.getId(),
                olehOlehForm.getNamaOlehOleh(),
                olehOlehForm.getAsalDaerah(),
                olehOlehForm.getProvinsi(),
                olehOlehForm.getKategori(),
                olehOlehForm.getDeskripsi(),
                olehOlehForm.getHargaPerkiraan(),
                olehOlehForm.getTempatBeli(),
                olehOlehForm.getRating(),
                olehOlehForm.getIsRekomendasi() != null ? olehOlehForm.getIsRekomendasi() : false);

        if (entity == null) {
            return handleError(redirectAttributes, "Gagal menyimpan data ke database", "addOlehOlehModalOpen");
        }

        // Proses Upload Foto (Jika ada)
        if (fotoForm != null) {
            try {
                fotoForm.setId(entity.getId());
                olehOlehService.uploadFoto(entity.getId(), fotoForm);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("warning", "Data tersimpan, tapi gagal upload foto: " + e.getMessage());
                return "redirect:/";
            }
        }

        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil ditambahkan!");
        return "redirect:/";
    }

    // ========================================================================
    // 2. EDIT OLEH-OLEH (DATA TEKS)
    // ========================================================================
    @PostMapping("/edit")
    public String postEditOlehOleh(@Valid @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            RedirectAttributes redirectAttributes) {
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken) || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) authentication.getPrincipal();

        if (olehOlehForm.getId() == null) {
            return handleError(redirectAttributes, "ID tidak valid", "editOlehOlehModalOpen");
        }

        // Validasi
        if (olehOlehForm.getNamaOlehOleh() == null || olehOlehForm.getNamaOlehOleh().isBlank()) {
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return handleError(redirectAttributes, "Nama oleh-oleh wajib diisi", "editOlehOlehModalOpen");
        }

        var updated = olehOlehService.updateOlehOleh(
                authUser.getId(),
                olehOlehForm.getId(),
                olehOlehForm.getNamaOlehOleh(),
                olehOlehForm.getAsalDaerah(),
                olehOlehForm.getProvinsi(),
                olehOlehForm.getKategori(),
                olehOlehForm.getDeskripsi(),
                olehOlehForm.getHargaPerkiraan(),
                olehOlehForm.getTempatBeli(),
                olehOlehForm.getRating(),
                olehOlehForm.getIsRekomendasi());

        if (updated == null) {
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return handleError(redirectAttributes, "Gagal update data", "editOlehOlehModalOpen");
        }

        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil diperbarui.");
        return "redirect:/";
    }

// ========================================================================
    // 3. HAPUS OLEH-OLEH (FIXED)
    // ========================================================================
    @PostMapping("/delete")
    public String postDeleteOlehOleh(
            // HAPUS @Valid DARI SINI AGAR TIDAK MENGECEK FIELD LAIN
            @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken) || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) authentication.getPrincipal();

        // Validasi Manual: Cukup cek ID dan Nama Konfirmasi saja
        if (olehOlehForm.getId() == null) {
            return handleError(redirectAttributes, "ID tidak valid", "deleteOlehOlehModalOpen");
        }
        
        if (olehOlehForm.getConfirmNamaOlehOleh() == null || olehOlehForm.getConfirmNamaOlehOleh().isBlank()) {
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return handleError(redirectAttributes, "Nama konfirmasi wajib diisi", "deleteOlehOlehModalOpen");
        }

        // Cek Data di Database
        OlehOleh existing = olehOlehService.getOlehOlehById(authUser.getId(), olehOlehForm.getId());
        if (existing == null) {
            return handleError(redirectAttributes, "Data tidak ditemukan", "deleteOlehOlehModalOpen");
        }
        
        // Cek Kesesuaian Nama
        if (!existing.getNamaOlehOleh().equals(olehOlehForm.getConfirmNamaOlehOleh())) {
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return handleError(redirectAttributes, "Nama konfirmasi tidak sesuai", "deleteOlehOlehModalOpen");
        }

        // Proses Hapus
        boolean deleted = olehOlehService.deleteOlehOleh(authUser.getId(), olehOlehForm.getId());
        if (!deleted) {
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return handleError(redirectAttributes, "Gagal menghapus data", "deleteOlehOlehModalOpen");
        }

        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil dihapus.");
        return "redirect:/";
    }

    // ========================================================================
    // 4. DETAIL HALAMAN
    // ========================================================================
    @GetMapping("/{olehOlehId}")
    public String getDetailOlehOleh(@PathVariable UUID olehOlehId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken) || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) authentication.getPrincipal();
        model.addAttribute("auth", authUser);

        OlehOleh olehOleh = olehOlehService.getOlehOlehById(authUser.getId(), olehOlehId);
        if (olehOleh == null) return "redirect:/";
        
        model.addAttribute("olehOleh", olehOleh);
        
        // Form untuk edit foto di halaman detail
        FotoOlehOlehForm fotoOlehOlehForm = new FotoOlehOlehForm();
        fotoOlehOlehForm.setId(olehOlehId);
        model.addAttribute("fotoOlehOlehForm", fotoOlehOlehForm);

        return ConstUtil.TEMPLATE_PAGES_OLEH_OLEH_DETAIL;
    }

    // ========================================================================
    // 5. UPDATE FOTO (DARI HALAMAN DETAIL)
    // ========================================================================
    @PostMapping("/edit-foto")
    public String postEditFotoOlehOleh(@Valid @ModelAttribute("fotoOlehOlehForm") FotoOlehOlehForm fotoOlehOlehForm,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken) || !(authentication.getPrincipal() instanceof User)) {
            return "redirect:/auth/logout";
        }

        if (fotoOlehOlehForm.isEmpty()) return handleError(redirectAttributes, "File belum dipilih", "editFotoOlehOlehModalOpen", "/oleh-oleh/" + fotoOlehOlehForm.getId());
        if (!fotoOlehOlehForm.isValidImage()) return handleError(redirectAttributes, "Format salah. Gunakan JPG/PNG/WEBP", "editFotoOlehOlehModalOpen", "/oleh-oleh/" + fotoOlehOlehForm.getId());
        if (!fotoOlehOlehForm.isSizeValid()) return handleError(redirectAttributes, "File terlalu besar (Max 5MB)", "editFotoOlehOlehModalOpen", "/oleh-oleh/" + fotoOlehOlehForm.getId());

        try {
            olehOlehService.uploadFoto(fotoOlehOlehForm.getId(), fotoOlehOlehForm);
            redirectAttributes.addFlashAttribute("success", "Foto berhasil diupdate");
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        } catch (Exception e) {
            return handleError(redirectAttributes, "Error: " + e.getMessage(), "editFotoOlehOlehModalOpen", "/oleh-oleh/" + fotoOlehOlehForm.getId());
        }
    }

    // ========================================================================
    // 6. LOAD GAMBAR (Resource Handler)
    // ========================================================================
    @GetMapping("/foto/{filename:.+}")
    @ResponseBody
    public Resource getFotoByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());
            return (resource.exists() || resource.isReadable()) ? resource : null;
        } catch (Exception e) {
            return null;
        }
    }

    // Helper untuk mempersingkat error handling
    private String handleError(RedirectAttributes ra, String msg, String modalKey) {
        return handleError(ra, msg, modalKey, "redirect:/");
    }

    private String handleError(RedirectAttributes ra, String msg, String modalKey, String redirectUrl) {
        ra.addFlashAttribute("error", msg);
        if (modalKey != null) ra.addFlashAttribute(modalKey, true);
        return redirectUrl;
    }
}