package morning.com.services.user.dto;

import java.util.UUID;

public record RolePermissionEdge(UUID roleId, UUID permissionId) {}
