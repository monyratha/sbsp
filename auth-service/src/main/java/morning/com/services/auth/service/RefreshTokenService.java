package morning.com.services.auth.service;

import jakarta.transaction.Transactional;
import morning.com.services.auth.entity.RefreshToken;
import morning.com.services.auth.entity.User;
import morning.com.services.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final Duration ttl;

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${security.refresh-token.ttl:P7D}") Duration ttl) {
        this.repository = repository;
        this.ttl = ttl;
    }

    @Transactional
    public RefreshToken create(User user) {
        var token = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiresAt(Instant.now().plus(ttl))
                .build();
        return repository.save(token);
    }

    @Transactional
    public Optional<RefreshToken> refresh(String token) {
        return repository.findByToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(rt -> {
                    repository.delete(rt);
                    return create(rt.getUser());
                });
    }
}
