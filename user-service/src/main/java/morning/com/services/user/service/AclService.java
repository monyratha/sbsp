package morning.com.services.user.service;

import morning.com.services.user.dto.*;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.repository.RolePermissionRepository;
import morning.com.services.user.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AclService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final RolePermissionRepository rolePermissionRepository;

    public AclService(RoleRepository roleRepository,
                      PermissionRepository permissionRepository,
                      RolePermissionRepository rolePermissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.rolePermissionRepository = rolePermissionRepository;
    }

    public MatrixResponse getMatrix() {
        List<RoleDTO> roles = roleRepository.findAllProjectedBy();
        List<PermissionDTO> permissions = permissionRepository.findAllProjectedByOrderBySectionAscLabelAsc();
        List<Edge> edges = rolePermissionRepository.findAllEdges().stream()
                .map(e -> new Edge(e.getRoleId(), e.getPermissionId()))
                .toList();
        return new MatrixResponse(roles, permissions, edges);
    }

    @Transactional
    public void setGrant(UUID roleId, UUID permId, boolean granted) {
        if (granted) {
            rolePermissionRepository.grant(roleId, permId);
        } else {
            rolePermissionRepository.revoke(roleId, permId);
        }
    }

    @Transactional
    public void applyBulk(List<BulkOp> ops) {
        for (BulkOp op : ops) {
            setGrant(op.roleId(), op.permissionId(), op.granted());
        }
    }
}
