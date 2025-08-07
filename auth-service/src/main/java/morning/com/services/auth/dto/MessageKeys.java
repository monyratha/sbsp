package morning.com.services.auth.dto;

/**
 * Standard message keys for API responses.
 */
public final class MessageKeys {

    private MessageKeys() {
        // Utility class
    }

    public static final String SUCCESS = "auth.success";
    public static final String USER_REGISTERED = "auth.register.success";
    public static final String USERNAME_EXISTS = "auth.username.exists";
    public static final String INVALID_CREDENTIALS = "auth.invalid.credentials";
    public static final String VALIDATION_ERROR = "validation.error";
}

