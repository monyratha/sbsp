package morning.com.services.user.repository;

import morning.com.services.user.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for managing entries in the {@code role_permissions} join table
 * without relying on native SQL queries. Grant and revoke operations are
 * implemented using standard JPA {@link JpaRepository} methods.
 */
public interface RolePermissionRepository extends JpaRepository<RolePermission, RolePermission.Id> {

    interface RolePermissionEdgeView {
        RolePermission.Id getId();

        default UUID getRoleId() {
            return getId().getRoleId();
        }

        default UUID getPermissionId() {
            return getId().getPermissionId();
        }
    }

    List<RolePermissionEdgeView> findAllProjectedBy();

    default void grant(UUID roleId, UUID permId) {
        save(new RolePermission(new RolePermission.Id(roleId, permId)));
    }

    default void revoke(UUID roleId, UUID permId) {
        deleteById(new RolePermission.Id(roleId, permId));
    }
}

