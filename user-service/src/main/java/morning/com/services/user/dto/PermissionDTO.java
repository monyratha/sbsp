package morning.com.services.user.dto;

import java.util.UUID;

public record PermissionDTO(UUID id, String code, String section, String label) {}
