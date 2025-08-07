package morning.com.services.auth.service;

import morning.com.services.auth.dto.MessageKeys;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public void register(String username, String password) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException(MessageKeys.USERNAME_EXISTS);
        }
        String id = UUID.randomUUID().toString();
        String hash = encoder.encode(password);
        repository.save(new User(id, username, hash));
    }

    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(user -> encoder.matches(password, user.getPasswordHash()))
                .orElse(false);
    }
}
