package morning.com.services.user.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO representing a Permission.
 */
public record PermissionResponse(
        UUID id,
        String code,
        String section,
        String label,
        Instant createdAt,
        Instant updatedAt
) {
}
