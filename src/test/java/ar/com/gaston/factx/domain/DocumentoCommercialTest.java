package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoCommercialTest {
    @Test
    void receivedAndIssuedDocumentsKeepSeparateSettlementStates() {
        DocumentoRecibido recibido = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, null, null, "ars", BigDecimal.ONE, null, null);
        DocumentoEmitido emitido = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, "ars", BigDecimal.ONE, null, null);

        assertEquals(EstadoDocumentoRecibido.PENDIENTE, recibido.estado());
        assertEquals(EstadoDocumentoEmitido.PENDIENTE, emitido.estado());
        assertEquals("ARS", recibido.moneda());
        assertEquals("ARS", emitido.moneda());
    }

    @Test
    void documentsRejectMissingCounterpartiesAndNegativeTotals() {
        assertThrows(IllegalArgumentException.class, () -> DocumentoRecibido.create(null, TipoDocumento.FACTURA, null, null, null, null, BigDecimal.ZERO, null, null));
        assertThrows(IllegalArgumentException.class, () -> DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, null, BigDecimal.valueOf(-1), null, null));
    }
}
