package morning.com.services.auth.exception;

/**
 * Exception representing an authenticated user lacking sufficient
 * privileges which should result in a {@code 403 FORBIDDEN} response.
 */
public class ForbiddenException extends RuntimeException {
}
