package hr.algebra.surfspot.exception;

public class NavigationException extends SurfSpotException {
    public NavigationException() {
        super("Doslo je do pogreske prilikom navigacije.");
    }
    public NavigationException(String message) {
        super(message);
    }
    public NavigationException(String message, Throwable cause) {
        super(message, cause);
    }
}
