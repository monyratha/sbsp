package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "ux_users_username", columnList = "username", unique = true),
        @Index(name = "ux_users_email", columnList = "email", unique = true),
        @Index(name = "ix_users_lock_until", columnList = "lock_until"),
        @Index(name = "ix_users_enabled", columnList = "enabled"),
        @Index(name = "ix_users_last_login_at", columnList = "last_login_at")
})
public class User {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 190, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false)
    private boolean enabled = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;

    @Setter
    private Instant lastLoginAt;

    @Setter
    private Instant lastPasswordChangeAt;

    @Column(length = 16)
    private String locale;

    @Column(length = 64)
    private String timeZone;

    @Column(nullable = false)
    private boolean mfaEnabled;

    @Column(length = 128)
    private String totpSecret;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    @Setter
    @Column(nullable = false)
    private int failedAttempts;

    @Setter
    private Instant lockUntil;

    public enum Role {
        USER, ADMIN
    }
}