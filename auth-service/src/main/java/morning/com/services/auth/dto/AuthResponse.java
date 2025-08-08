package morning.com.services.auth.dto;

public record AuthResponse(
        String token,
        long expiresAtEpochMs,
        String tokenType,
        String refreshToken,
        long refreshExpiresAtEpochMs
) {
    public AuthResponse(String token, long expiresAtEpochMs, String refreshToken, long refreshExpiresAtEpochMs) {
        this(token, expiresAtEpochMs, "Bearer", refreshToken, refreshExpiresAtEpochMs);
    }
}
