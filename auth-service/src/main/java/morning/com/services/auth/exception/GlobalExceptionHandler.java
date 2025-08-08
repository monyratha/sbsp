package morning.com.services.auth.exception;

import morning.com.services.auth.dto.ApiResponse;
import morning.com.services.auth.dto.MessageKeys;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex) {
        return ApiResponse.error(HttpStatus.BAD_REQUEST, MessageKeys.VALIDATION_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {
        // Pass-through messageKey if you throw keys as messages, or map here deliberately
        return ApiResponse.error(HttpStatus.BAD_REQUEST, MessageKeys.VALIDATION_ERROR);
    }
}