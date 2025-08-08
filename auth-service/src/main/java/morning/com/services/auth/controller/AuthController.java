package morning.com.services.auth.controller;

import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import morning.com.services.auth.dto.*;
import morning.com.services.auth.service.JwtService;
import morning.com.services.auth.service.RefreshTokenService;
import morning.com.services.auth.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            userService.register(request.username(), request.email(), request.password());
            return ApiResponse.success(MessageKeys.USER_REGISTERED);
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(HttpStatus.CONFLICT, MessageKeys.USERNAME_EXISTS);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        if (userService.authenticate(request.username(), request.password())) {
            String token = jwtService.generateToken(request.username());
            long exp = jwtService.getExpiration(token).getTime();
            var user = userService.findByUsername(request.username()).orElseThrow();
            var refreshToken = refreshTokenService.create(user);
            return ApiResponse.success(MessageKeys.SUCCESS,
                    new AuthResponse(token, exp, refreshToken.getToken(), refreshToken.getExpiresAt().toEpochMilli()));
        }
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        return refreshTokenService.refresh(request.refreshToken())
                .map(rt -> {
                    String token = jwtService.generateToken(rt.getUser().getUsername());
                    long exp = jwtService.getExpiration(token).getTime();
                    return ApiResponse.success(MessageKeys.SUCCESS,
                            new AuthResponse(token, exp, rt.getToken(), rt.getExpiresAt().toEpochMilli()));
                })
                .orElseGet(() -> ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> me(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        if (authorization == null) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
        String auth = authorization.trim();
        if (!auth.regionMatches(true, 0, "Bearer ", 0, 7)) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
        String token = auth.substring(7).trim();

        String username;
        try {
            username = jwtService.getUsername(token);
        } catch (JwtException e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }

        return userService.findByUsername(username)
                .map(u -> ApiResponse.success(MessageKeys.SUCCESS, new UserInfo(u.getId(), u.getUsername())))
                .orElseGet(() -> ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS));
    }
}
