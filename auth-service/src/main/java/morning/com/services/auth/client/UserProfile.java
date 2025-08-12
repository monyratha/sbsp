package morning.com.services.auth.client;

/**
 * Profile representation sent to user-service when a new user registers.
 */
public record UserProfile(
        String id,
        String username,
        String email,
        String phone,
        String status,
        String tenantId
) {
}
