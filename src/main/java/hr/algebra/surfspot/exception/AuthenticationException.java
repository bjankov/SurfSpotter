package hr.algebra.surfspot.exception;

public class AuthenticationException extends SurfSpotException {

    public AuthenticationException() {
        super("Neuspjesna autentifikacija");
    }

    public AuthenticationException(String message) {
        super(message);
    }
}
