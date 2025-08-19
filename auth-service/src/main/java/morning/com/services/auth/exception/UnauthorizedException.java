package morning.com.services.auth.exception;

/**
 * Exception representing an unauthenticated request that should result
 * in a {@code 401 UNAUTHORIZED} response.
 */
public class UnauthorizedException extends RuntimeException {
}
