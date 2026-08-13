package ar.com.gaston.factx.domain;

import java.util.Locale;

public enum EstadoDocumentoRecibido {
    PENDIENTE,
    PARCIAL,
    PAGADO,
    ANULADO;

    public static EstadoDocumentoRecibido fromDatabaseValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Received document state is required.");
        }
        return EstadoDocumentoRecibido.valueOf(value.trim().toUpperCase(Locale.ROOT));
    }
}
