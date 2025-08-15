package morning.com.services.user.repository;

import morning.com.services.user.dto.PermissionDTO;
import morning.com.services.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID>, JpaSpecificationExecutor<Permission> {

    List<PermissionDTO> findAllByOrderBySectionAscLabelAsc();

    boolean existsByCode(String code);
}
