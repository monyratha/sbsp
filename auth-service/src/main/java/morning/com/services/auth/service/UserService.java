package morning.com.services.auth.service;

import jakarta.transaction.Transactional;
import morning.com.services.auth.dto.MessageKeys;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.exception.AccountLockedException;
import morning.com.services.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PasswordEncoder encoder;
    private final int maxFailedAttempts;
    private final Duration lockDuration;

    public UserService(UserRepository repository,
                       PasswordEncoder encoder,
                       @Value("${security.login.max-failed-attempts:5}") int maxFailedAttempts,
                       @Value("${security.login.lock-duration:PT5M}") Duration lockDuration) {
        this.repository = repository;
        this.encoder = encoder;
        this.maxFailedAttempts = maxFailedAttempts;
        this.lockDuration = lockDuration;
    }

    @Transactional
    public void register(String username, String password) {
        String normalized = username.toLowerCase(Locale.ROOT);
        if (repository.existsByUsername(normalized)) {
            throw new IllegalArgumentException(MessageKeys.USERNAME_EXISTS);
        }
        String id = UUID.randomUUID().toString();
        String hash = encoder.encode(password);
        User user = new User(id, normalized, null, false, hash, 0, null,
                true, null, null, null, null, null, null, false, null,
                Set.of(User.Role.USER));
        repository.save(user);
    }

    public boolean authenticate(String username, String password) {
        return repository.findByUsername(username.toLowerCase(Locale.ROOT))
                .map(u -> {
                    Instant now = Instant.now();
                    if (u.getLockUntil() != null && now.isBefore(u.getLockUntil())) {
                        throw new AccountLockedException();
                    }
                    boolean matches = encoder.matches(password, u.getPasswordHash());
                    if (matches) {
                        u.setFailedAttempts(0);
                        u.setLockUntil(null);
                        u.setLastLoginAt(now);
                        repository.save(u);
                        return true;
                    }
                    int attempts = u.getFailedAttempts() + 1;
                    u.setFailedAttempts(attempts);
                    if (attempts >= maxFailedAttempts) {
                        u.setLockUntil(now.plus(lockDuration));
                        u.setFailedAttempts(0);
                        repository.save(u);
                        throw new AccountLockedException();
                    }
                    repository.save(u);
                    return false;
                })
                .orElse(false);
    }

    public Optional<User> findByUsername(String username) {
        return repository.findByUsername(username.toLowerCase(Locale.ROOT));
    }
}
