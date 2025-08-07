package morning.com.services.auth.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import morning.com.services.auth.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);
}

