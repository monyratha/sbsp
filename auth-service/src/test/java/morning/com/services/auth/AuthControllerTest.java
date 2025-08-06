package morning.com.services.auth;

import morning.com.services.auth.model.AuthRequest;
import morning.com.services.auth.model.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void registerAndLogin() {
        AuthRequest request = new AuthRequest("user", "password");
        ResponseEntity<Void> registerResponse = restTemplate.postForEntity("/auth/register", request, Void.class);
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity("/auth/login", request, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().token());
        assertFalse(loginResponse.getBody().token().isBlank());
    }
}
