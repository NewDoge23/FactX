package ar.com.gaston.factx.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoTest {

    @Test
    void appliesDefaultsAndTrimsText() {
        Documento documento = Documento.create(
                10L,
                TipoDocumento.FACTURA,
                "  A-0001  ",
                LocalDate.of(2026, 6, 21),
                null,
                " ars ",
                null,
                null,
                "  Nota  "
        );

        assertNull(documento.id());
        assertEquals(10L, documento.proveedorId());
        assertEquals("A-0001", documento.numero());
        assertEquals("ARS", documento.moneda());
        assertEquals(BigDecimal.ZERO, documento.total());
        assertEquals(EstadoDocumento.PENDIENTE, documento.estado());
        assertEquals("Nota", documento.notas());
    }

    @Test
    void rejectsMissingSupplier() {
        assertThrows(IllegalArgumentException.class, () -> Documento.create(
                null,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                null,
                BigDecimal.ZERO,
                EstadoDocumento.PENDIENTE,
                null
        ));
    }

    @Test
    void rejectsNegativeTotal() {
        assertThrows(IllegalArgumentException.class, () -> Documento.create(
                10L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(-1),
                EstadoDocumento.PENDIENTE,
                null
        ));
    }
}
