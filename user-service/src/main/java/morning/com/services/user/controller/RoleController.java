package morning.com.services.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import morning.com.services.user.dto.*;
import morning.com.services.user.service.RoleService;
import org.springframework.http.HttpStatus;
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
    @Operation(summary = "Create new role")
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Validated @RequestBody RoleCreateRequest request) {
        RoleResponse saved = service.add(request);
        return ApiResponse.created(
                MessageKeys.ROLE_CREATED,
                saved,
                "/role/" + saved.id()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing role")
    public ResponseEntity<ApiResponse<RoleResponse>> update(@PathVariable UUID id,
                                                            @Validated @RequestBody RoleUpdateRequest request) {
        return service.update(id, request)
                .map(resp -> ApiResponse.success(MessageKeys.SUCCESS, resp))
                .orElseGet(() -> ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.ROLE_NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return service.delete(id)
                ? ApiResponse.success(MessageKeys.SUCCESS)
                : ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.ROLE_NOT_FOUND);
    }

    @GetMapping("/acl-matrix")
    @Operation(summary = "Get ACL matrix")
    public ResponseEntity<ApiResponse<MatrixResponse>> getMatrix() {
        return ApiResponse.success(MessageKeys.SUCCESS, service.getMatrix());
    }

    @PatchMapping("/{roleId}/permissions/{permId}")
    @Operation(summary = "Grant or revoke role permission")
    public ResponseEntity<ApiResponse<Void>> toggle(@PathVariable UUID roleId,
                                                    @PathVariable UUID permId,
                                                    @RequestBody RolePermissionGrantRequest request) {
        service.setGrant(roleId, permId, request.granted());
        return ApiResponse.success(MessageKeys.SUCCESS);
    }

    @PostMapping("/acl-matrix")
    @Operation(summary = "Apply bulk permission operations")
    public ResponseEntity<ApiResponse<Void>> applyBulk(@RequestBody RolePermissionBulkGrantRequest request) {
        service.applyBulk(request.changes());
        return ApiResponse.success(MessageKeys.SUCCESS);
    }
}
