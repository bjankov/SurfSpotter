package hr.algebra.surfspot.exception;

public class DataAccessException extends SurfSpotException {
    public DataAccessException() {
        super("Doslo je do greske prilikom pristupa podacima.");
    }

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
