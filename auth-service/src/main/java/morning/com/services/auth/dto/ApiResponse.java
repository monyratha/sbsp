package morning.com.services.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response wrapper for API results, including code, message, and optional data.
 * Provides factory methods for various success and error responses.
 *
 * @author Lucas
 * @param <T> the type of the response data
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // Constructor for responses without data
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    // Factory method for success with data, using default success message
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), data);
    }

    // Factory method for success with enum message and data
    public static <T> ApiResponse<T> ok(ResultEnum messageEnum, T data) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessage(), data);
    }

    // Factory method for success with custom message and data
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), message, data);
    }

    // Factory method for success without data and with default success message
    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMessage(), null);
    }

    // Factory method for success without data and with a custom message
    public static <T> ApiResponse<T> ok(String message) {
        return new ApiResponse<>(ResultEnum.SUCCESS.getCode(), message, null);
    }

    // Factory method for error with enum message
    public static <T> ApiResponse<T> error(ResultEnum messageEnum) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessage());
    }

    // Factory method for error with custom message
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(ResultEnum.ERROR.getCode(), message);
    }

    // Factory method for error with enum message and data
    public static <T> ApiResponse<T> error(ResultEnum messageEnum, T data) {
        return new ApiResponse<>(messageEnum.getCode(), messageEnum.getMessage(), data);
    }

    // Factory method for error with custom message and data
    public static <T> ApiResponse<T> error(String message, T data) {
        return new ApiResponse<>(ResultEnum.ERROR.getCode(), message, data);
    }
}
