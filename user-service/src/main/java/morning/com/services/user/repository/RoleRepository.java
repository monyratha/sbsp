package morning.com.services.user.repository;

import morning.com.services.user.dto.RoleDTO;
import morning.com.services.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID>, JpaSpecificationExecutor<Role> {

    List<RoleDTO> findAllByOrderByName();

    boolean existsByName(String name);

    boolean existsByCode(String code);
}
