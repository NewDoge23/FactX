package ar.com.gaston.factx.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Locale;

public record DocumentoRecibido(
        Long id,
        Long proveedorId,
        TipoDocumento tipo,
        String numero,
        LocalDate fechaEmision,
        LocalDate fechaVencimiento,
        String moneda,
        BigDecimal total,
        EstadoDocumentoRecibido estado,
        String notas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static final String DEFAULT_MONEDA = "ARS";

    public DocumentoRecibido {
        if (proveedorId == null) {
            throw new IllegalArgumentException("Supplier id is required.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Document type is required.");
        }
        if (estado == null) {
            estado = EstadoDocumentoRecibido.PENDIENTE;
        }
        numero = normalizeOptional(numero);
        moneda = normalizeMoneda(moneda);
        total = normalizeTotal(total);
        notas = normalizeOptional(notas);
    }

    public static DocumentoRecibido create(
            Long proveedorId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoRecibido estado,
            String notas
    ) {
        return new DocumentoRecibido(
                null,
                proveedorId,
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
            throw new IllegalArgumentException("Received document total cannot be negative.");
        }
        return value;
    }
}
