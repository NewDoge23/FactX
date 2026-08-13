package ar.com.gaston.factx.validation;

import ar.com.gaston.factx.domain.Documento;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.regex.Pattern;

public final class DocumentoValidator {
    private static final Pattern CURRENCY_CODE = Pattern.compile("[A-Z]{3}");
    private static final BigDecimal MAX_TOTAL = new BigDecimal("999999999999.99");

    private DocumentoValidator() {
    }

    public static void validateForCreate(Documento documento) {
        validateForSave(documento);
    }

    public static void validateForUpdate(Documento documento) {
        validateForSave(documento);
        if (documento.id() == null || documento.id() <= 0) {
            throw new ValidationException("Document id must be positive.");
        }
    }

    public static void validateSupplierId(Long proveedorId) {
        if (proveedorId == null || proveedorId <= 0) {
            throw new ValidationException("Supplier id must be positive.");
        }
    }

    private static void validateForSave(Documento documento) {
        Objects.requireNonNull(documento, "documento");
        validateSupplierId(documento.proveedorId());

        if (documento.fechaEmision() != null
                && documento.fechaVencimiento() != null
                && documento.fechaVencimiento().isBefore(documento.fechaEmision())) {
            throw new ValidationException("Due date cannot be before issue date.");
        }
        if (!CURRENCY_CODE.matcher(documento.moneda()).matches()) {
            throw new ValidationException("Currency must be a three-letter uppercase code.");
        }
        if (documento.total().scale() > 2) {
            throw new ValidationException("Document total cannot have more than two decimal places.");
        }
        if (documento.total().compareTo(MAX_TOTAL) > 0) {
            throw new ValidationException("Document total cannot exceed 999999999999.99.");
        }
    }
}
