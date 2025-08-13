package morning.com.services.user.repository;

import morning.com.services.user.dto.RoleDTO;
import morning.com.services.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    @Query("select new morning.com.services.user.dto.RoleDTO(r.id, r.name) from Role r order by r.name")
    List<RoleDTO> findAllProjectedBy();
}
