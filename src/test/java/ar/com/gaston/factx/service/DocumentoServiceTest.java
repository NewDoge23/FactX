package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.EstadoDocumento;
import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.DocumentoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.ValidationException;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DocumentoServiceTest {

    @Test
    void createsANormalizedDocumentForAnExistingSupplier() {
        RecordingProveedorRepository proveedorRepository = new RecordingProveedorRepository(Optional.of(supplier()));
        RecordingDocumentoRepository documentoRepository = new RecordingDocumentoRepository();
        DocumentoService service = new DocumentoService(documentoRepository, proveedorRepository);

        Documento created = service.create(
                1L,
                TipoDocumento.FACTURA,
                "  A-0001  ",
                LocalDate.of(2026, 8, 1),
                LocalDate.of(2026, 8, 15),
                " ars ",
                new BigDecimal("1250.50"),
                null,
                "  Synthetic document  "
        );

        assertEquals(1, proveedorRepository.findByIdCalls);
        assertEquals(1, documentoRepository.createCalls);
        assertEquals("A-0001", documentoRepository.created.numero());
        assertEquals("ARS", documentoRepository.created.moneda());
        assertEquals(EstadoDocumento.PENDIENTE, documentoRepository.created.estado());
        assertEquals("Synthetic document", documentoRepository.created.notas());
        assertSame(documentoRepository.created, created);
    }

    @Test
    void rejectsInvalidDatesBeforeLookingUpTheSupplierOrPersisting() {
        RecordingProveedorRepository proveedorRepository = new RecordingProveedorRepository(Optional.of(supplier()));
        RecordingDocumentoRepository documentoRepository = new RecordingDocumentoRepository();
        DocumentoService service = new DocumentoService(documentoRepository, proveedorRepository);

        assertThrows(ValidationException.class, () -> service.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                LocalDate.of(2026, 8, 15),
                LocalDate.of(2026, 8, 1),
                "ARS",
                BigDecimal.ONE,
                EstadoDocumento.PENDIENTE,
                null
        ));

        assertEquals(0, proveedorRepository.findByIdCalls);
        assertEquals(0, documentoRepository.createCalls);
    }

    @Test
    void rejectsAmountsAboveTheDatabaseRangeBeforeLookingUpTheSupplierOrPersisting() {
        RecordingProveedorRepository proveedorRepository = new RecordingProveedorRepository(Optional.of(supplier()));
        RecordingDocumentoRepository documentoRepository = new RecordingDocumentoRepository();
        DocumentoService service = new DocumentoService(documentoRepository, proveedorRepository);

        assertThrows(ValidationException.class, () -> service.create(
                1L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                new BigDecimal("1000000000000.00"),
                EstadoDocumento.PENDIENTE,
                null
        ));

        assertEquals(0, proveedorRepository.findByIdCalls);
        assertEquals(0, documentoRepository.createCalls);
    }

    @Test
    void rejectsDocumentsForMissingSuppliersBeforePersisting() {
        RecordingProveedorRepository proveedorRepository = new RecordingProveedorRepository(Optional.empty());
        RecordingDocumentoRepository documentoRepository = new RecordingDocumentoRepository();
        DocumentoService service = new DocumentoService(documentoRepository, proveedorRepository);

        assertThrows(ValidationException.class, () -> service.create(
                99L,
                TipoDocumento.FACTURA,
                null,
                null,
                null,
                "ARS",
                BigDecimal.ONE,
                EstadoDocumento.PENDIENTE,
                null
        ));

        assertEquals(1, proveedorRepository.findByIdCalls);
        assertEquals(0, documentoRepository.createCalls);
    }

    @Test
    void updatesOnlyAfterValidatingTheDocumentAndItsSupplier() {
        RecordingProveedorRepository proveedorRepository = new RecordingProveedorRepository(Optional.of(supplier()));
        RecordingDocumentoRepository documentoRepository = new RecordingDocumentoRepository();
        DocumentoService service = new DocumentoService(documentoRepository, proveedorRepository);

        Documento updated = service.update(
                10L,
                1L,
                TipoDocumento.TICKET,
                "  T-0001  ",
                LocalDate.of(2026, 8, 1),
                null,
                "ARS",
                new BigDecimal("10.00"),
                EstadoDocumento.PAGADO,
                null
        ).orElseThrow();

        assertEquals(1, proveedorRepository.findByIdCalls);
        assertEquals(1, documentoRepository.updateCalls);
        assertEquals(10L, documentoRepository.updated.id());
        assertEquals("T-0001", documentoRepository.updated.numero());
        assertSame(documentoRepository.updated, updated);
    }

    private static Proveedor supplier() {
        return new Proveedor(1L, "Proveedor Demo", null, null, null, null);
    }

    private static final class RecordingProveedorRepository extends ProveedorRepository {
        private final Optional<Proveedor> supplier;
        private int findByIdCalls;

        private RecordingProveedorRepository(Optional<Proveedor> supplier) {
            super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx"));
            this.supplier = supplier;
        }

        @Override
        public Optional<Proveedor> findById(long id) {
            findByIdCalls++;
            return supplier;
        }
    }

    private static final class RecordingDocumentoRepository extends DocumentoRepository {
        private Documento created;
        private Documento updated;
        private int createCalls;
        private int updateCalls;

        private RecordingDocumentoRepository() {
            super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx"));
        }

        @Override
        public Documento create(Documento documento) {
            createCalls++;
            created = documento;
            return documento;
        }

        @Override
        public Optional<Documento> update(Documento documento) {
            updateCalls++;
            updated = documento;
            return Optional.of(documento);
        }
    }
}
