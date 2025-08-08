package morning.com.services.auth.service;

import jakarta.transaction.Transactional;
import morning.com.services.auth.dto.MessageKeys;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final Duration LOCK_DURATION = Duration.ofMinutes(5);

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
        repository.save(new User(id, username, hash, 0, null));
    }

    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username)
                .map(u -> {
                    Instant now = Instant.now();
                    if (u.getLockUntil() != null && now.isBefore(u.getLockUntil())) {
                        return false;
                    }
                    boolean matches = encoder.matches(password, u.getPasswordHash());
                    if (matches) {
                        u.setFailedAttempts(0);
                        u.setLockUntil(null);
                    } else {
                        int attempts = u.getFailedAttempts() + 1;
                        u.setFailedAttempts(attempts);
                        if (attempts >= MAX_FAILED_ATTEMPTS) {
                            u.setLockUntil(now.plus(LOCK_DURATION));
                            u.setFailedAttempts(0);
                        }
                    }
                    repository.save(u);
                    return matches;
                })
                .orElse(false);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}