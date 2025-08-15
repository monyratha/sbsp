package morning.com.services.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Standard API wrapper: { code: 0|-1, messageKey, data }
 * Includes minimal static helpers for common HTTP responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int code, String messageKey, T data) {

    public static final int SUCCESS = 0;
    public static final int ERROR = -1;

    // ---- Factory helpers producing ResponseEntity<ApiResponse<T>> ----
    public static <T> ResponseEntity<ApiResponse<T>> success(String messageKey, T data) {
        return ResponseEntity.ok(new ApiResponse<>(SUCCESS, messageKey, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> success(String messageKey) {
        return ResponseEntity.ok(new ApiResponse<>(SUCCESS, messageKey, null));
    }
    public static <T> ResponseEntity<ApiResponse<T>> created(String messageKey, T data, String location) {
        return ResponseEntity.created(java.net.URI.create(location))
                .body(new ApiResponse<>(SUCCESS, messageKey, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey) {
        return ResponseEntity.status(status).body(new ApiResponse<>(ERROR, messageKey, null));
    }
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(ERROR, messageKey, data));
    }
}