package org.delcom.app.utils;

public class ConstUtil {
    // Template paths for Pages
    public static final String TEMPLATE_PAGES_HOME = "pages/oleh-oleh/home";
    public static final String TEMPLATE_PAGES_OLEH_OLEH_DETAIL = "pages/oleh-oleh/detail";
    
    // <--- TAMBAHAN PENTING (Agar error HomeView hilang) ---
    // Pastikan kamu nanti membuat file chart.html di folder templates/pages/
    public static final String TEMPLATE_PAGES_CHART = "models/oleh-oleh/chart"; 

    // Template paths for Models (Form)
    public static final String TEMPLATE_MODELS_OLEH_OLEH_ADD = "models/oleh-oleh/add"; 
    
    // Auth templates
    public static final String TEMPLATE_PAGES_AUTH_LOGIN = "pages/auth/login";
    public static final String TEMPLATE_PAGES_AUTH_REGISTER = "pages/auth/register";
    
    // Error templates
    public static final String TEMPLATE_PAGES_ERROR_404 = "pages/error/404";
    public static final String TEMPLATE_PAGES_ERROR_500 = "pages/error/500";
    
    // Redirect paths
    public static final String REDIRECT_HOME = "redirect:/";
    public static final String REDIRECT_OLEH_OLEH = "redirect:/oleholeh"; 
    public static final String REDIRECT_LOGIN = "redirect:/auth/login";
    public static final String REDIRECT_LOGOUT = "redirect:/auth/logout";
    
    // API Response status
    public static final String API_STATUS_SUCCESS = "success";
    public static final String API_STATUS_FAIL = "fail";
    public static final String API_STATUS_ERROR = "error";
    
    // Kategori Oleh-Oleh
    public static final String[] KATEGORI_OLEH_OLEH = {
        "Makanan", "Minuman", "Kerajinan", "Pakaian", "Aksesoris", "Lainnya"
    };
    
    // Provinsi Indonesia
    public static final String[] PROVINSI_INDONESIA = {
        "Aceh", "Sumatera Utara", "Sumatera Barat", "Riau", "Kepulauan Riau",
        "Jambi", "Sumatera Selatan", "Bangka Belitung", "Bengkulu", "Lampung",
        "DKI Jakarta", "Jawa Barat", "Jawa Tengah", "DI Yogyakarta", "Jawa Timur", "Banten",
        "Bali", "Nusa Tenggara Barat", "Nusa Tenggara Timur",
        "Kalimantan Barat", "Kalimantan Tengah", "Kalimantan Selatan", "Kalimantan Timur", "Kalimantan Utara",
        "Sulawesi Utara", "Sulawesi Tengah", "Sulawesi Selatan", "Sulawesi Tenggara", "Gorontalo", "Sulawesi Barat",
        "Maluku", "Maluku Utara", "Papua", "Papua Barat", "Papua Tengah", "Papua Pegunungan", "Papua Selatan", "Papua Barat Daya"
    };
}