package ar.com.gaston.factx.validation;

public final class ValidationException extends IllegalArgumentException {
    public ValidationException(String message) {
        super(message);
    }
}
