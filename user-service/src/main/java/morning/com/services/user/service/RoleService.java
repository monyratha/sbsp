package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.dto.*;
import morning.com.services.user.entity.Role;
import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.mapper.RoleMapper;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.repository.RolePermissionRepository;
import morning.com.services.user.repository.RoleRepository;
import morning.com.services.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoleService {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserProfileRepository userRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleMapper mapper;

    public RoleService(RoleRepository roleRepository,
                       PermissionRepository permissionRepository,
                       UserProfileRepository userRepository,
                       RolePermissionRepository rolePermissionRepository,
                       RoleMapper mapper) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.mapper = mapper;
    }

    @Transactional
    public RoleResponse add(RoleCreateRequest request) {
        Role entity = mapper.toEntity(request);
        Role saved = roleRepository.save(entity);
        return mapper.toResponse(saved);
    }

    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }

    @Transactional
    public UserProfile addRoleToUser(UUID userId, UUID roleId) {
        UserProfile user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.getRoles().add(role);
        return user;
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
