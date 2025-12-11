package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.delcom.app.entities.AuthToken;
import org.delcom.app.repositories.AuthTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthTokenServiceTest {

    @Mock
    private AuthTokenRepository authTokenRepository;

    @InjectMocks
    private AuthTokenService authTokenService;

    private AuthToken mockAuthToken;
    private UUID userId;
    private String tokenString;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        tokenString = "dummy-token-abc";
        
        mockAuthToken = new AuthToken();
        // Asumsi di Entity AuthToken ada method setUserId dan setToken
        // Jika nama method setternya beda, sesuaikan di sini
        mockAuthToken.setUserId(userId);
        mockAuthToken.setToken(tokenString);
    }

    // ==========================================
    // 1. TEST FIND USER TOKEN
    // ==========================================
    @Test
    void testFindUserToken() {
        // PENTING: Nama method harus sama persis dengan di Service kamu: findUserToken
        when(authTokenRepository.findUserToken(userId, tokenString))
            .thenReturn(mockAuthToken);

        // Act
        AuthToken result = authTokenService.findUserToken(userId, tokenString);

        // Assert
        assertNotNull(result);
        assertEquals(tokenString, result.getToken());
        
        // Verifikasi bahwa method yang dipanggil repository adalah findUserToken
        verify(authTokenRepository, times(1)).findUserToken(userId, tokenString);
    }

    // ==========================================
    // 2. TEST CREATE AUTH TOKEN
    // ==========================================
    @Test
    void testCreateAuthToken() {
        // Arrange
        when(authTokenRepository.save(any(AuthToken.class))).thenReturn(mockAuthToken);

        // Act
        AuthToken result = authTokenService.createAuthToken(mockAuthToken);

        // Assert
        assertNotNull(result);
        verify(authTokenRepository, times(1)).save(mockAuthToken);
    }

    // ==========================================
    // 3. TEST DELETE AUTH TOKEN
    // ==========================================
    @Test
    void testDeleteAuthToken() {
        // Arrange: Karena method void, kita tidak perlu 'when', cukup verify nanti
        // (Secara default Mockito doNothing() untuk method void)

        // Act
        authTokenService.deleteAuthToken(userId);

        // Assert
        // Pastikan service memanggil repository.deleteByUserId
        verify(authTokenRepository, times(1)).deleteByUserId(userId);
    }
}