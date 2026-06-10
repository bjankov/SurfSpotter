package hr.algebra.surfspot.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException() {
        super("Resurs nije pronaden");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
