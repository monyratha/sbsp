package morning.com.services.auth.service;

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
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository repository;
    private final Duration ttl;
    private final SecureRandom random = new SecureRandom();
    private static final String INVALID_REFRESH_TOKEN = "invalid.refresh.token";

    public RefreshTokenService(RefreshTokenRepository repository,
                               @Value("${security.refresh.ttl:P7D}") Duration ttl) {
        this.repository = repository;
        this.ttl = ttl;
    }

    public record Issued(String id, String rawToken) {}

    public record Rotation(String userId, Issued issued) {}

    public Issued issue(String userId, String ip, String userAgent) {
        String id = UUID.randomUUID().toString();
        String secret = randomSecret();
        String raw = id + "." + secret;
        String hash = sha256(raw);
        Instant expiresAt = Instant.now().plus(ttl);
        RefreshToken token = new RefreshToken(id, userId, hash, expiresAt, null, null,
                ip, truncate(userAgent));
        repository.save(token);
        return new Issued(id, raw);
    }

    public Rotation verifyAndRotate(String rawToken) {
        String[] parts = rawToken.split("\\.");
        if (parts.length != 2) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        String id = parts[0];
        String hash = sha256(rawToken);
        RefreshToken token = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(INVALID_REFRESH_TOKEN));
        Instant now = Instant.now();
        if (token.getRevokedAt() != null || now.isAfter(token.getExpiresAt())) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        if (!token.getTokenHash().equals(hash)) {
            throw new IllegalArgumentException(INVALID_REFRESH_TOKEN);
        }
        token.setRevokedAt(now);
        repository.save(token);
        Issued issued = issue(token.getUserId(), token.getCreatedIp(), token.getUserAgent());
        return new Rotation(token.getUserId(), issued);
    }

    private String randomSecret() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private String truncate(String ua) {
        if (ua == null) {
            return null;
        }
        return ua.length() <= 255 ? ua : ua.substring(0, 255);
    }
}
