package morning.com.services.auth.controller;

import morning.com.services.auth.dto.ApiResponse;
import morning.com.services.auth.dto.*;
import morning.com.services.auth.exception.AccountLockedException;
import morning.com.services.auth.entity.RefreshToken;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.RefreshTokenService;
import morning.com.services.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static morning.com.services.auth.dto.ApiResponse.Status.ERROR;
import static morning.com.services.auth.dto.ApiResponse.Status.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerSuccess() {
        RegisterRequest request = new RegisterRequest("user", "user@example.com", "password");

        ResponseEntity<ApiResponse<Void>> response = authController.register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals(MessageKeys.USER_REGISTERED, body.messageKey());
        verify(userService).register("user", "user@example.com", "password");
    }

    @Test
    void registerUsernameExists() {
        RegisterRequest request = new RegisterRequest("user", "user@example.com", "password");
        doThrow(new IllegalArgumentException(MessageKeys.USERNAME_EXISTS))
                .when(userService).register("user", "user@example.com", "password");

        ResponseEntity<ApiResponse<Void>> response = authController.register(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());

        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertEquals(ERROR, body.status());
        assertEquals(MessageKeys.USERNAME_EXISTS, body.messageKey());
    }

    @Test
    void loginSuccess() {
        AuthRequest request = new AuthRequest("user", "password");

        when(userService.authenticate("user", "password")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("token123");

        long exp = System.currentTimeMillis() + 3_600_000L; // +1h
        when(jwtService.getExpiration("token123")).thenReturn(new Date(exp));

        User user = User.builder().id("1").username("user").email("user@example.com").passwordHash("hash").build();
        when(userService.findByUsername("user")).thenReturn(Optional.of(user));

        Instant rExp = Instant.now().plusSeconds(7200);
        RefreshToken rt = RefreshToken.builder().id("r1").user(user).token("refresh123").expiresAt(rExp).build();
        when(refreshTokenService.create(user)).thenReturn(rt);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals(MessageKeys.SUCCESS, body.messageKey());
        assertNotNull(body.data());
        assertEquals("token123", body.data().token());
        assertEquals(exp, body.data().expiresAtEpochMs());
        assertEquals("Bearer", body.data().tokenType());
        assertEquals("refresh123", body.data().refreshToken());
        assertEquals(rExp.toEpochMilli(), body.data().refreshExpiresAtEpochMs());
    }

    @Test
    void loginInvalidCredentials() {
        AuthRequest request = new AuthRequest("user", "wrong");
        when(userService.authenticate("user", "wrong")).thenReturn(false);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(ERROR, body.status());
        assertEquals(MessageKeys.INVALID_CREDENTIALS, body.messageKey());
        assertNull(body.data());
    }

    @Test
    void loginAccountLocked() {
        AuthRequest request = new AuthRequest("user", "password");
        when(userService.authenticate("user", "password")).thenThrow(new AccountLockedException());

        assertThrows(AccountLockedException.class, () -> authController.login(request));
    }

    @Test
    void refreshSuccess() {
        RefreshRequest request = new RefreshRequest("refresh123");
        User user = User.builder().id("1").username("user").email("user@example.com").passwordHash("hash").build();
        Instant rExp = Instant.now().plusSeconds(7200);
        RefreshToken newRt = RefreshToken.builder().id("r2").user(user).token("refresh456").expiresAt(rExp).build();

        when(refreshTokenService.refresh("refresh123")).thenReturn(Optional.of(newRt));
        when(jwtService.generateToken("user")).thenReturn("token456");
        long exp = System.currentTimeMillis() + 3_600_000L;
        when(jwtService.getExpiration("token456")).thenReturn(new Date(exp));

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals("token456", body.data().token());
        assertEquals("refresh456", body.data().refreshToken());
    }

    @Test
    void refreshInvalidToken() {
        RefreshRequest request = new RefreshRequest("bad");
        when(refreshTokenService.refresh("bad")).thenReturn(Optional.empty());

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(ERROR, body.status());
    }
}

