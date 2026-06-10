package hr.algebra.surfspot.exception;

public class PersistenceException extends RuntimeException {

    public PersistenceException(final String message) {
        super(message);
    }

    public PersistenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
