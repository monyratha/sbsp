package morning.com.services.auth.dto;

public record AuthResponse(
        String token,
        long expiresAtEpochMs,
        String tokenType
) {
    public AuthResponse(String token, long expiresAtEpochMs) {
        this(token, expiresAtEpochMs, "Bearer");
    }
}
