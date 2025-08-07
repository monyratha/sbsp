package morning.com.services.auth.controller;

import morning.com.services.auth.dto.ApiResponse;
import morning.com.services.auth.dto.AuthRequest;
import morning.com.services.auth.dto.AuthResponse;
import morning.com.services.auth.dto.ResultEnum;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        assertEquals("success", body.getStatus());
        assertEquals(ResultEnum.USER_REGISTERED.getMessageKey(), body.getMessageKey());
        verify(userService).register("user", "password");
    }

    @Test
    void registerUsernameExists() {
        AuthRequest request = new AuthRequest("user", "password");
        doThrow(new IllegalArgumentException(ResultEnum.USERNAME_EXISTS.getMessageKey()))
                .when(userService).register("user", "password");

        ResponseEntity<ApiResponse<Void>> response = authController.register(request);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        ApiResponse<Void> body = response.getBody();
        assertNotNull(body);
        assertEquals("error", body.getStatus());
        assertEquals(ResultEnum.USERNAME_EXISTS.getMessageKey(), body.getMessageKey());
    }

    @Test
    void loginSuccess() {
        AuthRequest request = new AuthRequest("user", "password");
        when(userService.authenticate("user", "password")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("token123");

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals("success", body.getStatus());
        assertEquals(ResultEnum.SUCCESS.getMessageKey(), body.getMessageKey());
        assertNotNull(body.getData());
        assertEquals("token123", body.getData().token());
    }

    @Test
    void loginInvalidCredentials() {
        AuthRequest request = new AuthRequest("user", "wrong");
        when(userService.authenticate("user", "wrong")).thenReturn(false);

        ResponseEntity<ApiResponse<AuthResponse>> response = authController.login(request);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        ApiResponse<AuthResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals("error", body.getStatus());
        assertEquals(ResultEnum.INVALID_CREDENTIALS.getMessageKey(), body.getMessageKey());
        assertNull(body.getData());
    }
}
