package morning.com.services.user;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;

import morning.com.services.user.model.UserPage;
import morning.com.services.user.model.UserProfile;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.config.discovery.enabled=false"
    }
)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
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
        restTemplate.postForObject("/user", new UserProfile("john",
                "john.smith@example.com", "111-1111", "ACTIVE", "t1"),
                UserProfile.class);
        restTemplate.postForObject("/user", new UserProfile("jane",
                "jane.doe@example.com", "222-2222", "INACTIVE", "t1"),
                UserProfile.class);

        HttpEntity<Void> request = new HttpEntity<>(headersForTenant("t1"));
        ResponseEntity<UserPage> response = restTemplate.exchange(
                "/user", HttpMethod.GET, request, UserPage.class);
        Assertions.assertEquals(2, response.getBody().getTotal());
    }

    @Test
    void findById() {
        UserProfile created = restTemplate.postForObject("/user",
                new UserProfile("bob", "bob@example.com", "333-3333",
                        "ACTIVE", "t1"), UserProfile.class);

        HttpEntity<Void> request = new HttpEntity<>(headersForTenant("t1"));
        ResponseEntity<UserProfile> response = restTemplate.exchange(
                "/user/{id}", HttpMethod.GET, request, UserProfile.class,
                created.getId());
        UserProfile user = response.getBody();
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(created.getId(), user.getId());
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
