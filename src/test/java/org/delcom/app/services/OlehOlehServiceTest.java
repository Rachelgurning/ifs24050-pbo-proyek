package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

import org.delcom.app.dto.FotoOlehOlehForm;
import org.delcom.app.entities.OlehOleh;
import org.delcom.app.repositories.OlehOlehRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
public class OlehOlehServiceTest {

    @Mock
    private OlehOlehRepository repository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private OlehOlehService service;

    private OlehOleh dummyOlehOleh;
    private UUID userId;
    private UUID itemId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();
        dummyOlehOleh = new OlehOleh(userId, "Bakpia", "Jogja", "DIY", "Makanan", "Enak", 50000.0, "Toko", 5, true);
        dummyOlehOleh.setId(itemId);
    }

    // --- TEST ORIGINAL (SUKSES) ---

    @Test
    void testCreateOlehOleh() {
        when(repository.save(any(OlehOleh.class))).thenReturn(dummyOlehOleh);
        OlehOleh res = service.createOlehOleh(userId, "Bakpia", "Jogja", "DIY", "Makanan", "Enak", 50000.0, "Toko", 5, true);
        assertNotNull(res);
        verify(repository, times(1)).save(any(OlehOleh.class));
    }

    @Test
    void testGetAllOlehOleh_NoSearch() {
        List<OlehOleh> list = new ArrayList<>();
        list.add(dummyOlehOleh);
        when(repository.findAllByUserId(userId)).thenReturn(list);
        List<OlehOleh> res = service.getAllOlehOleh(userId, null);
        assertEquals(1, res.size());
    }
    
    @Test
    void testGetById() {
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.of(dummyOlehOleh));
        OlehOleh res = service.getOlehOlehById(userId, itemId);
        assertNotNull(res);
    }

    @Test
    void testUpdateOlehOleh_Success() {
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.of(dummyOlehOleh));
        when(repository.save(any(OlehOleh.class))).thenReturn(dummyOlehOleh);
        OlehOleh res = service.updateOlehOleh(userId, itemId, "Baru", "Jogja", "DIY", "Makanan", "Enak", 60000.0, "Toko", 5, true);
        assertNotNull(res);
        assertEquals("Baru", res.getNamaOlehOleh());
    }

    @Test
    void testDeleteOlehOleh_Success() {
        dummyOlehOleh.setFotoPath(null);
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.of(dummyOlehOleh));
        
        boolean res = service.deleteOlehOleh(userId, itemId);
        
        assertTrue(res);
        verify(repository).deleteById(itemId);
        verify(fileStorageService, never()).deleteFile(anyString());
    }

    @Test
    void testUploadFoto_Success() throws IOException {
        MultipartFile mockFile = mock(MultipartFile.class);
        FotoOlehOlehForm form = new FotoOlehOlehForm();
        form.setFotoFile(mockFile);
        
        dummyOlehOleh.setFotoPath(null);

        when(repository.findById(itemId)).thenReturn(Optional.of(dummyOlehOleh));
        when(fileStorageService.storeFile(mockFile, itemId)).thenReturn("foto.jpg");
        when(repository.save(any(OlehOleh.class))).thenReturn(dummyOlehOleh);

        service.uploadFoto(itemId, form);
        
        verify(fileStorageService, times(1)).storeFile(mockFile, itemId);
        verify(repository, times(1)).save(dummyOlehOleh);
    }
    
    @Test
    void testCharts() {
        when(repository.countByKategori(userId)).thenReturn(new ArrayList<>());
        when(repository.countByProvinsi(userId)).thenReturn(new ArrayList<>());
        when(repository.avgHargaByKategori(userId)).thenReturn(new ArrayList<>());
        when(repository.countByRating(userId)).thenReturn(new ArrayList<>());

        service.getCountByKategori(userId);
        service.getCountByProvinsi(userId);
        service.getAvgHargaByKategori(userId);
        service.getCountByRating(userId);
        
        verify(repository, times(1)).countByKategori(userId);
    }

    // --- TEST TAMBAHAN SEBELUMNYA ---

    @Test
    void testGetAllOlehOleh_WithSearch() {
        String keyword = "Bakpia";
        when(repository.findByKeyword(userId, keyword)).thenReturn(Collections.singletonList(dummyOlehOleh));
        List<OlehOleh> res = service.getAllOlehOleh(userId, keyword);
        assertEquals(1, res.size());
    }

    @Test
    void testDeleteOlehOleh_WithPhoto_Success() {
        dummyOlehOleh.setFotoPath("gambar_lama.jpg");
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.of(dummyOlehOleh));
        
        boolean res = service.deleteOlehOleh(userId, itemId);
        
        assertTrue(res);
        verify(fileStorageService, times(1)).deleteFile("gambar_lama.jpg");
        verify(repository).deleteById(itemId);
    }

    @Test
    void testDeleteOlehOleh_NotFound() {
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.empty());
        boolean res = service.deleteOlehOleh(userId, itemId);
        assertFalse(res);
    }

    @Test
    void testUpdateOlehOleh_NotFound() {
        when(repository.findByUserIdAndId(userId, itemId)).thenReturn(Optional.empty());
        OlehOleh res = service.updateOlehOleh(userId, itemId, "Baru", "Jkt", "DKI", "Makanan", "Enak", 100.0, "Toko", 5, true);
        assertNull(res);
    }

    @Test
    void testUploadFoto_NotFound() {
        FotoOlehOlehForm form = new FotoOlehOlehForm();
        when(repository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.uploadFoto(itemId, form));
    }

    @Test
    void testUploadFoto_WithExistingPhoto() throws IOException {
        dummyOlehOleh.setFotoPath("foto_lama.jpg");
        MultipartFile mockFile = mock(MultipartFile.class);
        FotoOlehOlehForm form = new FotoOlehOlehForm();
        form.setFotoFile(mockFile);
        
        when(repository.findById(itemId)).thenReturn(Optional.of(dummyOlehOleh));
        when(fileStorageService.storeFile(mockFile, itemId)).thenReturn("foto_baru.jpg");
        
        service.uploadFoto(itemId, form);
        
        verify(fileStorageService, times(1)).deleteFile("foto_lama.jpg");
        verify(fileStorageService, times(1)).storeFile(mockFile, itemId);
    }

    @Test
    void testFilterMethods() {
        when(repository.findByKategori(userId, "Makanan")).thenReturn(Collections.emptyList());
        when(repository.findByProvinsi(userId, "DIY")).thenReturn(Collections.emptyList());
        service.getByKategori(userId, "Makanan");
        service.getByProvinsi(userId, "DIY");
        service.getByAsalDaerah(userId, "Jogja");
        service.getRekomendasi(userId);
        service.getByRating(userId, 5);
        verify(repository).findByKategori(userId, "Makanan");
    }

    // ===================================================================
    // TAMBAHAN BARU: UNTUK MENUTUP 2 BRANCH TERAKHIR (100% BRANCH)
    // ===================================================================

    // 1. Mengetes Search berupa SPASI/EMPTY STRING
    // Ini menutup branch: !search.trim().isEmpty() -> FALSE
    @Test
    void testGetAllOlehOleh_WithEmptyStringOrWhitespace() {
        // String berisi spasi dianggap kosong oleh trim().isEmpty()
        String search = "   "; 
        
        when(repository.findAllByUserId(userId)).thenReturn(new ArrayList<>());
        
        service.getAllOlehOleh(userId, search);
        
        // Verifikasi yang dipanggil adalah findAllByUserId (bukan findByKeyword)
        verify(repository).findAllByUserId(userId);
        verify(repository, never()).findByKeyword(any(), any());
    }

    // 2. Mengetes Upload Foto saat Path Foto adalah STRING KOSONG
    // Ini menutup branch: !olehOleh.getFotoPath().isEmpty() -> FALSE
    @Test
    void testUploadFoto_WithEmptyPathString() throws IOException {
        // Path tidak null, tapi kosong ""
        dummyOlehOleh.setFotoPath(""); 
        
        MultipartFile mockFile = mock(MultipartFile.class);
        FotoOlehOlehForm form = new FotoOlehOlehForm();
        form.setFotoFile(mockFile);

        when(repository.findById(itemId)).thenReturn(Optional.of(dummyOlehOleh));
        when(fileStorageService.storeFile(mockFile, itemId)).thenReturn("new.jpg");
        when(repository.save(any())).thenReturn(dummyOlehOleh);

        service.uploadFoto(itemId, form);

        // Pastikan deleteFile TIDAK dipanggil (karena path kosong dianggap tidak ada file)
        verify(fileStorageService, never()).deleteFile(anyString());
        verify(repository).save(dummyOlehOleh);
    }
}