package morning.com.services.user.repository;

import morning.com.services.user.entity.RolePermission;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends Repository<RolePermission, RolePermission.Id> {

    interface RolePermissionEdgeView {
        UUID getRoleId();
        UUID getPermissionId();
    }

    @Query("select rp.id.roleId as roleId, rp.id.permissionId as permissionId from RolePermission rp")
    List<RolePermissionEdgeView> findAllRolePermissionEdges();

    @Modifying
    @Query(value = "insert into role_permissions(role_id, permission_id) values (?1, ?2) on duplicate key update permission_id = permission_id", nativeQuery = true)
    void grant(UUID roleId, UUID permId);

    @Modifying
    @Query(value = "delete from role_permissions where role_id = ?1 and permission_id = ?2", nativeQuery = true)
    void revoke(UUID roleId, UUID permId);
}
