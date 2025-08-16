package morning.com.services.auth.controller;

import morning.com.services.auth.service.JwtService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/.well-known")
public class JwkController {
    private final JwtService jwtService;

    public JwkController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/jwks.json")
    public Map<String, Object> keys() {
        return Map.of("keys", List.of(jwtService.jwk()));
    }
}

