package morning.com.services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * Response wrapper for API results, including status, message key, and optional data.
 * Provides factory methods to build {@link ResponseEntity} objects for success and error cases.
 *
 * @author Lucas
 * @param <T> the type of the response data
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private String status;
    private String messageKey;
    private T data;

    // Constructor for responses without data
    public ApiResponse(String status, String messageKey) {
        this.status = status;
        this.messageKey = messageKey;
        this.data = null;
    }

    /**
     * Create a success response entity with the given HTTP status, message key and optional data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String messageKey, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>("success", messageKey, data));
    }

    /**
     * Create a success response entity with the given HTTP status and message key without data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(HttpStatus status, String messageKey) {
        return success(status, messageKey, null);
    }

    /**
     * Create an error response entity with the given HTTP status, message key and optional data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey, T data) {
        return ResponseEntity.status(status).body(new ApiResponse<>("error", messageKey, data));
    }

    /**
     * Create an error response entity with the given HTTP status and message key without data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(HttpStatus status, String messageKey) {
        return error(status, messageKey, null);
    }
}
