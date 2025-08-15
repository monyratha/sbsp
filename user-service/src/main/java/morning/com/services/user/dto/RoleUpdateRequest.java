package morning.com.services.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Role.
 * Identifier is provided via the path variable.
 */
public record RoleUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description
) {
}

