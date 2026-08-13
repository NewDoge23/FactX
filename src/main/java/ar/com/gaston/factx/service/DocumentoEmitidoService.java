package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.DocumentoEmitido;
import ar.com.gaston.factx.domain.EstadoDocumentoEmitido;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.ClienteRepository;
import ar.com.gaston.factx.repository.DocumentoEmitidoRepository;
import ar.com.gaston.factx.validation.DocumentoEmitidoValidator;
import ar.com.gaston.factx.validation.ValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class DocumentoEmitidoService {
    private final DocumentoEmitidoRepository documentoRepository;
    private final ClienteRepository clienteRepository;

    public DocumentoEmitidoService(DocumentoEmitidoRepository documentoRepository, ClienteRepository clienteRepository) {
        this.documentoRepository = Objects.requireNonNull(documentoRepository, "documentoRepository");
        this.clienteRepository = Objects.requireNonNull(clienteRepository, "clienteRepository");
    }

    public DocumentoEmitido create(
            Long clienteId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoEmitido estado,
            String notas
    ) {
        DocumentoEmitido documento = DocumentoEmitido.create(
                clienteId, tipo, numero, fechaEmision, fechaVencimiento, moneda, total, estado, notas
        );
        DocumentoEmitidoValidator.validateForCreate(documento);
        requireExistingCustomer(documento.clienteId());
        return documentoRepository.create(documento);
    }

    public Optional<DocumentoEmitido> findById(long id) {
        DocumentoEmitidoValidator.validateDocumentId(id);
        return documentoRepository.findById(id);
    }

    public List<DocumentoEmitido> findAll() {
        return documentoRepository.findAll();
    }

    public List<DocumentoEmitido> findByClienteId(long clienteId) {
        DocumentoEmitidoValidator.validateCustomerId(clienteId);
        return documentoRepository.findByClienteId(clienteId);
    }

    public Optional<DocumentoEmitido> update(
            Long id,
            Long clienteId,
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            String moneda,
            BigDecimal total,
            EstadoDocumentoEmitido estado,
            String notas
    ) {
        DocumentoEmitido documento = new DocumentoEmitido(
                id, clienteId, tipo, numero, fechaEmision, fechaVencimiento, moneda, total, estado, notas, null, null
        );
        DocumentoEmitidoValidator.validateForUpdate(documento);
        requireExistingCustomer(documento.clienteId());
        return documentoRepository.update(documento);
    }

    public boolean delete(long id) {
        DocumentoEmitidoValidator.validateDocumentId(id);
        return documentoRepository.delete(id);
    }

    private void requireExistingCustomer(long clienteId) {
        if (clienteRepository.findById(clienteId).isEmpty()) {
            throw new ValidationException("Customer does not exist: " + clienteId + ".");
        }
    }
}
