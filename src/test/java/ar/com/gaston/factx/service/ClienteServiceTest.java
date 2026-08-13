package ar.com.gaston.factx.service;

import ar.com.gaston.factx.domain.Cliente;
import ar.com.gaston.factx.repository.ClienteRepository;
import ar.com.gaston.factx.validation.ValidationException;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ClienteServiceTest {
    @Test
    void validatesAndNormalizesCustomersBeforePersistence() {
        RecordingClienteRepository repository = new RecordingClienteRepository();
        ClienteService service = new ClienteService(repository);

        assertThrows(ValidationException.class, () -> service.create(" ", null, null, null));
        service.create(" Cliente Demo ", " Demo SA ", null, null);

        assertEquals(1, repository.createCalls);
        assertEquals("Cliente Demo", repository.created.nombre());
        assertEquals("Demo SA", repository.created.razonSocial());
    }

    private static final class RecordingClienteRepository extends ClienteRepository {
        private int createCalls;
        private Cliente created;
        private RecordingClienteRepository() { super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx")); }
        @Override public Cliente create(Cliente cliente) { createCalls++; created = cliente; return cliente; }
    }
}
