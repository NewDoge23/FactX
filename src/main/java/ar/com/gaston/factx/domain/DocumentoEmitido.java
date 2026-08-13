package ar.com.gaston.factx.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;

public record DocumentoEmitido(
        Long id,
        Long clienteId,
        TipoDocumento tipo,
        String numero,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        String moneda,
        BigDecimal total,
        EstadoDocumentoEmitido estado,
        String notas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static final String DEFAULT_MONEDA = "ARS";

    public DocumentoEmitido {
        if (clienteId == null) {
            throw new IllegalArgumentException("Customer id is required.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Document type is required.");
        }
        if (estado == null) {
            estado = EstadoDocumentoEmitido.PENDIENTE;
        }
        numero = normalizeOptional(numero);
        moneda = normalizeMoneda(moneda);
        total = normalizeTotal(total);
        notas = normalizeOptional(notas);
    }

    public static DocumentoEmitido create(
            Long clienteId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoEmitido estado,
            String notas
    ) {
        return new DocumentoEmitido(
                null,
                clienteId,
                tipo,
                numero,
                fechaEmision,
                fechaVencimiento,
                moneda,
                total,
                estado,
                notas,
                null,
                null
        );
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private static String normalizeMoneda(String value) {
        if (value == null || value.isBlank()) {
            return DEFAULT_MONEDA;
        }
        return value.trim().toUpperCase(Locale.ROOT);
    }

    private static BigDecimal normalizeTotal(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Issued document total cannot be negative.");
        }
        return value;
    }
}
