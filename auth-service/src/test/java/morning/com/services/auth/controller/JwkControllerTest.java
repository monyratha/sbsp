package morning.com.services.auth.controller;

import morning.com.services.auth.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwkControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwkController jwkController;

    @Test
    void jwkEndpointReturnsValidJwkSet() {
        Map<String, Object> jwk = Map.of("kty", "RSA");
        when(jwtService.jwk()).thenReturn(jwk);

        Map<String, Object> result = jwkController.jwk();
        assertTrue(result.containsKey("keys"));
        Object keys = result.get("keys");
        assertTrue(keys instanceof List);
        List<?> list = (List<?>) keys;
        assertEquals(1, list.size());
        assertEquals(jwk, list.get(0));
    }
}
