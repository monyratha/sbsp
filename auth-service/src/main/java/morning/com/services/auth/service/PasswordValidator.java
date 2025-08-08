package morning.com.services.auth.service;

import java.util.regex.Pattern;

/**
 * Utility class for enforcing password policy.
 */
public final class PasswordValidator {
    // At least 8 chars, one upper, one lower, one digit, one special char
    private static final Pattern POLICY = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*]).{8,}$");

    private PasswordValidator() {
    }

    public static boolean isValid(String password) {
        return password != null && POLICY.matcher(password).matches();
    }
}
