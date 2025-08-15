package morning.com.services.auth.dto;

import java.util.UUID;

public record AuthResponse(
        UUID id,
        String username,
        String email,
        String locale,
        String accessToken,
        String tokenType,
        long expiresAt,
        String refreshToken
) {
    public AuthResponse(UUID id, String username, String email, String locale, String accessToken, long expiresAt, String refreshToken) {
        this(id, username, email, locale, accessToken, "Bearer", expiresAt, refreshToken);
    }
}
