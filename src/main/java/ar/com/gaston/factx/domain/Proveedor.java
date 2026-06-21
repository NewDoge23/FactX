package ar.com.gaston.factx.domain;

import java.time.OffsetDateTime;

public record Proveedor(
        Long id,
        String nombre,
        String cuit,
        String notas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public Proveedor {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Supplier name is required.");
        }
        nombre = nombre.trim();
        cuit = normalizeOptional(cuit);
        notas = normalizeOptional(notas);
    }

    public static Proveedor create(String nombre, String cuit, String notas) {
        return new Proveedor(null, nombre, cuit, notas, null, null);
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
