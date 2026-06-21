package ar.com.gaston.factx.domain;

import java.util.Locale;

public enum TipoDocumento {
    FACTURA,
    TICKET,
    PRESUPUESTO,
    NOTA_CREDITO,
    OTRO;

    public static TipoDocumento fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Document type is required.");
        }
        return TipoDocumento.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
