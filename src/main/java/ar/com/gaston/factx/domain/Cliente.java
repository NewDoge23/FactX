package ar.com.gaston.factx.domain;

import java.time.OffsetDateTime;

public record Cliente(
        Long id,
        String nombre,
        String razonSocial,
        String cuit,
        String notas,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public Cliente {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }
        nombre = nombre.trim();
        razonSocial = normalizeOptional(razonSocial);
        cuit = normalizeOptional(cuit);
        notas = normalizeOptional(notas);
    }

    public static Cliente create(String nombre, String razonSocial, String cuit, String notas) {
        return new Cliente(null, nombre, razonSocial, cuit, notas, null, null);
    }

    private static String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
