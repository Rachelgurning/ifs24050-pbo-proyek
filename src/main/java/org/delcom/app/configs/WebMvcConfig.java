package org.delcom.app.configs;

import org.delcom.app.interceptors.AuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // Jangan lupa import ini
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // Jangan lupa import ini
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    // 1. Ambil lokasi folder upload dari application.properties
    // Default ke folder "./uploads" jika tidak disetting
    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") 
                .excludePathPatterns("/api/auth/**") 
                .excludePathPatterns("/api/public/**"); 
    }

    // 2. Konfigurasi agar folder fisik bisa diakses via URL
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Artinya: Setiap request ke URL "/uploads/**" 
        // akan diarahkan ke folder fisik di komputer "file:./uploads/"
        
        // Penting: Tambahkan "/" di akhir path string location
        String locationPath = "file:" + uploadDir + "/";
        
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(locationPath);
    }
}