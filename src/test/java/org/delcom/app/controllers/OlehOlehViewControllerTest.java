package org.delcom.app.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.ArrayList;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.entities.User;
import org.delcom.app.services.OlehOlehService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class OlehOlehViewControllerTest {

    @Mock
    private OlehOlehService olehOlehService;

    @Mock
    private AuthContext authContext;

    @Mock
    private Model model;

    @InjectMocks
    private OlehOlehViewController controller;

    private User mockUser;
    private OlehOleh mockOlehOleh;
    private UUID userId;
    private UUID itemId;
    
    // UUID Dummy sesuai kodingan Controller kamu (0000...)
    private final UUID DUMMY_UUID = UUID.fromString("00000000-0000-0000-0000-000000000000");

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        // Inject AuthContext
        ReflectionTestUtils.setField(controller, "authContext", authContext);

        mockUser = new User();
        mockUser.setId(userId);
        
        mockOlehOleh = new OlehOleh(userId, "Bakpia", "Jogja", "DIY", "Makanan", "Enak", 50000.0, "Toko", 5, true);
        mockOlehOleh.setId(itemId);
    }

    // ==========================================
    // 1. INDEX (Covering Auth & Unauth Branches)
    // ==========================================
    @Test
    void testIndex_Authenticated() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getAllOlehOleh(any(), any())).thenReturn(new ArrayList<>());
        
        String viewName = controller.index(model);
        
        assertEquals("pages/oleh-oleh/home", viewName);
        // Verify dipanggil dengan User ID Asli
        verify(olehOlehService).getAllOlehOleh(eq(userId), any());
    }

    @Test
    void testIndex_Unauthenticated() {
        // INI YANG BIKIN KUNING JADI HIJAU
        // Kondisi: Tidak Login -> Pakai Dummy UUID
        when(authContext.isAuthenticated()).thenReturn(false);
        when(olehOlehService.getAllOlehOleh(any(), any())).thenReturn(new ArrayList<>());
        
        String viewName = controller.index(model);
        
        assertEquals("pages/oleh-oleh/home", viewName);
        // Verify dipanggil dengan DUMMY UUID
        verify(olehOlehService).getAllOlehOleh(eq(DUMMY_UUID), any());
    }

    // ==========================================
    // 2. SHOW ADD FORM
    // ==========================================
    @Test
    void testShowAddForm() {
        String viewName = controller.showAddForm(model);
        
        assertEquals("models/oleh-oleh/add", viewName);
        verify(model).addAttribute(eq("oleholeh"), any(OlehOleh.class));
    }

    // ==========================================
    // 3. DETAIL (Covering Auth & Unauth Branches)
    // ==========================================
    @Test
    void testDetail_Authenticated() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.getOlehOlehById(userId, itemId)).thenReturn(mockOlehOleh);

        String viewName = controller.detail(itemId, model);
        
        assertEquals("pages/oleh-oleh/detail", viewName);
        verify(model).addAttribute(eq("item"), eq(mockOlehOleh));
    }

    @Test
    void testDetail_Unauthenticated() {
        // INI YANG BIKIN KUNING JADI HIJAU
        when(authContext.isAuthenticated()).thenReturn(false);
        // Service dipanggil dengan Dummy UUID
        when(olehOlehService.getOlehOlehById(DUMMY_UUID, itemId)).thenReturn(mockOlehOleh);

        String viewName = controller.detail(itemId, model);
        
        assertEquals("pages/oleh-oleh/detail", viewName);
        verify(model).addAttribute(eq("item"), eq(mockOlehOleh));
    }

    // ==========================================
    // 4. SAVE (Covering All Branches)
    // ==========================================

    @Test
    void testSaveOlehOleh_Authenticated_WithImage() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("data".getBytes()));
        
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.createOlehOleh(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean()))
            .thenReturn(mockOlehOleh);

        String viewName = controller.saveOlehOleh(mockOlehOleh, mockFile);
        
        assertEquals("redirect:/oleholeh", viewName);
        verify(olehOlehService).createOlehOleh(eq(userId), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());
    }

    @Test
    void testSaveOlehOleh_Unauthenticated() throws IOException {
        // INI YANG BIKIN KUNING JADI HIJAU (Bagian User ID)
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true); 

        when(authContext.isAuthenticated()).thenReturn(false); // Tidak Login
        
        when(olehOlehService.createOlehOleh(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean()))
            .thenReturn(mockOlehOleh);

        String viewName = controller.saveOlehOleh(mockOlehOleh, mockFile);

        assertEquals("redirect:/oleholeh", viewName);
        // Pastikan service dipanggil dengan DUMMY UUID
        verify(olehOlehService).createOlehOleh(eq(DUMMY_UUID), any(), any(), any(), any(), any(), any(), any(), any(), anyBoolean());
    }
    
    @Test
    void testSaveOlehOleh_RekomendasiNull() throws IOException {
        // INI YANG BIKIN KUNING JADI HIJAU (Bagian Ternary IsRekomendasi)
        // Logika kode: req.getIsRekomendasi() != null ? ... : false
        
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(true); 
        
        OlehOleh req = new OlehOleh();
        req.setIsRekomendasi(null); // Force Null agar masuk ke branch 'false'

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        
        controller.saveOlehOleh(req, mockFile);
        
        // Verifikasi parameter terakhir (isRekomendasi) adalah FALSE
        verify(olehOlehService).createOlehOleh(any(), any(), any(), any(), any(), any(), any(), any(), any(), eq(false));
    }
    
    @Test
    void testSaveOlehOleh_IOException() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.isEmpty()).thenReturn(false);
        when(mockFile.getOriginalFilename()).thenReturn("test.jpg");
        when(mockFile.getInputStream()).thenThrow(new IOException("Disk Error"));
        
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
            
        String viewName = controller.saveOlehOleh(mockOlehOleh, mockFile);
        
        assertEquals("redirect:/oleholeh", viewName); 
    }

    // ==========================================
    // 5. DELETE (Covering Auth & Unauth Branches)
    // ==========================================

    @Test
    void testDeleteOlehOleh_Authenticated() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.deleteOlehOleh(userId, itemId)).thenReturn(true);

        String viewName = controller.delete(itemId);
        
        assertEquals("redirect:/oleholeh", viewName);
        verify(olehOlehService).deleteOlehOleh(eq(userId), eq(itemId));
    }

    @Test
    void testDeleteOlehOleh_Unauthenticated() {
        // INI YANG BIKIN KUNING JADI HIJAU
        when(authContext.isAuthenticated()).thenReturn(false);
        when(olehOlehService.deleteOlehOleh(DUMMY_UUID, itemId)).thenReturn(true);

        String viewName = controller.delete(itemId);
        
        assertEquals("redirect:/oleholeh", viewName);
        // Verify dengan DUMMY UUID
        verify(olehOlehService).deleteOlehOleh(eq(DUMMY_UUID), eq(itemId));
    }
    
    @Test
    void testDeleteOlehOleh_Fail() {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(mockUser);
        when(olehOlehService.deleteOlehOleh(userId, itemId)).thenReturn(false);

        String viewName = controller.delete(itemId);
        
        assertEquals("redirect:/oleholeh", viewName);
    }
}