package morning.com.services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response wrapper for API results, including code, message key, and optional data.
 * Provides factory methods for various success and error responses.
 *
 * @author Lucas
 * @param <T> the type of the response data
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String messageKey;
    private T data;

    // Constructor for responses without data
    public ApiResponse(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
        this.data = null;
    }

    // Factory method for success with data, using default success message key
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessageKey(), data);
    }

    // Factory method for success with enum message key and data
    public static <T> ApiResponse<T> ok(ResultEnum messageEnum, T data) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessageKey(), data);
    }

    // Factory method for success with custom message key and data
    public static <T> ApiResponse<T> ok(String messageKey, T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), messageKey, data);
    }

    // Factory method for success without data using enum message key
    public static <T> ApiResponse<T> ok(ResultEnum messageEnum) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessageKey(), null);
    }

    // Factory method for success without data and with default success message key
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessageKey(), null);
    }

    // Factory method for success without data and with a custom message key
    public static <T> ApiResponse<T> ok(String messageKey) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), messageKey, null);
    }

    // Factory method for error with enum message key
    public static <T> ApiResponse<T> error(ResultEnum messageEnum) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessageKey());
    }

    // Factory method for error with custom message key
    public static <T> ApiResponse<T> error(String messageKey) {
        return new ApiResponse<>(ResultEnum.ERROR.getCode(), messageKey);
    }

    // Factory method for error with enum message key and data
    public static <T> ApiResponse<T> error(ResultEnum messageEnum, T data) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessageKey(), data);
    }

    // Factory method for error with custom message key and data
    public static <T> ApiResponse<T> error(String messageKey, T data) {
        return new ApiResponse<>(ResultEnum.ERROR.getCode(), messageKey, data);
    }
}
