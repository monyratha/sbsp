package morning.com.services.user.controller;

import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.entity.Role;
import morning.com.services.user.service.PermissionService;
import morning.com.services.user.service.RoleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/role")
public class RoleController {
    private final RoleService service;
    private final PermissionService permissionService;

    public RoleController(RoleService service, PermissionService permissionService) {
        this.service = service;
        this.permissionService = permissionService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role role) {
        Role saved = service.add(role);
        return ApiResponse.created(
                MessageKeys.ROLE_CREATED,
                saved,
                "/role/" + saved.getRoleId()
        );
    }

    @PostMapping("/{roleId}/permissions/{permissionId}")
    public ResponseEntity<ApiResponse<Role>> addPermission(@PathVariable UUID roleId, @PathVariable UUID permissionId) {
        if (service.findById(roleId).isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.ROLE_NOT_FOUND);
        }
        if (permissionService.findById(permissionId).isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.PERMISSION_NOT_FOUND);
        }
        Role updated = service.addPermission(roleId, permissionId);
        return ApiResponse.success(MessageKeys.SUCCESS, updated);
    }
}
