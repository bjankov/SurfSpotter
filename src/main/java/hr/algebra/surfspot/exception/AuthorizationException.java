package hr.algebra.surfspot.exception;

public class AuthorizationException extends SurfSpotException {

    public AuthorizationException() {
        super("Nedovoljna dopustenja za ovu radnju");
    }

    public AuthorizationException(String message) {
        super(message);
    }
}