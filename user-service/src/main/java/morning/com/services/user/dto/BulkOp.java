package morning.com.services.user.dto;

import java.util.UUID;

public record BulkOp(UUID roleId, UUID permissionId, boolean granted) {}
