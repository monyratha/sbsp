package morning.com.services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response wrapper for API results, including status, message key, and optional data.
 * Provides factory methods for various success and error responses.
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

    // Factory method for success with data, using default success message key
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", ResultEnum.SUCCESS.getMessageKey(), data);
    }

    // Factory method for success with enum message key and data
    public static <T> ApiResponse<T> success(ResultEnum messageEnum, T data) {
        return new ApiResponse<>("success", messageEnum.getMessageKey(), data);
    }

    // Factory method for success with custom message key and data
    public static <T> ApiResponse<T> success(String messageKey, T data) {
        return new ApiResponse<>("success", messageKey, data);
    }

    // Factory method for success without data using enum message key
    public static <T> ApiResponse<T> success(ResultEnum messageEnum) {
        return new ApiResponse<>("success", messageEnum.getMessageKey(), null);
    }

    // Factory method for success without data and with default success message key
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("success", ResultEnum.SUCCESS.getMessageKey(), null);
    }

    // Factory method for success without data and with a custom message key
    public static <T> ApiResponse<T> success(String messageKey) {
        return new ApiResponse<>("success", messageKey, null);
    }

    // Factory method for error with enum message key
    public static <T> ApiResponse<T> error(ResultEnum messageEnum) {
        return new ApiResponse<>("error", messageEnum.getMessageKey());
    }

    // Factory method for error with custom message key
    public static <T> ApiResponse<T> error(String messageKey) {
        return new ApiResponse<>("error", messageKey);
    }

    // Factory method for error with enum message key and data
    public static <T> ApiResponse<T> error(ResultEnum messageEnum, T data) {
        return new ApiResponse<>("error", messageEnum.getMessageKey(), data);
    }

    // Factory method for error with custom message key and data
    public static <T> ApiResponse<T> error(String messageKey, T data) {
        return new ApiResponse<>("error", messageKey, data);
    }
}
