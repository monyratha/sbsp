package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.dto.*;
import morning.com.services.user.entity.Role;
import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.mapper.RoleMapper;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.repository.RolePermissionRepository;
import morning.com.services.user.repository.RoleRepository;
import morning.com.services.user.repository.UserProfileRepository;
import morning.com.services.user.specification.RoleSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        if (roleRepository.existsByCode(request.code())) {
            throw new FieldValidationException("code", "already.exists");
        }
        if (roleRepository.existsByName(request.name())) {
            throw new FieldValidationException("name", "already.exists");
        }
        Role entity = mapper.toEntity(request);
        Role saved = roleRepository.save(entity);
        return mapper.toResponse(saved);
    }

    public Optional<Role> findById(UUID id) {
        return roleRepository.findById(id);
    }

    public Page<RoleResponse> search(String search, String code, String name, Pageable pageable) {
        Specification<Role> spec = Specification.allOf();
        if (search != null && !search.isBlank()) {
            spec = spec.and(RoleSpecification.searchInAll(search));
        }
        if (code != null && !code.isBlank()) {
            spec = spec.and(RoleSpecification.codeEquals(code));
        }
        if (name != null && !name.isBlank()) {
            spec = spec.and(RoleSpecification.nameEquals(name));
        }
        Page<Role> page = roleRepository.findAll(spec, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional
    public Optional<RoleResponse> update(UUID id, RoleUpdateRequest request) {
        return roleRepository.findById(id)
                .map(entity -> {
                    mapper.update(entity, request);
                    Role updated = roleRepository.save(entity);
                    return mapper.toResponse(updated);
                });
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!roleRepository.existsById(id)) {
            return false;
        }
        roleRepository.deleteById(id);
        return true;
    }

    @Transactional
    public UserProfile addRoleToUser(UUID userId, UUID roleId) {
        UserProfile user = userRepository.findById(userId).orElseThrow();
        Role role = roleRepository.findById(roleId).orElseThrow();
        user.getRoles().add(role);
        return user;
    }

    public MatrixResponse getMatrix() {
        List<RoleDTO> roles = roleRepository.findAllByOrderByName();
        List<PermissionDTO> permissions = permissionRepository.findAllByOrderBySectionAscLabelAsc();
        List<RolePermissionEdge> edges = rolePermissionRepository.findAllProjectedBy().stream()
                .map(e -> new RolePermissionEdge(e.getRoleId(), e.getPermissionId()))
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
    public void applyBulk(List<RolePermissionGrantChange> changes) {
        for (RolePermissionGrantChange change : changes) {
            setGrant(change.roleId(), change.permissionId(), change.granted());
        }
    }
}
