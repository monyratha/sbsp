package morning.com.services.user.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository extends Repository<Object, UUID> {

    interface EdgeView {
        UUID getRoleId();
        UUID getPermissionId();
    }

    @Query(value = "select role_id as roleId, permission_id as permissionId from role_permissions", nativeQuery = true)
    List<EdgeView> findAllEdges();

    @Modifying
    @Query(value = "insert into role_permissions(role_id, permission_id) values (?1, ?2) on duplicate key update permission_id = permission_id", nativeQuery = true)
    void grant(UUID roleId, UUID permId);

    @Modifying
    @Query(value = "delete from role_permissions where role_id = ?1 and permission_id = ?2", nativeQuery = true)
    void revoke(UUID roleId, UUID permId);
}
