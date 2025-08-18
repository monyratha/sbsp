package morning.com.services.user.exception;

/**
 * Exception indicating that an authenticated user lacks required
 * permissions. It should be translated to a {@code 403 FORBIDDEN}
 * response.
 */
public class ForbiddenException extends RuntimeException {
}
