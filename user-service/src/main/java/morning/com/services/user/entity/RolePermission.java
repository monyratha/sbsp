package morning.com.services.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.UUID;

/**
 * Simple entity representing the {@code role_permissions} join table.
 * <p>
 *   JPA requires repositories to be tied to managed entity types. The original
 *   implementation used {@link Object} as the domain type which caused
 *   Spring Data to fail startup with a "Not a managed type" error. By mapping
 *   the join table as an entity we allow Spring to create a repository proxy
 *   without loading the entire {@link Role} or {@link Permission} graph.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "role_permissions")
public class RolePermission {

    @EmbeddedId
    private Id id;

    /**
     * Composite identifier for a row in {@code role_permissions}.
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Embeddable
    public static class Id implements Serializable {

        @JdbcTypeCode(SqlTypes.BINARY)
        @Column(name = "role_id", columnDefinition = "BINARY(16)")
        private UUID roleId;

        @JdbcTypeCode(SqlTypes.BINARY)
        @Column(name = "permission_id", columnDefinition = "BINARY(16)")
        private UUID permissionId;
    }
}

