package morning.com.services.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import morning.com.services.user.model.User;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = {
        "spring.cloud.discovery.enabled=false",
        "spring.cloud.config.discovery.enabled=false"
    }
)
public class UserApplicationTests {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void findAll() {
        User[] users = restTemplate.getForObject("/user", User[].class);
        Assertions.assertTrue(users.length > 0);
    }

    @Test
    void findById() {
        User user = restTemplate.getForObject("/user/{id}", User.class, 1L);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(1L, user.getId());
    }

    @Test
    void add() {
        User user = new User("Alice", "alice@example.com");
        user = restTemplate.postForObject("/user", user, User.class);
        Assertions.assertNotNull(user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals("Alice", user.getName());
    }
}
