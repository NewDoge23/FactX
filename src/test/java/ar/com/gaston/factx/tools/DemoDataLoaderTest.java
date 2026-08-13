package ar.com.gaston.factx.tools;

import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.EstadoDocumento;
import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.DocumentoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.service.DocumentoService;
import ar.com.gaston.factx.service.ProveedorService;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoDataLoaderTest {

    @Test
    void loadsASyntheticDatasetWithUsefulDocumentVariation() {
        InMemoryProveedorRepository proveedorRepository = new InMemoryProveedorRepository();
        InMemoryDocumentoRepository documentoRepository = new InMemoryDocumentoRepository();
        DemoDataLoader loader = loader(proveedorRepository, documentoRepository);

        DemoDataLoader.LoadResult result = loader.load();

        assertEquals(5, result.suppliersCreated());
        assertEquals(0, result.suppliersAlreadyPresent());
        assertEquals(6, result.documentsCreated());
        assertEquals(0, result.documentsAlreadyPresent());
        assertEquals(5, proveedorRepository.findAll().size());
        assertEquals(6, documentoRepository.findAllDocuments().size());
        assertTrue(proveedorRepository.findAll().stream().allMatch(proveedor -> proveedor.nombre().startsWith("FactX Demo ")));
        assertTrue(documentoRepository.findAllDocuments().stream().allMatch(documento -> "ARS".equals(documento.moneda())));
        assertTrue(documentoRepository.findAllDocuments().stream().anyMatch(documento -> documento.numero() == null));
        assertEquals(
                EnumSet.allOf(TipoDocumento.class),
                documentoRepository.findAllDocuments().stream()
                        .map(Documento::tipo)
                        .collect(java.util.stream.Collectors.toCollection(() -> EnumSet.noneOf(TipoDocumento.class)))
        );
        assertEquals(
                EnumSet.allOf(EstadoDocumento.class),
                documentoRepository.findAllDocuments().stream()
                        .map(Documento::estado)
                        .collect(java.util.stream.Collectors.toCollection(() -> EnumSet.noneOf(EstadoDocumento.class)))
        );
    }

    @Test
    void doesNotDuplicateTheDatasetOnASecondExecution() {
        InMemoryProveedorRepository proveedorRepository = new InMemoryProveedorRepository();
        InMemoryDocumentoRepository documentoRepository = new InMemoryDocumentoRepository();
        DemoDataLoader loader = loader(proveedorRepository, documentoRepository);

        loader.load();
        DemoDataLoader.LoadResult secondResult = loader.load();

        assertEquals(0, secondResult.suppliersCreated());
        assertEquals(5, secondResult.suppliersAlreadyPresent());
        assertEquals(0, secondResult.documentsCreated());
        assertEquals(6, secondResult.documentsAlreadyPresent());
        assertEquals(5, proveedorRepository.findAll().size());
        assertEquals(6, documentoRepository.findAllDocuments().size());
    }

    private static DemoDataLoader loader(
            InMemoryProveedorRepository proveedorRepository,
            InMemoryDocumentoRepository documentoRepository
    ) {
        ProveedorService proveedorService = new ProveedorService(proveedorRepository);
        DocumentoService documentoService = new DocumentoService(documentoRepository, proveedorRepository);
        return new DemoDataLoader(proveedorService, documentoService);
    }

    private static final class InMemoryProveedorRepository extends ProveedorRepository {
        private final List<Proveedor> proveedores = new ArrayList<>();
        private long nextId = 1;

        private InMemoryProveedorRepository() {
            super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx"));
        }

        @Override
        public Proveedor create(Proveedor proveedor) {
            Proveedor saved = new Proveedor(
                    nextId++,
                    proveedor.nombre(),
                    proveedor.cuit(),
                    proveedor.notas(),
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            proveedores.add(saved);
            return saved;
        }

        @Override
        public Optional<Proveedor> findById(long id) {
            return proveedores.stream().filter(proveedor -> proveedor.id() == id).findFirst();
        }

        @Override
        public List<Proveedor> findAll() {
            return List.copyOf(proveedores);
        }
    }

    private static final class InMemoryDocumentoRepository extends DocumentoRepository {
        private final List<Documento> documentos = new ArrayList<>();
        private long nextId = 1;

        private InMemoryDocumentoRepository() {
            super(Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx"));
        }

        @Override
        public Documento create(Documento documento) {
            Documento saved = new Documento(
                    nextId++,
                    documento.proveedorId(),
                    documento.tipo(),
                    documento.numero(),
                    documento.fechaEmision(),
                    documento.fechaVencimiento(),
                    documento.moneda(),
                    documento.total(),
                    documento.estado(),
                    documento.notas(),
                    OffsetDateTime.now(),
                    OffsetDateTime.now()
            );
            documentos.add(saved);
            return saved;
        }

        @Override
        public List<Documento> findByProveedorId(long proveedorId) {
            return documentos.stream()
                    .filter(documento -> documento.proveedorId() == proveedorId)
                    .toList();
        }

        private List<Documento> findAllDocuments() {
            return List.copyOf(documentos);
        }
    }
}
