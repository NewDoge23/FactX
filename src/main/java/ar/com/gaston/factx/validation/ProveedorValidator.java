package ar.com.gaston.factx.validation;

public final class ProveedorValidator {
    private ProveedorValidator() {
    }

    public static void validateForCreate(String nombre) {
        validateNombre(nombre);
    }

    public static void validateForUpdate(Long id, String nombre) {
        validateId(id);
        validateNombre(nombre);
    }

    public static void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Supplier id must be positive.");
        }
    }

    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new ValidationException("Supplier name is required.");
        }
    }
}
