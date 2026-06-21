package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TipoDocumentoTest {

    @Test
    void readsDatabaseValueCaseInsensitively() {
        assertEquals(TipoDocumento.FACTURA, TipoDocumento.fromDatabaseValue("factura"));
        assertEquals(TipoDocumento.NOTA_CREDITO, TipoDocumento.fromDatabaseValue("  nota_credito  "));
    }

    @Test
    void rejectsMissingDatabaseValue() {
        assertThrows(IllegalArgumentException.class, () -> TipoDocumento.fromDatabaseValue(" "));
    }
}
