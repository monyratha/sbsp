package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens",
        indexes = {
                @Index(name = "ix_refresh_user", columnList = "user_id"),
                @Index(name = "ix_refresh_expires", columnList = "expires_at")
        })
public class RefreshToken {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Column(nullable = false, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(length = 45)
    private String createdIp;

    @Column(length = 255)
    private String userAgent;
}
