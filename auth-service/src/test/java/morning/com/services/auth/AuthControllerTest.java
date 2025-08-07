package morning.com.services.auth;

import morning.com.services.auth.controller.AuthController;
import morning.com.services.auth.model.AuthRequest;
import morning.com.services.auth.model.AuthResponse;
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
    void registerAndLogin() {
        AuthRequest request = new AuthRequest("user", "password");

        ResponseEntity<Void> registerResponse = authController.register(request);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        verify(userService).register("user", "password");

        when(userService.authenticate("user", "password")).thenReturn(true);
        when(jwtService.generateToken("user")).thenReturn("token123");

        ResponseEntity<AuthResponse> loginResponse = authController.login(request);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertEquals("token123", loginResponse.getBody().token());
    }
}
