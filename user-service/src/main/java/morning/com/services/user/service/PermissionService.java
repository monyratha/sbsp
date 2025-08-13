package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.entity.Permission;
import morning.com.services.user.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService {
    private final PermissionRepository repository;

    public PermissionService(PermissionRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Permission add(Permission permission) {
        return repository.save(permission);
    }

    public Optional<Permission> findById(UUID id) {
        return repository.findById(id);
    }
}
