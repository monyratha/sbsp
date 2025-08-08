package morning.com.services.auth.repository;

import morning.com.services.auth.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    List<RefreshToken> findAllByUserId(String userId);
    Optional<RefreshToken> findByIdAndUserId(String id, String userId);
    long deleteByUserId(String userId);
}
