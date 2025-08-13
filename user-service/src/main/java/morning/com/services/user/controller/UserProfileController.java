package morning.com.services.user.controller;

import morning.com.services.user.entity.UserProfile;
import morning.com.services.user.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserProfileController {
    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping
    public UserProfile create(@RequestBody UserProfile profile) {
        return service.add(profile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfile> get(@PathVariable UUID id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
