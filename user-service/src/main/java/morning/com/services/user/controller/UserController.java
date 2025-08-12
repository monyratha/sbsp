package morning.com.services.user.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;
import morning.com.services.user.repository.UserRepository;

@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    UserRepository repository;

    @PostMapping
    public UserProfile add(@RequestBody UserProfile user) {
        LOGGER.info("User add: {}", user);
        return repository.add(user);
    }

    @GetMapping("/{id}")
    public UserProfile findById(@PathVariable("id") String id) {
        LOGGER.info("User find: id={}", id);
        return repository.findById(id);
    }

    @GetMapping
    public UserPage list(
            @RequestHeader("Authorization") String authorization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sort,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status) {
        String tenantId = extractTenantId(authorization);
        LOGGER.info("User list for tenant {}", tenantId);
        return repository.search(tenantId, q, status, page, size, sort);
    }

    private String extractTenantId(String authorization) {
        try {
            String token = authorization.substring(7); // strip 'Bearer '
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return null;
            }
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]),
                    StandardCharsets.UTF_8);
            JsonNode node = MAPPER.readTree(payload);
            return node.path("tenantId").asText();
        } catch (Exception e) {
            LOGGER.warn("Failed to extract tenantId from JWT", e);
            return null;
        }
    }
}

