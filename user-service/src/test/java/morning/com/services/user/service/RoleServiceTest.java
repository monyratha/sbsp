package morning.com.services.user.service;

import morning.com.services.user.dto.RolePermissionEdge;
import morning.com.services.user.dto.MatrixResponse;
import morning.com.services.user.dto.PermissionDTO;
import morning.com.services.user.dto.RoleCreateRequest;
import morning.com.services.user.dto.RoleDTO;
import morning.com.services.user.exception.FieldValidationException;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.repository.RolePermissionRepository;
import morning.com.services.user.repository.RoleRepository;
import morning.com.services.user.repository.UserProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    @Mock
    private UserProfileRepository userRepository;

    @Mock
    private morning.com.services.user.mapper.RoleMapper roleMapper;

    private RoleService service;

    @BeforeEach
    void setUp() {
        service = new RoleService(roleRepository, permissionRepository, userRepository, rolePermissionRepository, roleMapper);
    }

    @Test
    void addWhenNameExistsThrowsFieldValidationException() {
        RoleCreateRequest request = new RoleCreateRequest("admin", null);
        when(roleRepository.existsByName("admin")).thenReturn(true);

        assertThrows(FieldValidationException.class, () -> service.add(request));

        verify(roleRepository).existsByName("admin");
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void setGrantDelegatesToRepository() {
        UUID roleId = UUID.randomUUID();
        UUID permId = UUID.randomUUID();

        service.setGrant(roleId, permId, true);
        verify(rolePermissionRepository).grant(roleId, permId);

        service.setGrant(roleId, permId, false);
        verify(rolePermissionRepository).revoke(roleId, permId);

        verifyNoMoreInteractions(rolePermissionRepository);
    }

    @Test
    void getMatrixCombinesDataFromRepositories() {
        RoleDTO role = new RoleDTO(UUID.randomUUID(), "admin");
        PermissionDTO perm = new PermissionDTO(UUID.randomUUID(), "code", "sec", "label");

        RolePermissionRepository.RolePermissionEdgeView edge = mock(RolePermissionRepository.RolePermissionEdgeView.class);
        when(edge.getRoleId()).thenReturn(role.id());
        when(edge.getPermissionId()).thenReturn(perm.id());

        when(roleRepository.findAllByOrderByName()).thenReturn(List.of(role));
        when(permissionRepository.findAllByOrderBySectionAscLabelAsc()).thenReturn(List.of(perm));
        when(rolePermissionRepository.findAllProjectedBy()).thenReturn(List.of(edge));

        MatrixResponse matrix = service.getMatrix();

        assertEquals(List.of(role), matrix.roles());
        assertEquals(List.of(perm), matrix.permissions());
        assertEquals(List.of(new RolePermissionEdge(role.id(), perm.id())), matrix.grants());

        verify(roleRepository).findAllByOrderByName();
        verify(permissionRepository).findAllByOrderBySectionAscLabelAsc();
        verify(rolePermissionRepository).findAllProjectedBy();
    }
}

