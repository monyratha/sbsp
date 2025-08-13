package morning.com.services.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO for updating an existing Permission.
 * The {@code id} field is ignored by the service; the identifier should come from the path variable.
 */
public record PermissionUpdateRequest(
        UUID id,
        @NotBlank @Size(max = 100) String section,
        @NotBlank @Size(max = 100) String label
) {
}
