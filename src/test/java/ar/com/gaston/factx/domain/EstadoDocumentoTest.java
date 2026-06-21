package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstadoDocumentoTest {

    @Test
    void readsDatabaseValueCaseInsensitively() {
        assertEquals(EstadoDocumento.PENDIENTE, EstadoDocumento.fromDatabaseValue("pendiente"));
        assertEquals(EstadoDocumento.ANULADO, EstadoDocumento.fromDatabaseValue("  ANULADO  "));
    }

    @Test
    void rejectsMissingDatabaseValue() {
        assertThrows(IllegalArgumentException.class, () -> EstadoDocumento.fromDatabaseValue(null));
    }
}
