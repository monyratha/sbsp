package morning.com.services.user.service;

import morning.com.services.user.dto.Edge;
import morning.com.services.user.dto.MatrixResponse;
import morning.com.services.user.dto.PermissionDTO;
import morning.com.services.user.dto.RoleDTO;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.repository.RolePermissionRepository;
import morning.com.services.user.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AclServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RolePermissionRepository rolePermissionRepository;

    private AclService service;

    @BeforeEach
    void setUp() {
        service = new AclService(roleRepository, permissionRepository, rolePermissionRepository);
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

        RolePermissionRepository.EdgeView edge = mock(RolePermissionRepository.EdgeView.class);
        when(edge.getRoleId()).thenReturn(role.id());
        when(edge.getPermissionId()).thenReturn(perm.id());

        when(roleRepository.findAllProjectedBy()).thenReturn(List.of(role));
        when(permissionRepository.findAllProjectedByOrderBySectionAscLabelAsc()).thenReturn(List.of(perm));
        when(rolePermissionRepository.findAllEdges()).thenReturn(List.of(edge));

        MatrixResponse matrix = service.getMatrix();

        assertEquals(List.of(role), matrix.roles());
        assertEquals(List.of(perm), matrix.permissions());
        assertEquals(List.of(new Edge(role.id(), perm.id())), matrix.grants());

        verify(roleRepository).findAllProjectedBy();
        verify(permissionRepository).findAllProjectedByOrderBySectionAscLabelAsc();
        verify(rolePermissionRepository).findAllEdges();
    }
}

