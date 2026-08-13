package ar.com.gaston.factx.domain;

import java.util.Locale;

public enum EstadoDocumentoEmitido {
    PENDIENTE,
    PARCIAL,
    COBRADO,
    ANULADO;

    public static EstadoDocumentoEmitido fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Issued document state is required.");
        }
        return EstadoDocumentoEmitido.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
