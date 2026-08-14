package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Cliente;
import ar.com.gaston.factx.domain.DocumentoEmitido;
import ar.com.gaston.factx.domain.DocumentoRecibido;
import ar.com.gaston.factx.domain.EstadoDocumentoEmitido;
import ar.com.gaston.factx.domain.EstadoDocumentoRecibido;
import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.ClienteRepository;
import ar.com.gaston.factx.repository.DocumentoEmitidoRepository;
import ar.com.gaston.factx.repository.DocumentoRecibidoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.ValidationException;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoCommercialServiceTest {
    private static final Jdbi UNUSED_JDBI = Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx");

    @Test
    void receivedDocumentDoesNotPersistWhenDatesAreInvalidOrSupplierIsMissing() {
        RecordingReceivedRepository documents = new RecordingReceivedRepository();
        DocumentoRecibidoService service = new DocumentoRecibidoService(documents, new SupplierStub(Optional.empty()));

        assertThrows(ValidationException.class, () -> service.create(1L, TipoDocumento.FACTURA, null, LocalDate.of(2026, 2, 2), LocalDate.of(2026, 2, 1), "ARS", BigDecimal.ONE, null, null));
        assertThrows(ValidationException.class, () -> service.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", BigDecimal.ONE, null, null));
        assertEquals(0, documents.createCalls);
    }

    @Test
    void issuedDocumentDoesNotPersistWhenCustomerIsMissingOrAmountIsInvalid() {
        RecordingIssuedRepository documents = new RecordingIssuedRepository();
        DocumentoEmitidoService service = new DocumentoEmitidoService(documents, new CustomerStub(Optional.empty()));

        assertThrows(ValidationException.class, () -> service.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", new BigDecimal("1000000000000.00"), null, null));
        assertThrows(ValidationException.class, () -> service.create(1L, TipoDocumento.FACTURA, null, null, null, "ARS", BigDecimal.ONE, null, null));
        assertEquals(0, documents.createCalls);
    }

    @Test
    void issuedDocumentPersistsOnlyAfterCustomerValidation() {
        RecordingIssuedRepository documents = new RecordingIssuedRepository();
        DocumentoEmitidoService service = new DocumentoEmitidoService(documents, new CustomerStub(Optional.of(Cliente.create("Cliente Demo", null, null, null))));

        service.create(1L, TipoDocumento.FACTURA, " E-1 ", null, null, "ars", BigDecimal.ONE, null, null);
        assertEquals(1, documents.createCalls);
        assertEquals("E-1", documents.created.numero());
        assertEquals("ARS", documents.created.moneda());
    }

    @Test
    void receivedDocumentUpdatePersistsOnlyAfterSupplierValidation() {
        RecordingReceivedRepository documents = new RecordingReceivedRepository();
        DocumentoRecibidoService service = new DocumentoRecibidoService(
                documents,
                new SupplierStub(Optional.of(Proveedor.create("Proveedor Demo", null, null)))
        );

        DocumentoRecibido updated = service.update(
                11L,
                1L,
                TipoDocumento.TICKET,
                " R-11 ",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15),
                "ars",
                new BigDecimal("1250.50"),
                EstadoDocumentoRecibido.PAGADO,
                " Updated received document "
        ).orElseThrow();

        assertEquals(1, documents.updateCalls);
        assertEquals(11L, updated.id());
        assertEquals("R-11", documents.updated.numero());
        assertEquals("ARS", documents.updated.moneda());
        assertEquals(EstadoDocumentoRecibido.PAGADO, documents.updated.estado());
    }

    @Test
    void receivedDocumentUpdateRequiresAnIdAndDoesNotPersistWhenInvalid() {
        RecordingReceivedRepository documents = new RecordingReceivedRepository();
        DocumentoRecibidoService service = new DocumentoRecibidoService(
                documents,
                new SupplierStub(Optional.of(Proveedor.create("Proveedor Demo", null, null)))
        );

        assertThrows(ValidationException.class, () -> service.update(
                null, 1L, TipoDocumento.FACTURA, null, null, null, "ARS", BigDecimal.ONE, null, null
        ));
        assertThrows(ValidationException.class, () -> service.update(
                11L, 1L, TipoDocumento.FACTURA, null,
                LocalDate.of(2026, 3, 2), LocalDate.of(2026, 3, 1), "ARS", BigDecimal.ONE, null, null
        ));

        assertEquals(0, documents.updateCalls);
    }

    @Test
    void issuedDocumentUpdatePersistsOnlyAfterCustomerValidation() {
        RecordingIssuedRepository documents = new RecordingIssuedRepository();
        DocumentoEmitidoService service = new DocumentoEmitidoService(
                documents,
                new CustomerStub(Optional.of(Cliente.create("Cliente Demo", null, null, null)))
        );

        DocumentoEmitido updated = service.update(
                12L,
                1L,
                TipoDocumento.PRESUPUESTO,
                " E-12 ",
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 3, 15),
                "ars",
                new BigDecimal("2500.75"),
                EstadoDocumentoEmitido.COBRADO,
                " Updated issued document "
        ).orElseThrow();

        assertEquals(1, documents.updateCalls);
        assertEquals(12L, updated.id());
        assertEquals("E-12", documents.updated.numero());
        assertEquals("ARS", documents.updated.moneda());
        assertEquals(EstadoDocumentoEmitido.COBRADO, documents.updated.estado());
    }

    @Test
    void issuedDocumentUpdateRequiresAnIdAndDoesNotPersistWhenInvalid() {
        RecordingIssuedRepository documents = new RecordingIssuedRepository();
        DocumentoEmitidoService service = new DocumentoEmitidoService(
                documents,
                new CustomerStub(Optional.of(Cliente.create("Cliente Demo", null, null, null)))
        );

        assertThrows(ValidationException.class, () -> service.update(
                null, 1L, TipoDocumento.FACTURA, null, null, null, "ARS", BigDecimal.ONE, null, null
        ));
        assertThrows(ValidationException.class, () -> service.update(
                12L, 1L, TipoDocumento.FACTURA, null, null, null, "PESO", BigDecimal.ONE, null, null
        ));

        assertEquals(0, documents.updateCalls);
    }

    private static final class SupplierStub extends ProveedorRepository {
        private final Optional<Proveedor> result;
        private SupplierStub(Optional<Proveedor> result) { super(UNUSED_JDBI); this.result = result; }
        @Override public Optional<Proveedor> findById(long id) { return result; }
    }

    private static final class CustomerStub extends ClienteRepository {
        private final Optional<Cliente> result;
        private CustomerStub(Optional<Cliente> result) { super(UNUSED_JDBI); this.result = result; }
        @Override public Optional<Cliente> findById(long id) { return result; }
    }

    private static final class RecordingReceivedRepository extends DocumentoRecibidoRepository {
        private int createCalls;
        private int updateCalls;
        private DocumentoRecibido updated;
        private RecordingReceivedRepository() { super(UNUSED_JDBI); }
        @Override public DocumentoRecibido create(DocumentoRecibido documento) { createCalls++; return documento; }
        @Override public Optional<DocumentoRecibido> update(DocumentoRecibido documento) { updateCalls++; updated = documento; return Optional.of(documento); }
    }

    private static final class RecordingIssuedRepository extends DocumentoEmitidoRepository {
        private int createCalls;
        private int updateCalls;
        private DocumentoEmitido created;
        private DocumentoEmitido updated;
        private RecordingIssuedRepository() { super(UNUSED_JDBI); }
        @Override public DocumentoEmitido create(DocumentoEmitido documento) { createCalls++; created = documento; return documento; }
        @Override public Optional<DocumentoEmitido> update(DocumentoEmitido documento) { updateCalls++; updated = documento; return Optional.of(documento); }
    }
}
