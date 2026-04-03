package hr.algebra.surfspot.exception;

public class EntityNotFoundException extends DataAccessException {
    public EntityNotFoundException() {
        super("Entitet nije pronaden.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
