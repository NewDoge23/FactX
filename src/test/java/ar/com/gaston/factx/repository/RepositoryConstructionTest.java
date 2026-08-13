package ar.com.gaston.factx.repository;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RepositoryConstructionTest {

    @Test
    void repositoriesCanBeInstantiatedWithoutOpeningConnection() {
        Jdbi jdbi = Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx");

        assertDoesNotThrow(() -> new ProveedorRepository(jdbi));
        assertDoesNotThrow(() -> new DocumentoRecibidoRepository(jdbi));
        assertDoesNotThrow(() -> new ClienteRepository(jdbi));
        assertDoesNotThrow(() -> new DocumentoEmitidoRepository(jdbi));
    }

    @Test
    void repositoriesRequireJdbi() {
        assertThrows(NullPointerException.class, () -> new ProveedorRepository(null));
        assertThrows(NullPointerException.class, () -> new DocumentoRecibidoRepository(null));
        assertThrows(NullPointerException.class, () -> new ClienteRepository(null));
        assertThrows(NullPointerException.class, () -> new DocumentoEmitidoRepository(null));
    }
}
