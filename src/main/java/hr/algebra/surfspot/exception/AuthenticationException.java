package hr.algebra.surfspot.exception;

public class AuthenticationException extends AuthorizationException {
    public AuthenticationException() {
        super("Krivi podaci - nije pronaden niti jedan korisnik sa ovim podacima.");
    }

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
