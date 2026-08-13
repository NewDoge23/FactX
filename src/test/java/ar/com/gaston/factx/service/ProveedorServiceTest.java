package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.validation.ValidationException;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProveedorServiceTest {

    @Test
    void createsANormalizedSupplierThroughTheRepository() {
        RecordingProveedorRepository repository = new RecordingProveedorRepository();
        ProveedorService service = new ProveedorService(repository);

        Proveedor created = service.create("  Proveedor Demo  ", " 20-12345678-9 ", "  Nota ");

        assertEquals(1, repository.createCalls);
        assertEquals("Proveedor Demo", repository.created.nombre());
        assertEquals("20-12345678-9", repository.created.cuit());
        assertEquals("Nota", repository.created.notas());
        assertSame(repository.created, created);
    }

    @Test
    void rejectsInvalidInputBeforeCallingTheRepository() {
        RecordingProveedorRepository repository = new RecordingProveedorRepository();
        ProveedorService service = new ProveedorService(repository);

        assertThrows(ValidationException.class, () -> service.create(" ", null, null));
        assertEquals(0, repository.createCalls);
    }

    @Test
    void updatesANormalizedSupplierThroughTheRepository() {
        RecordingProveedorRepository repository = new RecordingProveedorRepository();
        ProveedorService service = new ProveedorService(repository);

        Proveedor updated = service.update(5L, "  Proveedor Actualizado  ", " 20-12345678-9 ", "  Nota ").orElseThrow();

        assertEquals(1, repository.updateCalls);
        assertEquals(5L, repository.updated.id());
        assertEquals("Proveedor Actualizado", repository.updated.nombre());
        assertEquals("20-12345678-9", repository.updated.cuit());
        assertSame(repository.updated, updated);
    }

    @Test
    void rejectsAnInvalidIdBeforeUpdatingOrDeleting() {
        RecordingProveedorRepository repository = new RecordingProveedorRepository();
        ProveedorService service = new ProveedorService(repository);

        assertThrows(ValidationException.class, () -> service.update(0L, "Proveedor Demo", null, null));
        assertThrows(ValidationException.class, () -> service.delete(0));
        assertEquals(0, repository.updateCalls);
        assertEquals(0, repository.deleteCalls);
    }

    private static final class RecordingProveedorRepository extends ProveedorRepository {
        private Proveedor created;
        private Proveedor updated;
        private int createCalls;
        private int updateCalls;
        private int deleteCalls;

        private RecordingProveedorRepository() {
            super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx"));
        }

        @Override
        public Proveedor create(Proveedor proveedor) {
            createCalls++;
            created = proveedor;
            return proveedor;
        }

        @Override
        public java.util.Optional<Proveedor> update(Proveedor proveedor) {
            updateCalls++;
            updated = proveedor;
            return java.util.Optional.of(proveedor);
        }

        @Override
        public boolean delete(long id) {
            deleteCalls++;
            return true;
        }
    }
}
