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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository repository;

    private RefreshTokenService service;

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(repository, "PT1H");
    }

    @Test
    void issueAndVerifyRotate() {
        // Stub only what this test needs:
        // 1) save echoes the entity
        when(repository.save(any(RefreshToken.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // 2) saveAndFlush assigns UUIDs
        when(repository.saveAndFlush(any(RefreshToken.class)))
                .thenAnswer(inv -> {
                    RefreshToken rt = inv.getArgument(0);
                    if (rt.getId() == null) rt.setId(UUID.randomUUID());
                    return rt;
                });

        UUID userId = UUID.randomUUID();
        RefreshTokenService.Issued issued = service.issue(userId, "ip", "ua");

        // Make repository able to find the stored token
        RefreshToken stored = new RefreshToken(
                issued.id(), userId,
                RefreshTokenService.sha256(issued.rawToken()),
                Instant.now().plus(Duration.ofHours(1)),
                null, null, "ip", "ua"
        );
        when(repository.findById(issued.id())).thenReturn(Optional.of(stored));

        // Rotate (creates id 2)
        RefreshTokenService.Rotation rotation = service.verifyAndRotate(issued.rawToken());

        assertEquals(userId, rotation.userId());
        assertNotEquals(issued.id(), rotation.issued().id());

        // Calls: saveAndFlush twice (create old/new), save thrice (hash old, revoke old, hash new)
        verify(repository, times(2)).saveAndFlush(any(RefreshToken.class));
        verify(repository, times(3)).save(any(RefreshToken.class));
        verify(repository).findById(issued.id());
        verifyNoMoreInteractions(repository);
    }

    @Test
    void invalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate("bad"));
        verifyNoInteractions(repository);
    }

    @Test
    void expiredToken() {
        UUID id = UUID.randomUUID();
        String secret = "secret";
        String raw = id + "." + secret;
        RefreshToken token = new RefreshToken(
                id, UUID.randomUUID(), RefreshTokenService.sha256(raw),
                Instant.now().minusSeconds(1), null, null, "ip", "ua"
        );
        when(repository.findById(id)).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void revokedToken() {
        UUID id = UUID.randomUUID();
        String secret = "secret";
        String raw = id + "." + secret;
        RefreshToken token = new RefreshToken(
                id, UUID.randomUUID(), RefreshTokenService.sha256(raw),
                Instant.now().plusSeconds(3600), Instant.now(), null, "ip", "ua"
        );
        when(repository.findById(id)).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void mismatchedHash() {
        UUID id = UUID.randomUUID();
        String secret = "secret";
        String raw = id + "." + secret;
        RefreshToken token = new RefreshToken(
                id, UUID.randomUUID(), "different-hash",
                Instant.now().plusSeconds(3600), null, null, "ip", "ua"
        );
        when(repository.findById(id)).thenReturn(Optional.of(token));

        assertThrows(IllegalArgumentException.class, () -> service.verifyAndRotate(raw));
        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }
}
