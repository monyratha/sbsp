package morning.com.services.user.mapper;

import morning.com.services.user.dto.RoleCreateRequest;
import morning.com.services.user.dto.RoleResponse;
import morning.com.services.user.dto.RoleUpdateRequest;
import morning.com.services.user.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between Role entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(RoleCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(@MappingTarget Role role, RoleUpdateRequest request);

    RoleResponse toResponse(Role role);
}

