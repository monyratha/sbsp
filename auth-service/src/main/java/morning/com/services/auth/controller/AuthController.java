package morning.com.services.auth.controller;

import morning.com.services.auth.model.ApiResponse;
import morning.com.services.auth.model.AuthRequest;
import morning.com.services.auth.model.AuthResponse;
import morning.com.services.auth.model.ResultEnum;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@RequestBody AuthRequest request) {
        try {
            userService.register(request.username(), request.password());
            return ResponseEntity.ok(ApiResponse.ok("User registered successfully"));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(ResultEnum.USERNAME_EXISTS));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        if (userService.authenticate(request.username(), request.password())) {
            String token = jwtService.generateToken(request.username());
            return ResponseEntity.ok(ApiResponse.ok(new AuthResponse(token)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ResultEnum.INVALID_CREDENTIALS));
    }
}
