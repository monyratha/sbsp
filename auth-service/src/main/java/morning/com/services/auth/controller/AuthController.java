package morning.com.services.auth.controller;

import jakarta.validation.Valid;
import morning.com.services.auth.dto.*;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRequest request) {
        try {
            userService.register(request.username(), request.password());
            return ApiResponse.success(MessageKeys.USER_REGISTERED);
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(HttpStatus.CONFLICT, MessageKeys.USERNAME_EXISTS);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        if (userService.authenticate(request.username(), request.password())) {
            String token = jwtService.generateToken(request.username());
            return ApiResponse.success(MessageKeys.SUCCESS, new AuthResponse(token));
        }
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
    }
}