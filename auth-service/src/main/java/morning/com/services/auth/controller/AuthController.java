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

import jakarta.servlet.http.HttpServletRequest;

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
            var issued = refreshTokenService.issue(userId, http.getRemoteAddr(), http.getHeader(HttpHeaders.USER_AGENT));
            return ApiResponse.success(MessageKeys.SUCCESS,
                    new AuthResponse(token, exp, issued.rawToken()));
        }
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
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

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest request) {
        try {
            var rotation = refreshTokenService.verifyAndRotate(request.refreshToken());
            String username = userService.findUsernameById(rotation.userId());
            String token = jwtService.generateToken(username);
            long exp = System.currentTimeMillis() + jwtService.ttlMillis();
            return ApiResponse.success(MessageKeys.SUCCESS,
                    new AuthResponse(token, exp, rotation.issued().rawToken()));
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshRequest request) {
        try {
            refreshTokenService.revoke(request.refreshToken());
            return ApiResponse.success(MessageKeys.SUCCESS);
        } catch (IllegalArgumentException ex) {
            return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @Valid @RequestBody PasswordChangeRequest request) {
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
        boolean changed = userService.changePassword(username, request.currentPassword(), request.newPassword());
        if (changed) {
            return ApiResponse.success(MessageKeys.PASSWORD_CHANGED);
        }
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, MessageKeys.INVALID_CREDENTIALS);
    }
}
