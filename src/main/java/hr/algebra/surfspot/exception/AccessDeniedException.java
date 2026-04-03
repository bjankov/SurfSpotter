package hr.algebra.surfspot.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException() {
        super("Zabranjen pristup.");
    }

    public AccessDeniedException(String message) {
        super(message);
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}
