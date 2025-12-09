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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/oleh-oleh")
public class OlehOlehView {

    private final OlehOlehService olehOlehService;
    private final FileStorageService fileStorageService;

    public OlehOlehView(OlehOlehService olehOlehService, FileStorageService fileStorageService) {
        this.olehOlehService = olehOlehService;
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/add")
    public String postAddOlehOleh(@Valid @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi form
        if (olehOlehForm.getNamaOlehOleh() == null || olehOlehForm.getNamaOlehOleh().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nama oleh-oleh tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getAsalDaerah() == null || olehOlehForm.getAsalDaerah().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Asal daerah tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getProvinsi() == null || olehOlehForm.getProvinsi().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Provinsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getKategori() == null || olehOlehForm.getKategori().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Kategori tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getDeskripsi() == null || olehOlehForm.getDeskripsi().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Deskripsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        // Validasi rating (1-5)
        if (!olehOlehForm.isRatingValid()) {
            redirectAttributes.addFlashAttribute("error", "Rating harus antara 1-5");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        // Validasi harga
        if (!olehOlehForm.isHargaValid()) {
            redirectAttributes.addFlashAttribute("error", "Harga tidak valid");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        // Simpan oleh-oleh
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
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan oleh-oleh");
            redirectAttributes.addFlashAttribute("addOlehOlehModalOpen", true);
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil ditambahkan.");
        return "redirect:/";
    }

    @PostMapping("/edit")
    public String postEditOlehOleh(@Valid @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {
        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        // Validasi form
        if (olehOlehForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID oleh-oleh tidak valid");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getNamaOlehOleh() == null || olehOlehForm.getNamaOlehOleh().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Nama oleh-oleh tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        if (olehOlehForm.getAsalDaerah() == null || olehOlehForm.getAsalDaerah().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Asal daerah tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        if (olehOlehForm.getProvinsi() == null || olehOlehForm.getProvinsi().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Provinsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        if (olehOlehForm.getKategori() == null || olehOlehForm.getKategori().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Kategori tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        if (olehOlehForm.getDeskripsi() == null || olehOlehForm.getDeskripsi().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Deskripsi tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Validasi rating
        if (!olehOlehForm.isRatingValid()) {
            redirectAttributes.addFlashAttribute("error", "Rating harus antara 1-5");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Validasi harga
        if (!olehOlehForm.isHargaValid()) {
            redirectAttributes.addFlashAttribute("error", "Harga tidak valid");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Update oleh-oleh
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
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui oleh-oleh");
            redirectAttributes.addFlashAttribute("editOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("editOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil diperbarui.");
        return "redirect:/";
    }

    @PostMapping("/delete")
    public String postDeleteOlehOleh(@Valid @ModelAttribute("olehOlehForm") OlehOlehForm olehOlehForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        // Validasi form
        if (olehOlehForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID oleh-oleh tidak valid");
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalOpen", true);
            return "redirect:/";
        }

        if (olehOlehForm.getConfirmNamaOlehOleh() == null || olehOlehForm.getConfirmNamaOlehOleh().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi nama oleh-oleh tidak boleh kosong");
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Periksa apakah oleh-oleh tersedia
        OlehOleh existingOlehOleh = olehOlehService.getOlehOlehById(authUser.getId(), olehOlehForm.getId());
        if (existingOlehOleh == null) {
            redirectAttributes.addFlashAttribute("error", "Oleh-oleh tidak ditemukan");
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        if (!existingOlehOleh.getNamaOlehOleh().equals(olehOlehForm.getConfirmNamaOlehOleh())) {
            redirectAttributes.addFlashAttribute("error", "Konfirmasi nama oleh-oleh tidak sesuai");
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Hapus oleh-oleh
        boolean deleted = olehOlehService.deleteOlehOleh(
                authUser.getId(),
                olehOlehForm.getId());
        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus oleh-oleh");
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalOpen", true);
            redirectAttributes.addFlashAttribute("deleteOlehOlehModalId", olehOlehForm.getId());
            return "redirect:/";
        }

        // Redirect dengan pesan sukses
        redirectAttributes.addFlashAttribute("success", "Oleh-oleh berhasil dihapus.");
        return "redirect:/";
    }

    @GetMapping("/{olehOlehId}")
    public String getDetailOlehOleh(@PathVariable UUID olehOlehId, Model model) {
        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // Ambil oleh-oleh
        OlehOleh olehOleh = olehOlehService.getOlehOlehById(authUser.getId(), olehOlehId);
        if (olehOleh == null) {
            return "redirect:/";
        }
        model.addAttribute("olehOleh", olehOleh);

        // Foto OlehOleh Form
        FotoOlehOlehForm fotoOlehOlehForm = new FotoOlehOlehForm();
        fotoOlehOlehForm.setId(olehOlehId);
        model.addAttribute("fotoOlehOlehForm", fotoOlehOlehForm);

        return ConstUtil.TEMPLATE_PAGES_OLEH_OLEH_DETAIL;
    }

    @PostMapping("/edit-foto")
    public String postEditFotoOlehOleh(@Valid @ModelAttribute("fotoOlehOlehForm") FotoOlehOlehForm fotoOlehOlehForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        // Autentikasi user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        if (fotoOlehOlehForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File foto tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editFotoOlehOlehModalOpen", true);
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        }

        // Check if oleh-oleh exists
        OlehOleh olehOleh = olehOlehService.getOlehOlehById(authUser.getId(), fotoOlehOlehForm.getId());
        if (olehOleh == null) {
            redirectAttributes.addFlashAttribute("error", "Oleh-oleh tidak ditemukan");
            redirectAttributes.addFlashAttribute("editFotoOlehOlehModalOpen", true);
            return "redirect:/";
        }

        // Validasi manual file type
        if (!fotoOlehOlehForm.isValidImage()) {
            redirectAttributes.addFlashAttribute("error",
                    "Format file tidak didukung. Gunakan JPEG, JPG, PNG, GIF, atau WebP");
            redirectAttributes.addFlashAttribute("editFotoOlehOlehModalOpen", true);
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        }

        // Validasi file size (max 5MB)
        if (!fotoOlehOlehForm.isSizeValid()) {
            redirectAttributes.addFlashAttribute("error",
                    "Ukuran file terlalu besar. Maksimal 5MB. Ukuran Anda: " + fotoOlehOlehForm.getFileSizeFormatted());
            redirectAttributes.addFlashAttribute("editFotoOlehOlehModalOpen", true);
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        }

        try {
            // Simpan file
            String fileName = fileStorageService.storeFile(fotoOlehOlehForm.getFotoFile(), fotoOlehOlehForm.getId());

            // Update oleh-oleh dengan nama file foto
            olehOlehService.updateFoto(fotoOlehOlehForm.getId(), fileName);

            redirectAttributes.addFlashAttribute("success", "Foto berhasil diupload");
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload foto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("editFotoOlehOlehModalOpen", true);
            return "redirect:/oleh-oleh/" + fotoOlehOlehForm.getId();
        }

    }

    @GetMapping("/foto/{filename:.+}")
    @ResponseBody
    public Resource getFotoByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}