package morning.com.services.auth.controller;

import morning.com.services.auth.dto.ApiResponse;
import morning.com.services.auth.dto.*;
import morning.com.services.auth.exception.AccountLockedException;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.RefreshTokenService;
import morning.com.services.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;


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

        MockHttpServletRequest http = new MockHttpServletRequest();
        http.setRemoteAddr("1.1.1.1");
        http.addHeader(HttpHeaders.USER_AGENT, "JUnit");
        when(userService.authenticate("user", "password")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("token123");
        when(jwtService.ttlMillis()).thenReturn(3_600_000L);
        when(userService.findUserIdByUsername("user")).thenReturn("uid1");
        when(refreshTokenService.issue(eq("uid1"), any(), any())).thenReturn(new RefreshTokenService.Issued("rid","refresh1"));

        long now = System.currentTimeMillis();
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request, http);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals(MessageKeys.SUCCESS, body.messageKey());
        assertNotNull(body.data());
        assertEquals("token123", body.data().token());
        assertEquals("Bearer", body.data().tokenType());
        assertTrue(body.data().expiresAt() > now);
        assertEquals("refresh1", body.data().refreshToken());
    }

    @Test
    void loginInvalidCredentials() {
        AuthRequest request = new AuthRequest("user", "wrong");
        when(userService.authenticate("user", "wrong")).thenReturn(false);
        MockHttpServletRequest http = new MockHttpServletRequest();
        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request, http);
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
        MockHttpServletRequest http = new MockHttpServletRequest();
        assertThrows(AccountLockedException.class, () -> authController.login(request, http));

    }

    @Test
    void refreshSuccess() {
        RefreshRequest request = new RefreshRequest("old");
        when(refreshTokenService.verifyAndRotate("old"))
                .thenReturn(new RefreshTokenService.Rotation("uid1", new RefreshTokenService.Issued("rid2","newRef")));
        when(userService.findUsernameById("uid1")).thenReturn("user");
        when(jwtService.generateToken("user")).thenReturn("token2");
        when(jwtService.ttlMillis()).thenReturn(3_600_000L);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(SUCCESS, body.status());
        assertEquals("token2", body.data().token());
        assertEquals("newRef", body.data().refreshToken());
    }

    @Test
    void refreshInvalid() {
        RefreshRequest request = new RefreshRequest("bad");
        when(refreshTokenService.verifyAndRotate("bad")).thenThrow(new IllegalArgumentException());

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.refresh(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(ERROR, body.status());
        assertEquals(MessageKeys.INVALID_CREDENTIALS, body.messageKey());
    }

    @Test
    void refreshReuseOldTokenFails() {
        RefreshRequest request = new RefreshRequest("reuse");
        when(refreshTokenService.verifyAndRotate("reuse"))
                .thenReturn(new RefreshTokenService.Rotation("uid1", new RefreshTokenService.Issued("rid2","newRef")))
                .thenThrow(new IllegalArgumentException());
        when(userService.findUsernameById("uid1")).thenReturn("user");
        when(jwtService.generateToken("user")).thenReturn("token2");
        when(jwtService.ttlMillis()).thenReturn(3_600_000L);

        ResponseEntity<ApiResponse<AuthResponse>> first = authController.refresh(request);
        assertEquals(HttpStatus.OK, first.getStatusCode());

        ResponseEntity<ApiResponse<AuthResponse>> second = authController.refresh(request);
        assertEquals(HttpStatus.UNAUTHORIZED, second.getStatusCode());
        ApiResponse<AuthResponse> body = second.getBody();
        assertNotNull(body);
        assertEquals(ERROR, body.status());
    }
}

