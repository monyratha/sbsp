package morning.com.services.auth.service;

import morning.com.services.auth.config.SecurityBeans;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserService.class, SecurityBeans.class})
@TestPropertySource(properties = "spring.flyway.enabled=false")
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerPopulatesDefaults() {
        userService.register("User", "password");
        Optional<User> opt = userRepository.findByUsername("user");
        assertTrue(opt.isPresent());
        User u = opt.get();
        assertEquals("user", u.getUsername());
        assertTrue(u.isEnabled());
        assertFalse(u.isEmailVerified());
        assertEquals(0, u.getFailedAttempts());
        assertNotNull(u.getCreatedAt());
        assertNotNull(u.getUpdatedAt());
        assertTrue(u.getRoles().contains(User.Role.USER));
    }

    @Test
    void authenticateUpdatesLastLogin() {
        userService.register("user", "password");
        assertTrue(userService.authenticate("user", "password"));
        User u = userRepository.findByUsername("user").orElseThrow();
        assertNotNull(u.getLastLoginAt());
    }
}
