package morning.com.services.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a Role.
 */
public record RoleCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 255) String description
) {}

