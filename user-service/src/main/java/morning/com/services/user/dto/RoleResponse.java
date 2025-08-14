package morning.com.services.user.dto;

import java.util.UUID;

/**
 * Response DTO representing a Role.
 */
public record RoleResponse(
        UUID id,
        String name,
        String description
) {}

