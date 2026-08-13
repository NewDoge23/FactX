package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.DocumentoRecibido;
import ar.com.gaston.factx.domain.EstadoDocumentoRecibido;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.DocumentoRecibidoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.DocumentoRecibidoValidator;
import ar.com.gaston.factx.validation.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DocumentoRecibidoService {
    private final DocumentoRecibidoRepository documentoRepository;
    private final ProveedorRepository proveedorRepository;

    public DocumentoRecibidoService(
            DocumentoRecibidoRepository documentoRepository,
            ProveedorRepository proveedorRepository
    ) {
        this.documentoRepository = Objects.requireNonNull(documentoRepository, "documentoRepository");
        this.proveedorRepository = Objects.requireNonNull(proveedorRepository, "proveedorRepository");
    }

    public DocumentoRecibido create(
            Long proveedorId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoRecibido estado,
            String notas
    ) {
        DocumentoRecibido documento = DocumentoRecibido.create(
                proveedorId, tipo, numero, fechaEmision, fechaVencimiento, moneda, total, estado, notas
        );
        DocumentoRecibidoValidator.validateForCreate(documento);
        requireExistingSupplier(documento.proveedorId());
        return documentoRepository.create(documento);
    }

    public Optional<DocumentoRecibido> findById(long id) {
        DocumentoRecibidoValidator.validateDocumentId(id);
        return documentoRepository.findById(id);
    }

    public List<DocumentoRecibido> findAll() {
        return documentoRepository.findAll();
    }

    public List<DocumentoRecibido> findByProveedorId(long proveedorId) {
        DocumentoRecibidoValidator.validateSupplierId(proveedorId);
        return documentoRepository.findByProveedorId(proveedorId);
    }

    public Optional<DocumentoRecibido> update(
            Long id,
            Long proveedorId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoRecibido estado,
            String notas
    ) {
        DocumentoRecibido documento = new DocumentoRecibido(
                id, proveedorId, tipo, numero, fechaEmision, fechaVencimiento, moneda, total, estado, notas, null, null
        );
        DocumentoRecibidoValidator.validateForUpdate(documento);
        requireExistingSupplier(documento.proveedorId());
        return documentoRepository.update(documento);
    }

    public boolean delete(long id) {
        DocumentoRecibidoValidator.validateDocumentId(id);
        return documentoRepository.delete(id);
    }

    private void requireExistingSupplier(long proveedorId) {
        if (proveedorRepository.findById(proveedorId).isEmpty()) {
            throw new ValidationException("Supplier does not exist: " + proveedorId + ".");
        }
    }
}
