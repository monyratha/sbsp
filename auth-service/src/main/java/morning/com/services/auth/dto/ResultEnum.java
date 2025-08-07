package morning.com.services.auth.dto;

/**
 * Standard message keys for API responses.
 */
public enum ResultEnum {
    SUCCESS("auth.success"),
    USER_REGISTERED("auth.register.success"),
    USERNAME_EXISTS("auth.username.exists"),
    INVALID_CREDENTIALS("auth.invalid.credentials"),
    VALIDATION_ERROR("validation.error");

    private final String messageKey;

    ResultEnum(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
