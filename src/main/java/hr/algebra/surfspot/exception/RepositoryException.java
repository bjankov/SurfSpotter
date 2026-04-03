package hr.algebra.surfspot.exception;

public class RepositoryException extends DataAccessException {
    public RepositoryException() {
        super("Greska u povezivanju ili interakciji sa bazom.");
    }

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
