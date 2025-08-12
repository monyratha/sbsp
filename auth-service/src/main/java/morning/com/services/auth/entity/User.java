package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(length = 190, unique = true)
    private String email;

    @Setter
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

    @Setter
    @Column(nullable = false)
    private int failedAttempts;

    @Setter
    private Instant lockUntil;
}