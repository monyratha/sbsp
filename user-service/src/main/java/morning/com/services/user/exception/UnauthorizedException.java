package morning.com.services.user.exception;

/**
 * Exception indicating that the request lacks valid authentication.
 * It should be translated to a {@code 401 UNAUTHORIZED} response.
 */
public class UnauthorizedException extends RuntimeException {
}
