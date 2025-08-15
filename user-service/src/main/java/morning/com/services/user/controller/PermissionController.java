package morning.com.services.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.dto.PermissionCreateRequest;
import morning.com.services.user.dto.PermissionResponse;
import morning.com.services.user.dto.PermissionUpdateRequest;
import morning.com.services.user.dto.PageResponse;
import morning.com.services.user.service.PermissionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user/permission")
public class PermissionController {
    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "List permissions")
    public ResponseEntity<ApiResponse<PageResponse<PermissionResponse>>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String section,
            @RequestParam(required = false) String code) {
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Page<PermissionResponse> result = service.search(search, section, code,
                PageRequest.of(Math.max(page - 1, 0), size, sort));
        return ApiResponse.success(MessageKeys.SUCCESS, PageResponse.from(result));
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

    @PostMapping("/bulk")
    @Operation(summary = "Create permissions in bulk")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> bulkCreate(
            @RequestBody List<@Valid PermissionCreateRequest> requests) {
        return ApiResponse.success(MessageKeys.SUCCESS, service.addBulk(requests));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return service.delete(id)
                ? ApiResponse.success(MessageKeys.SUCCESS)
                : ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.PERMISSION_NOT_FOUND);
    }

    @DeleteMapping("/bulk")
    @Operation(summary = "Delete permissions in bulk")
    public ResponseEntity<ApiResponse<Void>> bulkDelete(@RequestBody List<UUID> ids) {
        service.deleteBulk(ids);
        return ApiResponse.success(MessageKeys.SUCCESS);
    }
}
