package org.delcom.app.controllers;

import org.delcom.app.entities.OlehOleh;
import org.delcom.app.services.OlehOlehService;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.utils.ConstUtil; // Import wajib
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/oleholeh")
public class OlehOlehViewController {

    @Autowired private OlehOlehService olehOlehService;
    @Autowired private AuthContext authContext;

    // 1. HOME PAGE
    @GetMapping
    public String index(Model model) {
        // ... (Logika Auth & User ID sama seperti sebelumnya) ...
        UUID userId = authContext.isAuthenticated() ? authContext.getAuthUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        List<OlehOleh> list = olehOlehService.getAllOlehOleh(userId, null);
        List<Object[]> chartData = olehOlehService.getCountByKategori(userId);
        
        model.addAttribute("listOlehOleh", list);
        model.addAttribute("chartData", chartData);

        // SESUAI CONSTUTIL BARU:
        return ConstUtil.TEMPLATE_PAGES_HOME; 
    }


    // 2. FORM TAMBAH
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("oleholeh", new OlehOleh());
        model.addAttribute("kategoriList", ConstUtil.KATEGORI_OLEH_OLEH);
        model.addAttribute("provinsiList", ConstUtil.PROVINSI_INDONESIA);

        // SESUAI CONSTUTIL BARU (yang kita tambah tadi):
        return ConstUtil.TEMPLATE_MODELS_OLEH_OLEH_ADD; 
    }
    

    // 3. PROSES SIMPAN
    @PostMapping("/save")
    public String saveOlehOleh(@ModelAttribute OlehOleh oleholeh, 
                               @RequestParam("imageFile") MultipartFile imageFile) {
        // ... (Logika Auth & Upload Gambar sama seperti sebelumnya) ...
        UUID userId = authContext.isAuthenticated() ? authContext.getAuthUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");

        if (!imageFile.isEmpty()) {
            try {
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path path = Paths.get("src/main/resources/static/uploads/" + fileName);
                Files.createDirectories(path.getParent());
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                oleholeh.setFotoPath("/uploads/" + fileName);
            } catch (IOException e) { e.printStackTrace(); }
        }

        olehOlehService.createOlehOleh(
            userId,
            oleholeh.getNamaOlehOleh(),
            oleholeh.getAsalDaerah(),
            oleholeh.getProvinsi(),
            oleholeh.getKategori(),
            oleholeh.getDeskripsi(),
            oleholeh.getHargaPerkiraan(),
            oleholeh.getTempatBeli(),
            oleholeh.getRating(),
            oleholeh.getIsRekomendasi() != null ? oleholeh.getIsRekomendasi() : false
        );
        
        // SESUAI CONSTUTIL BARU:
        return ConstUtil.REDIRECT_OLEH_OLEH;
    }

    // 4. DETAIL PAGE
    @GetMapping("/detail/{id}")
    public String detail(@PathVariable UUID id, Model model) {
        UUID userId = authContext.isAuthenticated() ? authContext.getAuthUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
        OlehOleh item = olehOlehService.getOlehOlehById(userId, id);
        model.addAttribute("item", item);
        
        // SESUAI CONSTUTIL BARU:
        return ConstUtil.TEMPLATE_PAGES_OLEH_OLEH_DETAIL;
    }

    // 5. DELETE
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable UUID id) {
        UUID userId = authContext.isAuthenticated() ? authContext.getAuthUser().getId() : UUID.fromString("00000000-0000-0000-0000-000000000000");
        olehOlehService.deleteOlehOleh(userId, id);
        
        // SESUAI CONSTUTIL BARU:
        return ConstUtil.REDIRECT_OLEH_OLEH;
    }
    
}