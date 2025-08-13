package morning.com.services.user.mapper;

import morning.com.services.user.dto.PermissionCreateRequest;
import morning.com.services.user.dto.PermissionUpdateRequest;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.entity.Permission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between Permission entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface PermissionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Permission toEntity(PermissionCreateRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void update(@MappingTarget Permission permission, PermissionUpdateRequest request);

    PermissionResponse toResponse(Permission permission);
}
