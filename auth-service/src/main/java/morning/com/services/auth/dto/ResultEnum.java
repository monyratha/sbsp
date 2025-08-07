package morning.com.services.auth.dto;

/**
 * Standard result codes and message keys for API responses.
 */
public enum ResultEnum {
    SUCCESS(200, "auth.success"),
    ERROR(500, "auth.error"),
    USER_REGISTERED(200, "auth.register.success"),
    USERNAME_EXISTS(409, "auth.username.exists"),
    INVALID_CREDENTIALS(401, "auth.invalid.credentials"),
    VALIDATION_ERROR(400, "validation.error");

    private final int code;
    private final String messageKey;

    ResultEnum(int code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }

    public int getCode() {
        return code;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
