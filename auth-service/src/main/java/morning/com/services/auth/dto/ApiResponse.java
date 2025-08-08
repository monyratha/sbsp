package morning.com.services.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Standard API wrapper: { status: success|error, messageKey, data }
 * Includes minimal static helpers for common HTTP responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(Status status, String messageKey, T data) {

    public enum Status {
        SUCCESS("success"), ERROR("error");
        private final String json;
        Status(String json) { this.json = json; }
        @JsonValue public String json() { return json; }
    }

    // ---- Factory helpers producing ResponseEntity<ApiResponse<T>> ----
    public static <T> ResponseEntity<ApiResponse<T>> success(String messageKey, T data) {
        return ResponseEntity.ok(new ApiResponse<>(Status.SUCCESS, messageKey, data));
    }
    public static <T> ResponseEntity<ApiResponse<T>> success(String messageKey) {
        return ResponseEntity.ok(new ApiResponse<>(Status.SUCCESS, messageKey, null));
    }
    public static <T> ResponseEntity<ApiResponse<T>> created(String messageKey, T data, String location) {
        return ResponseEntity.created(java.net.URI.create(location))
                .body(new ApiResponse<>(Status.SUCCESS, messageKey, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey) {
        return ResponseEntity.status(status).body(new ApiResponse<>(Status.ERROR, messageKey, null));
    }
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>(Status.ERROR, messageKey, data));
    }
}