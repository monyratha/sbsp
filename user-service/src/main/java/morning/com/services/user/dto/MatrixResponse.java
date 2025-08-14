package morning.com.services.user.dto;

import java.util.List;

public record MatrixResponse(List<RoleDTO> roles, List<PermissionDTO> permissions, List<RolePermissionEdge> grants) {}
