package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.*;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.dto.FotoOlehOlehForm;
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.entities.User;
import org.delcom.app.services.OlehOlehService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class OlehOlehControllerTest {

    @Mock
    private OlehOlehService olehOlehService;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private OlehOlehController controller;

    private User mockUser;
    private OlehOleh mockOlehOleh;
    private UUID userId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        
        // Inject authContext manually
        ReflectionTestUtils.setField(controller, "authContext", authContext);
        
        mockUser = new User();
        mockUser.setId(userId);
        
        mockOlehOleh = new OlehOleh(userId, "Bakpia", "Jogja", "DIY", "Makanan", "Enak", 50000.0, "Toko", 5, true);
        mockOlehOleh.setId(itemId);
    }

    // ==========================================
    // 1. UPLOAD FOTO (FIXED: Using Mock Form)
    // ==========================================
    
    @Test
    void testUploadFoto_Success() throws IOException {
        // MOCK FORM AGAR KITA BISA KONTROL HASIL VALIDASINYA
        FotoOlehOlehForm mockForm = mock(FotoOlehOlehForm.class);
        
        // Skenario: Validasi Form Lolos (return null)
        when(mockForm.getValidationError()).thenReturn(null);
        
        when(authContext.isAuthenticated()).thenReturn(true);
        // Controller memanggil service.uploadFoto
        doNothing().when(olehOlehService).uploadFoto(itemId, mockForm);
        
        ResponseEntity<ApiResponse<String>> response = controller.uploadFoto(itemId, mockForm);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("success", response.getBody().getStatus());
        verify(olehOlehService).uploadFoto(itemId, mockForm);
    }
    
    @Test
    void testUploadFoto_Unauthenticated() {
        FotoOlehOlehForm mockForm = mock(FotoOlehOlehForm.class);
        when(authContext.isAuthenticated()).thenReturn(false);
        
        ResponseEntity<ApiResponse<String>> response = controller.uploadFoto(itemId, mockForm);
        
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testUploadFoto_ValidationError() {
        FotoOlehOlehForm mockForm = mock(FotoOlehOlehForm.class);
        
        // Skenario: Validasi Form Gagal (return pesan error)
        when(authContext.isAuthenticated()).thenReturn(true);
        when(mockForm.getValidationError()).thenReturn("File terlalu besar");
        
        ResponseEntity<ApiResponse<String>> response = controller.uploadFoto(itemId, mockForm);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fail", response.getBody().getStatus());
        assertEquals("File terlalu besar", response.getBody().getMessage());
        
        // Pastikan service TIDAK dipanggil karena validasi gagal di awal
        try {
            verify(olehOlehService, never()).uploadFoto(any(), any());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void testUploadFoto_IOException() throws IOException {
        FotoOlehOlehForm mockForm = mock(FotoOlehOlehForm.class);
        
        // Skenario: Validasi Lolos, tapi Service error IO
        when(mockForm.getValidationError()).thenReturn(null);
        
        when(authContext.isAuthenticated()).thenReturn(true);
        doThrow(new IOException("Disk Error")).when(olehOlehService).uploadFoto(itemId, mockForm);
        
        ResponseEntity<ApiResponse<String>> response = controller.uploadFoto(itemId, mockForm);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("error", response.getBody().getStatus());
    }

    @Test
    void testUploadFoto_RuntimeError() throws IOException {
        FotoOlehOlehForm mockForm = mock(FotoOlehOlehForm.class);
        
        // Skenario: Validasi Lolos, tapi ID tidak ketemu di Service
        when(mockForm.getValidationError()).thenReturn(null);
        
        when(authContext.isAuthenticated()).thenReturn(true);
        doThrow(new RuntimeException("ID Not Found")).when(olehOlehService).uploadFoto(itemId, mockForm);
        
        ResponseEntity<ApiResponse<String>> response = controller.uploadFoto(itemId, mockForm);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("fail", response.getBody().getStatus());
        assertEquals("ID Not Found", response.getBody().getMessage());
    }

    // ==========================================
    // 2. CREATE OLEH-OLEH (Full Validation Coverage)
    // ==========================================
    
    @Test
    void testCreate_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.createOlehOleh(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean()))
            .thenReturn(mockOlehOleh);
        
        ResponseEntity<ApiResponse<Map<String, UUID>>> response = controller.createOlehOleh(mockOlehOleh);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testCreate_Unauthenticated() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.createOlehOleh(mockOlehOleh).getStatusCode());
    }

    @Test
    void testCreate_Validations_NullAndEmpty() {
        OlehOleh req = new OlehOleh();

        // 1. Nama
        req.setNamaOlehOleh(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
        req.setNamaOlehOleh("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());

        // 2. Asal Daerah
        req.setNamaOlehOleh("Valid"); 
        req.setAsalDaerah(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
        req.setAsalDaerah("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());

        // 3. Provinsi
        req.setAsalDaerah("Valid"); 
        req.setProvinsi(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
        req.setProvinsi("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());

        // 4. Kategori
        req.setProvinsi("Valid"); 
        req.setKategori(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
        req.setKategori("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());

        // 5. Deskripsi
        req.setKategori("Valid"); 
        req.setDeskripsi(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
        req.setDeskripsi("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.createOlehOleh(req).getStatusCode());
    }

    @Test
    void testCreate_TernaryRekomendasiDefault() {
        // Test branch: req.getIsRekomendasi() != null ? ... : FALSE
        OlehOleh req = new OlehOleh(userId, "A", "B", "C", "D", "E", 10.0, "F", 1, null);
        req.setIsRekomendasi(null); // Force null
        
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        // Mock return agar tidak NPE
        when(olehOlehService.createOlehOleh(any(),any(),any(),any(),any(),any(),any(),any(),any(),eq(false)))
            .thenReturn(mockOlehOleh);
            
        controller.createOlehOleh(req);
        
        // Verifikasi service dipanggil dengan param false
        verify(olehOlehService).createOlehOleh(any(),any(),any(),any(),any(),any(),any(),any(),any(),eq(false));
    }

    // ==========================================
    // 3. GET METHODS
    // ==========================================
    
    @Test
    void testGetAll_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getAllOlehOleh(userId, null)).thenReturn(List.of(mockOlehOleh));
        assertEquals(HttpStatus.OK, controller.getAllOlehOleh(null).getStatusCode());
    }

    @Test
    void testGetAll_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getAllOlehOleh(null).getStatusCode());
    }

    @Test
    void testGetById_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getOlehOlehById(userId, itemId)).thenReturn(mockOlehOleh);
        assertEquals(HttpStatus.OK, controller.getOlehOlehById(itemId).getStatusCode());
    }

    @Test
    void testGetById_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getOlehOlehById(userId, itemId)).thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND, controller.getOlehOlehById(itemId).getStatusCode());
    }
    
    @Test
    void testGetById_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getOlehOlehById(itemId).getStatusCode());
    }

    // ==========================================
    // 4. UPDATE (Coverage Fix)
    // ==========================================
    
    @Test
    void testUpdate_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.updateOlehOleh(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any()))
            .thenReturn(mockOlehOleh);
        assertEquals(HttpStatus.OK, controller.updateOlehOleh(itemId, mockOlehOleh).getStatusCode());
    }

    @Test
    void testUpdate_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.updateOlehOleh(any(),any(),any(),any(),any(),any(),any(),any(),any(),any(),any()))
            .thenReturn(null);
        assertEquals(HttpStatus.NOT_FOUND, controller.updateOlehOleh(itemId, mockOlehOleh).getStatusCode());
    }

    @Test
    void testUpdate_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.updateOlehOleh(itemId, mockOlehOleh).getStatusCode());
    }

    @Test
    void testUpdate_Validations_NullAndEmpty() {
        OlehOleh req = new OlehOleh();

        // 1. Nama
        req.setNamaOlehOleh(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
        req.setNamaOlehOleh("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());

        // 2. Asal
        req.setNamaOlehOleh("OK"); 
        req.setAsalDaerah(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
        req.setAsalDaerah("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());

        // 3. Provinsi
        req.setAsalDaerah("OK"); 
        req.setProvinsi(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
        req.setProvinsi("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());

        // 4. Kategori
        req.setProvinsi("OK"); 
        req.setKategori(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
        req.setKategori("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());

        // 5. Deskripsi
        req.setKategori("OK"); 
        req.setDeskripsi(null);
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
        req.setDeskripsi("");
        assertEquals(HttpStatus.BAD_REQUEST, controller.updateOlehOleh(itemId, req).getStatusCode());
    }

    // ==========================================
    // 5. DELETE
    // ==========================================
    
    @Test
    void testDelete_Success() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.deleteOlehOleh(userId, itemId)).thenReturn(true);
        assertEquals(HttpStatus.OK, controller.deleteOlehOleh(itemId).getStatusCode());
    }

    @Test
    void testDelete_NotFound() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.deleteOlehOleh(userId, itemId)).thenReturn(false);
        assertEquals(HttpStatus.NOT_FOUND, controller.deleteOlehOleh(itemId).getStatusCode());
    }

    @Test
    void testDelete_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.deleteOlehOleh(itemId).getStatusCode());
    }

    // ==========================================
    // 6. FILTERS & CHARTS (All Cases)
    // ==========================================
    
    @Test
    void testGetByKategori() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getByKategori(userId, "A")).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getByKategori("A").getStatusCode());
    }
    @Test
    void testGetByKategori_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getByKategori("A").getStatusCode());
    }

    @Test
    void testGetByProvinsi() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getByProvinsi(userId, "A")).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getByProvinsi("A").getStatusCode());
    }
    @Test
    void testGetByProvinsi_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getByProvinsi("A").getStatusCode());
    }

    @Test
    void testGetRekomendasi() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getRekomendasi(userId)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getRekomendasi().getStatusCode());
    }
    @Test
    void testGetRekomendasi_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getRekomendasi().getStatusCode());
    }

    @Test
    void testChartKategori() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getCountByKategori(userId)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getChartKategori().getStatusCode());
    }
    @Test
    void testChartKategori_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getChartKategori().getStatusCode());
    }

    @Test
    void testChartProvinsi() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getCountByProvinsi(userId)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getChartProvinsi().getStatusCode());
    }
    @Test
    void testChartProvinsi_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getChartProvinsi().getStatusCode());
    }

    @Test
    void testChartHarga() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getAvgHargaByKategori(userId)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getChartHarga().getStatusCode());
    }
    @Test
    void testChartHarga_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getChartHarga().getStatusCode());
    }

    @Test
    void testChartRating() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getCountByRating(userId)).thenReturn(List.of());
        assertEquals(HttpStatus.OK, controller.getChartRating().getStatusCode());
    }
    @Test
    void testChartRating_Unauth() {
        when(authContext.isAuthenticated()).thenReturn(false);
        assertEquals(HttpStatus.FORBIDDEN, controller.getChartRating().getStatusCode());
    }
}