package morning.com.services.user.controller;

import morning.com.services.user.dto.*;
import morning.com.services.user.entity.Role;
import morning.com.services.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping("/role")
    public ResponseEntity<ApiResponse<Role>> create(@RequestBody Role role) {
        Role saved = service.add(role);
        return ApiResponse.created(
                MessageKeys.ROLE_CREATED,
                saved,
                "/role/" + saved.getId()
        );
    }

    @GetMapping("/acl-matrix")
    public ResponseEntity<ApiResponse<MatrixResponse>> getMatrix() {
        return ApiResponse.success(MessageKeys.SUCCESS, service.getMatrix());
    }

    @PatchMapping("/roles/{roleId}/permissions/{permId}")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID roleId,
                                                    @PathVariable UUID permId,
                                                    @RequestBody GrantRequest request) {
        service.setGrant(roleId, permId, request.granted());
        return ApiResponse.success(MessageKeys.SUCCESS);
    }

    @PostMapping("/acl-matrix")
    public ResponseEntity<ApiResponse<Void>> applyBulk(@RequestBody BulkRequest request) {
        service.applyBulk(request.operations());
        return ApiResponse.success(MessageKeys.SUCCESS);
    }
}
