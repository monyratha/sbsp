package morning.com.services.user.controller;

import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.dto.PermissionCreateRequest;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.dto.PermissionUpdateRequest;
import morning.com.services.user.service.PermissionService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/user/permission")
public class PermissionController {
    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Create new permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> create(
            @Validated @RequestBody PermissionCreateRequest request) {
        PermissionResponse saved = service.add(request);
        return ApiResponse.created(
                MessageKeys.PERMISSION_CREATED,
                saved,
                "/permission/" + saved.id()
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update existing permission")
    public ResponseEntity<ApiResponse<PermissionResponse>> update(
            @PathVariable UUID id,
            @Validated @RequestBody PermissionUpdateRequest request) {
        return service.update(id, request)
                .map(resp -> ApiResponse.success(MessageKeys.SUCCESS, resp))
                .orElseGet(() -> ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.PERMISSION_NOT_FOUND));
    }
}
