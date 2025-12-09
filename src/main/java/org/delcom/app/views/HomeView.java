package org.delcom.app.views;

import org.delcom.app.dto.OlehOlehForm;
import org.delcom.app.entities.User;
import org.delcom.app.services.OlehOlehService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeView {

    private final OlehOlehService olehOlehService;

    public HomeView(OlehOlehService olehOlehService) {
        this.olehOlehService = olehOlehService;
    }

    @GetMapping
    public String home(Model model, @RequestParam(required = false) String search) {
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

        // Oleh-Oleh List dengan search
        var olehOlehList = olehOlehService.getAllOlehOleh(authUser.getId(), search != null ? search : "");
        model.addAttribute("olehOlehList", olehOlehList);

        // Search keyword
        model.addAttribute("searchKeyword", search != null ? search : "");

        // OlehOleh Form
        model.addAttribute("olehOlehForm", new OlehOlehForm());

        // Statistics untuk dashboard (optional)
        model.addAttribute("totalOlehOleh", olehOlehList.size());
        
        // Hitung jumlah rekomendasi
        long jumlahRekomendasi = olehOlehList.stream()
                .filter(o -> o.getIsRekomendasi() != null && o.getIsRekomendasi())
                .count();
        model.addAttribute("jumlahRekomendasi", jumlahRekomendasi);

        return ConstUtil.TEMPLATE_PAGES_HOME;
    }

    @GetMapping("/chart")
    public String chart(Model model) {
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

        // Data untuk chart
        var chartKategori = olehOlehService.getCountByKategori(authUser.getId());
        var chartProvinsi = olehOlehService.getCountByProvinsi(authUser.getId());
        var chartHarga = olehOlehService.getAvgHargaByKategori(authUser.getId());
        var chartRating = olehOlehService.getCountByRating(authUser.getId());

        model.addAttribute("chartKategori", chartKategori);
        model.addAttribute("chartProvinsi", chartProvinsi);
        model.addAttribute("chartHarga", chartHarga);
        model.addAttribute("chartRating", chartRating);

        return ConstUtil.TEMPLATE_PAGES_CHART;
    }
}