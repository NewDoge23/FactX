package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.EstadoDocumento;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.DocumentoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.DocumentoValidator;
import ar.com.gaston.factx.validation.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DocumentoService {
    private final DocumentoRepository documentoRepository;
    private final ProveedorRepository proveedorRepository;

    public DocumentoService(DocumentoRepository documentoRepository, ProveedorRepository proveedorRepository) {
        this.documentoRepository = Objects.requireNonNull(documentoRepository, "documentoRepository");
        this.proveedorRepository = Objects.requireNonNull(proveedorRepository, "proveedorRepository");
    }

    public Documento create(
            Long proveedorId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumento estado,
            String notas
    ) {
        Documento documento = Documento.create(
                proveedorId,
                tipo,
                numero,
                fechaEmision,
                fechaVencimiento,
                moneda,
                total,
                estado,
                notas
        );
        DocumentoValidator.validateForCreate(documento);
        requireExistingSupplier(documento.proveedorId());
        return documentoRepository.create(documento);
    }

    public Optional<Documento> findById(long id) {
        validateDocumentId(id);
        return documentoRepository.findById(id);
    }

    public List<Documento> findAll() {
        return documentoRepository.findAll();
    }

    public List<Documento> findByProveedorId(long proveedorId) {
        DocumentoValidator.validateSupplierId(proveedorId);
        return documentoRepository.findByProveedorId(proveedorId);
    }

    public Optional<Documento> update(
            Long id,
            Long proveedorId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumento estado,
            String notas
    ) {
        Documento documento = new Documento(
                id,
                proveedorId,
                tipo,
                numero,
                fechaEmision,
                fechaVencimiento,
                moneda,
                total,
                estado,
                notas,
                null,
                null
        );
        DocumentoValidator.validateForUpdate(documento);
        requireExistingSupplier(documento.proveedorId());
        return documentoRepository.update(documento);
    }

    public boolean delete(long id) {
        validateDocumentId(id);
        return documentoRepository.delete(id);
    }

    private void requireExistingSupplier(long proveedorId) {
        if (proveedorRepository.findById(proveedorId).isEmpty()) {
            throw new ValidationException("Supplier does not exist: " + proveedorId + ".");
        }
    }

    private static void validateDocumentId(long id) {
        if (id <= 0) {
            throw new ValidationException("Document id must be positive.");
        }
    }
}
