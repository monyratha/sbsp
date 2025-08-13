package morning.com.services.user.repository;

import morning.com.services.user.dto.PermissionDTO;
import morning.com.services.user.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    @Query("select new morning.com.services.user.dto.PermissionDTO(p.id, p.code, p.section, p.label) from Permission p order by p.section asc, p.label asc")
    List<PermissionDTO> findAllProjectedByOrderBySectionAscLabelAsc();
}
