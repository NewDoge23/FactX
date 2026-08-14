package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EstadosDocumentoCommercialTest {
    @Test
    void readsReceivedAndIssuedStatesIndependently() {
        assertEquals(EstadoDocumentoRecibido.PAGADO, EstadoDocumentoRecibido.fromDatabaseValue("pagado"));
        assertEquals(EstadoDocumentoEmitido.COBRADO, EstadoDocumentoEmitido.fromDatabaseValue(" COBRADO "));
    }

    @Test
    void rejectsInvalidReceivedAndIssuedStatesIndependently() {
        assertThrows(IllegalArgumentException.class, () -> EstadoDocumentoRecibido.fromDatabaseValue("unknown"));
        assertThrows(IllegalArgumentException.class, () -> EstadoDocumentoEmitido.fromDatabaseValue("unknown"));
    }
}
