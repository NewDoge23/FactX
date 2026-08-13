package ar.com.gaston.factx.validation;

import ar.com.gaston.factx.domain.DocumentoRecibido;

import java.util.Objects;

public final class DocumentoRecibidoValidator {
    private DocumentoRecibidoValidator() {
    }

    public static void validateForCreate(DocumentoRecibido documento) {
        validateForSave(documento);
    }

    public static void validateForUpdate(DocumentoRecibido documento) {
        validateForSave(documento);
        DocumentoValidationRules.validateId(documento.id(), "Received document");
    }

    public static void validateSupplierId(Long proveedorId) {
        DocumentoValidationRules.validateCounterpartyId(proveedorId, "Supplier");
    }

    public static void validateDocumentId(Long documentoId) {
        DocumentoValidationRules.validateId(documentoId, "Received document");
    }

    private static void validateForSave(DocumentoRecibido documento) {
        Objects.requireNonNull(documento, "documento");
        validateSupplierId(documento.proveedorId());
        DocumentoValidationRules.validateDates(documento.fechaEmision(), documento.fechaVencimiento());
        DocumentoValidationRules.validateCurrency(documento.moneda());
        DocumentoValidationRules.validateTotal(documento.total());
    }
}
