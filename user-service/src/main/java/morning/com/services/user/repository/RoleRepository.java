package morning.com.services.user.repository;

import morning.com.services.user.dto.RoleDTO;
import morning.com.services.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<RoleDTO> findAllByOrderByName();

    boolean existsByName(String name);

    boolean existsByCode(String code);
}
