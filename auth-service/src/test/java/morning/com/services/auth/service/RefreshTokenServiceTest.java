package morning.com.services.auth.service;

import morning.com.services.auth.entity.RefreshToken;
import morning.com.services.auth.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository, Duration.ofHours(1));
    }

    @Test
    void issueAndVerifyRotate() {
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        RefreshTokenService.Issued issued = service.issue("u1", "ip", "ua");
        RefreshToken stored = new RefreshToken(issued.id(), "u1",
                RefreshTokenService.sha256(issued.rawToken()),
                Instant.now().plus(Duration.ofHours(1)), null, null, "ip", "ua");
        when(repository.findById(issued.id())).thenReturn(Optional.of(stored));
        when(repository.save(any(RefreshToken.class))).thenAnswer(inv -> inv.getArgument(0));

        RefreshTokenService.Rotation rotation = service.verifyAndRotate(issued.rawToken());
        assertEquals("u1", rotation.userId());
        assertNotEquals(issued.id(), rotation.issued().id());
        // Expect three save operations:
        // 1. initial token issue,
        // 2. revoking the old token during rotation,
        // 3. persisting the newly issued token
        verify(repository, times(3)).save(any());
    }

    @Test
    void invalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate("bad"));
    }

    @Test
    void expiredToken() {
        String raw = "id.secret";
        RefreshToken token = new RefreshToken("id", "u1",
                RefreshTokenService.sha256(raw),
                Instant.now().minusSeconds(1), null, null, "ip", "ua");
        when(repository.findById("id")).thenReturn(Optional.of(token));
        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
    }

    @Test
    void revokedToken() {
        String raw = "id.secret";
        RefreshToken token = new RefreshToken("id", "u1",
                RefreshTokenService.sha256(raw),
                Instant.now().plusSeconds(3600), Instant.now(), null, "ip", "ua");
        when(repository.findById("id")).thenReturn(Optional.of(token));
        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
    }

    @Test
    void mismatchedHash() {
        String raw = "id.secret";
        RefreshToken token = new RefreshToken("id", "u1",
                "otherhash",
                Instant.now().plusSeconds(3600), null, null, "ip", "ua");
        when(repository.findById("id")).thenReturn(Optional.of(token));
        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
    }
}
