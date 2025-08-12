package morning.com.services.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users_profile", indexes = {
        @Index(name = "ux_users_profile_username", columnList = "username", unique = true),
        @Index(name = "ux_users_profile_email", columnList = "email", unique = true),
        @Index(name = "ix_users_profile_tenant_id", columnList = "tenant_id"),
        @Index(name = "ix_users_profile_status", columnList = "status"),
        @Index(name = "ix_users_profile_phone", columnList = "phone")
})
public class UserProfile {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(length = 190, unique = true)
    private String email;

    @Column(length = 32)
    private String phone;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "tenant_id", nullable = false, length = 36)
    private String tenantId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
