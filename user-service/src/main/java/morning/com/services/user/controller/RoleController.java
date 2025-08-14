package morning.com.services.user.controller;

import morning.com.services.user.dto.*;
import morning.com.services.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user/role")
public class RoleController {
    private final RoleService service;

    public RoleController(RoleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Validated @RequestBody RoleCreateRequest request) {
        RoleResponse saved = service.add(request);
        return ApiResponse.created(
                MessageKeys.ROLE_CREATED,
                saved,
                "/role/" + saved.id()
        );
    }

    @GetMapping("/acl-matrix")
    public ResponseEntity<ApiResponse<MatrixResponse>> getMatrix() {
        return ApiResponse.success(MessageKeys.SUCCESS, service.getMatrix());
    }

    @PatchMapping("/{roleId}/permissions/{permId}")
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
