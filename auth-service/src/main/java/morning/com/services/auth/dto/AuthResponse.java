package morning.com.services.auth.dto;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresAt,
        String refreshToken
) {
    public AuthResponse(String token, long expiresAt, String refreshToken) {
        this(token, "Bearer", expiresAt, refreshToken);
    }
}
