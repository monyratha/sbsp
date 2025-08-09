package morning.com.services.auth.controller;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
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
    private static final String BEARER_PREFIX = "Bearer ";

    public AuthController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request,
                                                          HttpServletRequest http) {
        if (userService.authenticate(request.username(), request.password())) {
            String username = request.username().toLowerCase();
            String token = jwtService.generateToken(username);
            long exp = System.currentTimeMillis() + jwtService.ttlMillis();
            String userId = userService.findUserIdByUsername(username);
            RefreshTokenService.Issued issued =
                    refreshTokenService.issue(userId, http.getRemoteAddr(), http.getHeader(HttpHeaders.USER_AGENT));
            return ApiResponse.success(
                    MessageKeys.SUCCESS,
                    new AuthResponse(token, exp, issued.rawToken()));
        }
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserInfo>> me(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        String token = extractToken(authorization);
        if (token == null) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
        String username;
        try {
            username = jwtService.getUsername(token);
        } catch (JwtException e) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
        return userService.findByUsername(username)
                .map(u -> ApiResponse.success(
                        MessageKeys.SUCCESS, new UserInfo(u.getId(), u.getUsername())))
                .orElseGet(() -> ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS));
    }

    private String extractToken(String authorization) {
        if (authorization == null) {
            return null;
        }
        String auth = authorization.trim();
        if (!auth.regionMatches(true, 0, BEARER_PREFIX, 0, BEARER_PREFIX.length())) {
            return null;
        }
        String token = auth.substring(BEARER_PREFIX.length()).trim();
        return token.isEmpty() ? null : token;
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            RefreshTokenService.Rotation rotation =
                    refreshTokenService.verifyAndRotate(request.refreshToken());
            String username = userService.findUsernameById(rotation.userId());
            String token = jwtService.generateToken(username);
            long exp = System.currentTimeMillis() + jwtService.ttlMillis();
            return ApiResponse.success(
                    MessageKeys.SUCCESS,
                    new AuthResponse(token, exp, rotation.issued().rawToken()));
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
    }
}
