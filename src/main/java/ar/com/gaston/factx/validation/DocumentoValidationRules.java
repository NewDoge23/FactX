package ar.com.gaston.factx.validation;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.regex.Pattern;

final class DocumentoValidationRules {
    private static final Pattern CURRENCY_CODE = Pattern.compile("[A-Z]{3}");
    private static final BigDecimal MAX_TOTAL = new BigDecimal("999999999999.99");

    private DocumentoValidationRules() {
    }

    static void validateId(Long id, String documentName) {
        if (id == null || id <= 0) {
            throw new ValidationException(documentName + " id must be positive.");
        }
    }

    static void validateCounterpartyId(Long id, String counterpartyName) {
        if (id == null || id <= 0) {
            throw new ValidationException(counterpartyName + " id must be positive.");
        }
    }

    static void validateDates(LocalDate fechaEmision, LocalDate fechaVencimiento) {
        if (fechaEmision != null
                && fechaVencimiento != null
                && fechaVencimiento.isBefore(fechaEmision)) {
            throw new ValidationException("Due date cannot be before issue date.");
        }
    }

    static void validateCurrency(String moneda) {
        if (!CURRENCY_CODE.matcher(moneda).matches()) {
            throw new ValidationException("Currency must be a three-letter uppercase code.");
        }
    }

    static void validateTotal(BigDecimal total) {
        if (total.scale() > 2) {
            throw new ValidationException("Document total cannot have more than two decimal places.");
        }
        if (total.compareTo(MAX_TOTAL) > 0) {
            throw new ValidationException("Document total cannot exceed 999999999999.99.");
        }
    }
}
