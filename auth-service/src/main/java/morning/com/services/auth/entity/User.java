package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_users_username", columnNames = "username"),
                @UniqueConstraint(name = "ux_users_email", columnNames = "email")
        },
        indexes = {
                @Index(name = "ix_users_lock_until", columnList = "lock_until"),
                @Index(name = "ix_users_enabled", columnList = "enabled"),
                @Index(name = "ix_users_last_login_at", columnList = "last_login_at")
        })
public class User {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    private String username;

    @Column(length = 190)
    private String email;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @Setter
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Setter
    @Column(name = "lock_until")
    private Instant lockUntil;

    @Column(nullable = false)
    private boolean enabled;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Setter
    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Setter
    @Column(name = "last_password_change_at")
    private Instant lastPasswordChangeAt;

    @Column(length = 16)
    private String locale;

    @Column(name = "time_zone", length = 64)
    private String timeZone;

    @Column(name = "mfa_enabled", nullable = false)
    private boolean mfaEnabled;

    @Column(name = "totp_secret", length = 128)
    private String totpSecret;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", nullable = false, length = 32)
    private Set<Role> roles;

    public enum Role {
        USER, ADMIN
    }
}
