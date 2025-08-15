package morning.com.services.user.service;

import jakarta.transaction.Transactional;
import morning.com.services.user.dto.PermissionCreateRequest;
import morning.com.services.user.dto.PermissionUpdateRequest;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.entity.Permission;
import morning.com.services.user.mapper.PermissionMapper;
import morning.com.services.user.repository.PermissionRepository;
import morning.com.services.user.specification.PermissionSpecification;
import org.springframework.data.jpa.domain.Specification;
import morning.com.services.user.exception.FieldValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
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
        if (repository.existsByCode(request.code())) {
            throw new FieldValidationException("code", "already.exists");
        }
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

    public Page<PermissionResponse> search(String search, String section, String code, Pageable pageable) {
        Specification<Permission> spec = Specification.allOf();
        if (search != null && !search.isBlank()) {
            spec = spec.and(PermissionSpecification.searchInAll(search));
        }
        if (section != null && !section.isBlank()) {
            spec = spec.and(PermissionSpecification.sectionEquals(section));
        }
        if (code != null && !code.isBlank()) {
            spec = spec.and(PermissionSpecification.codeEquals(code));
        }
        Page<Permission> page = repository.findAll(spec, pageable);
        return page.map(mapper::toResponse);
    }

    @Transactional
    public List<PermissionResponse> addBulk(List<PermissionCreateRequest> requests) {
        List<Permission> entities = requests.stream().map(req -> {
            if (repository.existsByCode(req.code())) {
                throw new FieldValidationException("code", "already.exists");
            }
            return mapper.toEntity(req);
        }).toList();
        return repository.saveAll(entities).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public boolean delete(UUID id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }

    @Transactional
    public void deleteBulk(List<UUID> ids) {
        repository.deleteAllByIdInBatch(ids);
    }
}
