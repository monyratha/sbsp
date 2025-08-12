package morning.com.services.user.repository;

import morning.com.services.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, String> {
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserProfile> findByUsername(String username);
}
