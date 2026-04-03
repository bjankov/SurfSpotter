package hr.algebra.surfspot.exception;

public class AuthorizationException extends SurfSpotException {
    public AuthorizationException() {
        super("Greska prilikom autorizacije.");
    }

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
