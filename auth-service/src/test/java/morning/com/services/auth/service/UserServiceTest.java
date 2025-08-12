package morning.com.services.auth.service;

import morning.com.services.auth.client.UserClient;
import morning.com.services.auth.client.UserProfile;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository repository;

    @Mock
    PasswordEncoder encoder;

    @Mock
    UserClient userClient;

    private UserService service;

    @BeforeEach
    void setUp() {
        service = new UserService(repository, encoder, userClient, 5, Duration.ofMinutes(5));
    }

    @Test
    void registerStoresCredentialsAndCreatesProfile() {
        when(repository.existsByUsername("user")).thenReturn(false);
        when(encoder.encode("pwd")).thenReturn("hash");
        when(repository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        service.register("user", "pwd");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(repository).save(userCaptor.capture());
        ArgumentCaptor<UserProfile> profileCaptor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userClient).add(profileCaptor.capture());

        assertEquals(userCaptor.getValue().getId(), profileCaptor.getValue().id());
        assertEquals("user", profileCaptor.getValue().username());
    }
}
