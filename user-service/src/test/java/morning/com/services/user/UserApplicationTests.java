package morning.com.services.user;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.config.discovery.enabled=false"
    }
)
public class UserApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    private HttpHeaders headersForTenant(String tenant) {
        String header = Base64.getUrlEncoder().withoutPadding()
                .encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(("{\"tenantId\":\"" + tenant + "\"}")
                        .getBytes(StandardCharsets.UTF_8));
        String token = header + "." + payload + ".";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        return headers;
    }

    @Test
    void findAll() {
        HttpEntity<Void> request = new HttpEntity<>(headersForTenant("t1"));
        ResponseEntity<UserPage> response = restTemplate.exchange(
                "/user", HttpMethod.GET, request, UserPage.class);
        Assertions.assertEquals(2, response.getBody().getTotal());
    }

    @Test
    void findById() {
        HttpEntity<Void> request = new HttpEntity<>(headersForTenant("t1"));
        ResponseEntity<UserProfile> response = restTemplate.exchange(
                "/user/{id}", HttpMethod.GET, request, UserProfile.class, 1L);
        UserProfile user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(1L, user.getId());
    }

    @Test
    void add() {
        UserProfile user = new UserProfile("Alice", "alice@example.com",
                "444-4444", "ACTIVE", "t1");
        user = restTemplate.postForObject("/user", user, UserProfile.class);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("Alice", user.getUsername());
    }
}
