package morning.com.services.auth.service;

import jakarta.transaction.Transactional;
import morning.com.services.auth.entity.RefreshToken;
import morning.com.services.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final Duration ttl;
    private final SecureRandom random = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${security.refresh.ttl:P7D}") String ttl) {
        this.repository = repository;
        this.ttl = Duration.parse(ttl);
    }

    public record Issued(Long id, String rawToken) {}
    public record Rotation(String userId, Issued issued) {}

    public Issued issue(String userId, String ip, String userAgent) {
        Instant expiresAt = Instant.now().plus(ttl);

        // Insert with a non-null placeholder hash so NOT NULL is satisfied.
        RefreshToken token = RefreshToken.builder()
                .userId(userId)
                .tokenHash("-")
                .expiresAt(expiresAt)
                .createdIp(ip)
                .userAgent(truncate(userAgent))
                .build();

        token = repository.saveAndFlush(token);

        // Build the raw token and final hash using the numeric id.
        String secret = randomSecret();
        String raw = token.getId() + "." + secret;
        token.setTokenHash(sha256(raw));
        repository.save(token);

        return new Issued(token.getId(), raw);
    }

    @Transactional
    public Rotation verifyAndRotate(String rawToken) {
        long id = parseId(rawToken);
        String expectedHash = sha256(rawToken);

        RefreshToken token = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invalid.refresh.token"));

        Instant now = Instant.now();
        if (token.getRevokedAt() != null || now.isAfter(token.getExpiresAt())) {
            throw new IllegalArgumentException("invalid.refresh.token");
        }
        if (!expectedHash.equals(token.getTokenHash())) {
            throw new IllegalArgumentException("invalid.refresh.token");
        }

        // Revoke current and issue a new one
        token.setRevokedAt(now);
        repository.save(token);

        Issued issued = issue(token.getUserId(), token.getCreatedIp(), token.getUserAgent());
        return new Rotation(token.getUserId(), issued);
    }

    @Transactional
    public void revoke(String rawToken) {
        long id = parseId(rawToken);
        String expectedHash = sha256(rawToken);

        RefreshToken token = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("invalid.refresh.token"));

        Instant now = Instant.now();
        if (token.getRevokedAt() != null || now.isAfter(token.getExpiresAt())) {
            throw new IllegalArgumentException("invalid.refresh.token");
        }
        if (!expectedHash.equals(token.getTokenHash())) {
            throw new IllegalArgumentException("invalid.refresh.token");
        }

        token.setRevokedAt(now);
        repository.save(token);
    }

    private static long parseId(String raw) {
        String[] parts = raw.split("\\.");
        if (parts.length != 2) throw new IllegalArgumentException("invalid.refresh.token");
        try {
            return Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("invalid.refresh.token");
        }
    }

    private String randomSecret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String truncate(String ua) {
        if (ua == null) return null;
        return ua.length() <= 255 ? ua : ua.substring(0, 255);
    }
}
