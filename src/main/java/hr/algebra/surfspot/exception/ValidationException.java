package hr.algebra.surfspot.exception;

public class ValidationException extends SurfSpotException {
    public ValidationException() {
        super("Validacija neuspjesna");
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message,  Throwable cause) {
        super(message, cause);
    }
}
