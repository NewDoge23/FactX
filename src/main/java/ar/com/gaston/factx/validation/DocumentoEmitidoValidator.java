package ar.com.gaston.factx.validation;

import ar.com.gaston.factx.domain.DocumentoEmitido;

import java.util.Objects;

public final class DocumentoEmitidoValidator {
    private DocumentoEmitidoValidator() {
    }

    public static void validateForCreate(DocumentoEmitido documento) {
        validateForSave(documento);
    }

    public static void validateForUpdate(DocumentoEmitido documento) {
        validateForSave(documento);
        DocumentoValidationRules.validateId(documento.id(), "Issued document");
    }

    public static void validateCustomerId(Long clienteId) {
        DocumentoValidationRules.validateCounterpartyId(clienteId, "Customer");
    }

    public static void validateDocumentId(Long documentoId) {
        DocumentoValidationRules.validateId(documentoId, "Issued document");
    }

    private static void validateForSave(DocumentoEmitido documento) {
        Objects.requireNonNull(documento, "documento");
        validateCustomerId(documento.clienteId());
        DocumentoValidationRules.validateDates(documento.fechaEmision(), documento.fechaVencimiento());
        DocumentoValidationRules.validateCurrency(documento.moneda());
        DocumentoValidationRules.validateTotal(documento.total());
    }
}
