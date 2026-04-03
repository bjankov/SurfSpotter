package hr.algebra.surfspot.exception;

public class SurfSpotException extends RuntimeException {
    public SurfSpotException() {
        super("Doslo je do nepoznate pogreske u radu aplikacije");
    }

    public SurfSpotException(String message) {
        super(message);
    }

    public SurfSpotException(String message, Throwable cause) {
        super(message, cause);
    }
}
