package hr.algebra.surfspot.exception;

public class DuplicateRecordException extends DataAccessException {
    public DuplicateRecordException() {
        super("Uneseni podatak vec postoji u bazi podataka.");
    }

    public DuplicateRecordException(String message) {
        super(message);
    }

    public DuplicateRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
