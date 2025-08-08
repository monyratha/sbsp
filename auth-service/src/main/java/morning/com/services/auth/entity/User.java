package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
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

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(unique = true, length = 190)
    private String email;

    @Column(nullable = false)
    private boolean emailVerified;

    @Column(nullable = false, length = 100)
    private String passwordHash;

    @Setter
    @Column(nullable = false)
    private int failedAttempts;

    @Setter
    private Instant lockUntil;

    @Column(nullable = false)
    private boolean enabled;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 32)
    private Set<Role> roles = new HashSet<>();

    public User(String id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.failedAttempts = 0;
        this.emailVerified = false;
        this.enabled = true;
        this.mfaEnabled = false;
    }

    public enum Role {
        USER,
        ADMIN
    }
}
