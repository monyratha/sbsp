package morning.com.services.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(name = "ix_refresh_user_id", columnList = "user_id"),
        @Index(name = "ix_refresh_expires_at", columnList = "expires_at")
})
public class RefreshToken {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(length = 128, nullable = false)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    @Setter
    private Instant revokedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(length = 45)
    private String createdIp;

    @Column(length = 255)
    private String userAgent;
}
