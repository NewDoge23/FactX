package ar.com.gaston.factx.validation;

import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.TipoDocumento;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoValidatorTest {

    @Test
    void acceptsAConsistentDocument() {
        Documento documento = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                "A-0001",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 15),
                "ars",
                new BigDecimal("1250.50"),
                null,
                null
        );

        assertDoesNotThrow(() -> DocumentoValidator.validateForCreate(documento));
    }

    @Test
    void acceptsTheMaximumPostgresNumericAmount() {
        Documento documento = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                new BigDecimal("999999999999.99"),
                null,
                null
        );

        assertDoesNotThrow(() -> DocumentoValidator.validateForCreate(documento));
    }

    @Test
    void rejectsAmountsAboveThePostgresNumericRange() {
        Documento documento = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                new BigDecimal("1000000000000.00"),
                null,
                null
        );

        assertThrows(ValidationException.class, () -> DocumentoValidator.validateForCreate(documento));
    }

    @Test
    void rejectsDatesThatAreOutOfOrder() {
        Documento documento = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 1),
                "ARS",
                BigDecimal.ONE,
                null,
                null
        );

        assertThrows(ValidationException.class, () -> DocumentoValidator.validateForCreate(documento));
    }

    @Test
    void rejectsUnsupportedCurrencyPrecisionAndMissingDocumentIds() {
        Documento invalidCurrency = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "PESO",
                BigDecimal.ONE,
                null,
                null
        );
        Documento invalidPrecision = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                new BigDecimal("1.001"),
                null,
                null
        );
        Documento missingId = Documento.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                BigDecimal.ONE,
                null,
                null
        );

        assertThrows(ValidationException.class, () -> DocumentoValidator.validateForCreate(invalidCurrency));
        assertThrows(ValidationException.class, () -> DocumentoValidator.validateForCreate(invalidPrecision));
        assertThrows(ValidationException.class, () -> DocumentoValidator.validateForUpdate(missingId));
    }
}
