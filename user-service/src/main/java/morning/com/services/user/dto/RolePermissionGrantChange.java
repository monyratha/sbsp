package morning.com.services.user.dto;

import java.util.UUID;

public record RolePermissionGrantChange(UUID roleId, UUID permissionId, boolean granted) {}
