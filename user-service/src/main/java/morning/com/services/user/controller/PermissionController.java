package morning.com.services.user.controller;

import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.entity.Permission;
import morning.com.services.user.service.PermissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permission")
public class PermissionController {
    private final PermissionService service;

    public PermissionController(PermissionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Permission>> create(@RequestBody Permission permission) {
        Permission saved = service.add(permission);
        return ApiResponse.created(
                MessageKeys.PERMISSION_CREATED,
                saved,
                "/permission/" + saved.getPermissionId()
        );
    }
}
