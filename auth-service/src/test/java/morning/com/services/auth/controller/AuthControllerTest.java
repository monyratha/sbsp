package morning.com.services.auth.controller;

import morning.com.services.auth.dto.ApiResponse;
import morning.com.services.auth.dto.AuthRequest;
import morning.com.services.auth.dto.AuthResponse;
import morning.com.services.auth.dto.MessageKeys;
import morning.com.services.auth.exception.AccountLockedException;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

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

    @InjectMocks
    private AuthController authController;

    @Test
    void registerSuccess() {
        AuthRequest request = new AuthRequest("user", "password");

        ResponseEntity<ApiResponse<Void>> response = authController.register(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals(MessageKeys.USER_REGISTERED, body.messageKey());
        verify(userService).register("user", "password");
    }

    @Test
    void registerUsernameExists() {
        AuthRequest request = new AuthRequest("user", "password");
        doThrow(new IllegalArgumentException(MessageKeys.USERNAME_EXISTS))
                .when(userService).register("user", "password");

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
}

