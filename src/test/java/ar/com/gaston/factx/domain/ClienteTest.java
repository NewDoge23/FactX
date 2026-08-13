package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteTest {
    @Test
    void normalizesOptionalCustomerFields() {
        Cliente cliente = Cliente.create("  Cliente Demo ", "  Demo SA ", " 00-00000000-0 ", "  Nota ");
        assertEquals("Cliente Demo", cliente.nombre());
        assertEquals("Demo SA", cliente.razonSocial());
        assertEquals("00-00000000-0", cliente.cuit());
        assertEquals("Nota", cliente.notas());
    }

    @Test
    void rejectsBlankCustomerName() {
        assertThrows(IllegalArgumentException.class, () -> Cliente.create(" ", null, null, null));
    }
}
