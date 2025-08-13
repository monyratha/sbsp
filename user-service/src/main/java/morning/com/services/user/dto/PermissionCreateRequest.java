package morning.com.services.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a Permission.
 */
public record PermissionCreateRequest(
        @NotBlank @Size(max = 100) String code,
        @NotBlank @Size(max = 100) String section,
        @NotBlank @Size(max = 100) String label
) {
}
