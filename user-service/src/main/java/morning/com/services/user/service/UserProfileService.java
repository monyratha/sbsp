package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.repository.UserProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserProfileService {
    private final UserProfileRepository repository;

    public UserProfileService(UserProfileRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UserProfile add(UserProfile profile) {
        return repository.save(profile);
    }

    public Optional<UserProfile> findById(UUID id) {
        return repository.findById(id);
    }
}
