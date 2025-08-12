package morning.com.services.auth.client;

import java.util.UUID;

/**
 * Profile representation sent to user-service when a new user registers.
 */
public record UserProfile(
        UUID userId,
        String username,
        String email,
        String phone,
        boolean status
) {
}
