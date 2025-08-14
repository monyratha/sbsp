package morning.com.services.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing Permission.
 * The identifier is provided via the path variable; this request contains only updatable fields.
 */
public record PermissionUpdateRequest(
        @NotBlank @Size(max = 100) String section,
        @NotBlank @Size(max = 100) String label
) {
}
