package morning.com.services.user.controller;

import morning.com.services.user.dto.*;
import morning.com.services.user.service.AclService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class AclController {
    private final AclService service;

    public AclController(AclService service) {
        this.service = service;
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
