package morning.com.services.auth.model;

/**
 * Standard result codes and messages for API responses.
 */
public enum ResultEnum {
    SUCCESS(200, "Success"),
    ERROR(500, "Error"),
    USERNAME_EXISTS(409, "Username already exists"),
    INVALID_CREDENTIALS(401, "Invalid credentials");

    private final int code;
    private final String message;

    ResultEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
