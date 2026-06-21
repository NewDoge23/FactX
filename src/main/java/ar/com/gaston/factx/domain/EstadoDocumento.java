package ar.com.gaston.factx.domain;

import java.util.Locale;

public enum EstadoDocumento {
    PENDIENTE,
    PAGADO,
    ANULADO;

    public static EstadoDocumento fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Document state is required.");
        }
        return EstadoDocumento.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
