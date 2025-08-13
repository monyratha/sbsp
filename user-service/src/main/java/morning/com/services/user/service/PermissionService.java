package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.dto.PermissionCreateRequest;
import morning.com.services.user.dto.PermissionUpdateRequest;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.entity.Permission;
import morning.com.services.user.mapper.PermissionMapper;
import morning.com.services.user.repository.PermissionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class PermissionService {
    private final PermissionRepository repository;
    private final PermissionMapper mapper;

    public PermissionService(PermissionRepository repository, PermissionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public PermissionResponse add(PermissionCreateRequest request) {
        Permission entity = mapper.toEntity(request);
        Permission saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    @Transactional
    public Optional<PermissionResponse> update(UUID id, PermissionUpdateRequest request) {
        return repository.findById(id)
                .map(entity -> {
                    mapper.update(entity, request);
                    Permission updated = repository.save(entity);
                    return mapper.toResponse(updated);
                });
    }

    public Optional<Permission> findById(UUID id) {
        return repository.findById(id);
    }
}
