package morning.com.services.auth.service;

import jakarta.transaction.Transactional;
import morning.com.services.auth.dto.MessageKeys;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repository, PasswordEncoder encoder) {
        this.repository = repository;
        this.encoder = encoder;
    }

    @Transactional
    public void register(String username, String password) {
        if (repository.existsByUsername(username)) {
            throw new IllegalArgumentException(MessageKeys.USERNAME_EXISTS);
        }
        var id = UUID.randomUUID().toString();
        var hash = encoder.encode(password);
        repository.save(new User(id, username, hash));
    }

    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(u -> encoder.matches(password, u.getPasswordHash()))
                .orElse(false);
    }
}