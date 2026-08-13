package ar.com.gaston.factx.validation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProveedorValidatorTest {

    @Test
    void acceptsANameForCreation() {
        assertDoesNotThrow(() -> ProveedorValidator.validateForCreate("Proveedor Demo"));
    }

    @Test
    void rejectsBlankNamesAndNonPositiveIds() {
        assertThrows(ValidationException.class, () -> ProveedorValidator.validateForCreate(" "));
        assertThrows(ValidationException.class, () -> ProveedorValidator.validateForUpdate(0L, "Proveedor Demo"));
    }
}
