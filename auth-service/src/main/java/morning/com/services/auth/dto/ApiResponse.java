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

    /**
     * Convenience success response with default {@link HttpStatus#OK} and custom message/data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message, T data) {
        return success(HttpStatus.OK, message, data);
    }

    /**
     * Convenience success response with default message key and provided data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return success(HttpStatus.OK, MessageKeys.SUCCESS, data);
    }

    /**
     * Convenience success response with default {@link HttpStatus#OK} and no data.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success(String message) {
        return success(HttpStatus.OK, message, null);
    }

    /**
     * Convenience success response with default status and message.
     */
    public static <T> ResponseEntity<ApiResponse<T>> success() {
        return success(HttpStatus.OK, MessageKeys.SUCCESS, null);
    }

    /**
     * Convenience error response with default {@link HttpStatus#BAD_REQUEST} and message.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error() {
        return error(HttpStatus.BAD_REQUEST, MessageKeys.VALIDATION_ERROR, null);
    }

    /**
     * Convenience error response with default {@link HttpStatus#BAD_REQUEST} and custom message.
     */
    public static <T> ResponseEntity<ApiResponse<T>> error(String message) {
        return error(HttpStatus.BAD_REQUEST, message, null);
    }
}
