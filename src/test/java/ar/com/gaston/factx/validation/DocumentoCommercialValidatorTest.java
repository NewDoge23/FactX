package ar.com.gaston.factx.validation;

import ar.com.gaston.factx.domain.DocumentoEmitido;
import ar.com.gaston.factx.domain.DocumentoRecibido;
import ar.com.gaston.factx.domain.TipoDocumento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoCommercialValidatorTest {
    @Test
    void acceptsTheMaximumNumericAmountForBothDirections() {
        DocumentoRecibido recibido = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("999999999999.99"), null, null);
        DocumentoEmitido emitido = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("999999999999.99"), null, null);
        assertDoesNotThrow(() -> DocumentoRecibidoValidator.validateForCreate(recibido));
        assertDoesNotThrow(() -> DocumentoEmitidoValidator.validateForCreate(emitido));
    }

    @Test
    void rejectsOutOfOrderDatesForBothDirections() {
        DocumentoRecibido receivedBadDates = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), "ARS", BigDecimal.ONE, null, null);
        DocumentoEmitido issuedBadDates = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), "ARS", BigDecimal.ONE, null, null);
        assertThrows(ValidationException.class, () -> DocumentoRecibidoValidator.validateForCreate(receivedBadDates));
        assertThrows(ValidationException.class, () -> DocumentoEmitidoValidator.validateForCreate(issuedBadDates));
    }

    @Test
    void rejectsInvalidCurrencyForBothDirections() {
        DocumentoRecibido receivedInvalidCurrency = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, null, null, "PESO", BigDecimal.ONE, null, null);
        DocumentoEmitido issuedInvalidCurrency = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, "AR", BigDecimal.ONE, null, null);

        assertThrows(ValidationException.class, () -> DocumentoRecibidoValidator.validateForCreate(receivedInvalidCurrency));
        assertThrows(ValidationException.class, () -> DocumentoEmitidoValidator.validateForCreate(issuedInvalidCurrency));
    }

    @Test
    void rejectsAmountsWithMoreThanTwoDecimalPlacesForBothDirections() {
        DocumentoRecibido receivedInvalidPrecision = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("1.001"), null, null);
        DocumentoEmitido issuedInvalidPrecision = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("1.001"), null, null);

        assertThrows(ValidationException.class, () -> DocumentoRecibidoValidator.validateForCreate(receivedInvalidPrecision));
        assertThrows(ValidationException.class, () -> DocumentoEmitidoValidator.validateForCreate(issuedInvalidPrecision));
    }

    @Test
    void rejectsAmountsAboveTheNumericRangeForBothDirections() {
        DocumentoRecibido receivedBadAmount = DocumentoRecibido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("1000000000000.00"), null, null);
        DocumentoEmitido issuedBadAmount = DocumentoEmitido.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("1000000000000.00"), null, null);

        assertThrows(ValidationException.class, () -> DocumentoRecibidoValidator.validateForCreate(receivedBadAmount));
        assertThrows(ValidationException.class, () -> DocumentoEmitidoValidator.validateForCreate(issuedBadAmount));
    }
}
