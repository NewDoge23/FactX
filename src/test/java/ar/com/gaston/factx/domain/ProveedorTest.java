package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProveedorTest {

    @Test
    void trimsRequiredAndOptionalText() {
        Proveedor proveedor = Proveedor.create("  Proveedor Demo  ", "  20-12345678-9  ", "  Nota  ");

        assertNull(proveedor.id());
        assertEquals("Proveedor Demo", proveedor.nombre());
        assertEquals("20-12345678-9", proveedor.cuit());
        assertEquals("Nota", proveedor.notas());
    }

    @Test
    void blankOptionalTextBecomesNull() {
        Proveedor proveedor = Proveedor.create("Proveedor Demo", " ", "");

        assertNull(proveedor.cuit());
        assertNull(proveedor.notas());
    }

    @Test
    void rejectsBlankName() {
        assertThrows(IllegalArgumentException.class, () -> Proveedor.create(" ", null, null));
    }
}
