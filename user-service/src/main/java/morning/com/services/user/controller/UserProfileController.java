package morning.com.services.user.controller;

import morning.com.services.user.dto.ApiResponse;
import morning.com.services.user.dto.MessageKeys;
import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.service.RoleService;
import morning.com.services.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserProfileController {
    private final UserProfileService service;
    private final RoleService roleService;

    public UserProfileController(UserProfileService service, RoleService roleService) {
        this.service = service;
        this.roleService = roleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserProfile>> create(@RequestBody UserProfile profile) {
        UserProfile saved = service.add(profile);
        return ApiResponse.created(
                MessageKeys.PROFILE_CREATED,
                saved,
                "/user/" + saved.getUserId()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserProfile>> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(profile -> ApiResponse.success(MessageKeys.SUCCESS, profile))
                .orElseGet(() -> ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.PROFILE_NOT_FOUND));
    }

    @PostMapping("/{id}/roles/{roleId}")
    public ResponseEntity<ApiResponse<UserProfile>> addRole(@PathVariable UUID id, @PathVariable UUID roleId) {
        if (service.findById(id).isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.PROFILE_NOT_FOUND);
        }
        if (roleService.findById(roleId).isEmpty()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, MessageKeys.ROLE_NOT_FOUND);
        }
        UserProfile updated = roleService.addRoleToUser(id, roleId);
        return ApiResponse.success(MessageKeys.SUCCESS, updated);
    }
}
