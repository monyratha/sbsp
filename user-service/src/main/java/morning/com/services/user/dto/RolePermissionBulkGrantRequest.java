package morning.com.services.user.dto;

import java.util.List;

public record RolePermissionBulkGrantRequest(List<RolePermissionGrantChange> changes) {}
