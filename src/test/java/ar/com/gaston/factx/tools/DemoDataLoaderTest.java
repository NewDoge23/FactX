package ar.com.gaston.factx.tools;

import ar.com.gaston.factx.domain.Cliente;
import ar.com.gaston.factx.domain.DocumentoEmitido;
import ar.com.gaston.factx.domain.DocumentoRecibido;
import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.repository.ClienteRepository;
import ar.com.gaston.factx.repository.DocumentoEmitidoRepository;
import ar.com.gaston.factx.repository.DocumentoRecibidoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.service.ClienteService;
import ar.com.gaston.factx.service.DocumentoEmitidoService;
import ar.com.gaston.factx.service.DocumentoRecibidoService;
import ar.com.gaston.factx.service.ProveedorService;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DemoDataLoaderTest {
    private static final Jdbi UNUSED_JDBI = Jdbi.create("jdbc:postgresql://localhost:1/factx_unreachable", "factx", "factx");

    @Test
    void loadsBothDirectionsWithoutDuplicatingOnTheSecondRun() {
        Suppliers suppliers = new Suppliers();
        Customers customers = new Customers();
        Received received = new Received();
        Issued issued = new Issued();
        DemoDataLoader loader = new DemoDataLoader(new ProveedorService(suppliers), new DocumentoRecibidoService(received, suppliers), new ClienteService(customers), new DocumentoEmitidoService(issued, customers));

        DemoDataLoader.LoadResult first = loader.load();
        DemoDataLoader.LoadResult second = loader.load();

        assertEquals(5, first.demoSuppliers());
        assertEquals(6, first.receivedDocumentsCreated());
        assertEquals(4, first.demoCustomers());
        assertEquals(5, first.issuedDocumentsCreated());
        assertEquals(0, second.demoSuppliers());
        assertEquals(0, second.receivedDocumentsCreated());
        assertEquals(0, second.demoCustomers());
        assertEquals(0, second.issuedDocumentsCreated());
    }

    @Test
    void loadsUsefulVariationForBothDocumentDirections() {
        Suppliers suppliers = new Suppliers();
        Customers customers = new Customers();
        Received received = new Received();
        Issued issued = new Issued();
        DemoDataLoader loader = new DemoDataLoader(new ProveedorService(suppliers), new DocumentoRecibidoService(received, suppliers), new ClienteService(customers), new DocumentoEmitidoService(issued, customers));

        loader.load();

        assertEquals(6, received.values.size());
        assertEquals(5, issued.values.size());
        assertTrue(received.values.stream().map(DocumentoRecibido::tipo).distinct().count() > 1);
        assertTrue(issued.values.stream().map(DocumentoEmitido::tipo).distinct().count() > 1);
        assertTrue(received.values.stream().map(DocumentoRecibido::estado).distinct().count() > 1);
        assertTrue(issued.values.stream().map(DocumentoEmitido::estado).distinct().count() > 1);
        assertTrue(received.values.stream().allMatch(documento -> "ARS".equals(documento.moneda())));
        assertTrue(issued.values.stream().allMatch(documento -> "ARS".equals(documento.moneda())));
        assertTrue(received.values.stream().anyMatch(documento -> documento.numero() == null));
    }

    private static final class Suppliers extends ProveedorRepository {
        private final List<Proveedor> values = new ArrayList<>(); private long nextId = 1;
        Suppliers() { super(UNUSED_JDBI); }
        @Override public Proveedor create(Proveedor value) { Proveedor saved = new Proveedor(nextId++, value.nombre(), value.cuit(), value.notas(), null, null); values.add(saved); return saved; }
        @Override public List<Proveedor> findAll() { return List.copyOf(values); }
        @Override public Optional<Proveedor> findById(long id) { return values.stream().filter(value -> value.id() == id).findFirst(); }
    }
    private static final class Customers extends ClienteRepository {
        private final List<Cliente> values = new ArrayList<>(); private long nextId = 1;
        Customers() { super(UNUSED_JDBI); }
        @Override public Cliente create(Cliente value) { Cliente saved = new Cliente(nextId++, value.nombre(), value.razonSocial(), value.cuit(), value.notas(), null, null); values.add(saved); return saved; }
        @Override public List<Cliente> findAll() { return List.copyOf(values); }
        @Override public Optional<Cliente> findById(long id) { return values.stream().filter(value -> value.id() == id).findFirst(); }
    }
    private static final class Received extends DocumentoRecibidoRepository {
        private final List<DocumentoRecibido> values = new ArrayList<>(); private long nextId = 1;
        Received() { super(UNUSED_JDBI); }
        @Override public DocumentoRecibido create(DocumentoRecibido value) { DocumentoRecibido saved = new DocumentoRecibido(nextId++, value.proveedorId(), value.tipo(), value.numero(), value.fechaEmision(), value.fechaVencimiento(), value.moneda(), value.total(), value.estado(), value.notas(), null, null); values.add(saved); return saved; }
        @Override public List<DocumentoRecibido> findByProveedorId(long id) { return values.stream().filter(value -> value.proveedorId() == id).toList(); }
    }
    private static final class Issued extends DocumentoEmitidoRepository {
        private final List<DocumentoEmitido> values = new ArrayList<>(); private long nextId = 1;
        Issued() { super(UNUSED_JDBI); }
        @Override public DocumentoEmitido create(DocumentoEmitido value) { DocumentoEmitido saved = new DocumentoEmitido(nextId++, value.clienteId(), value.tipo(), value.numero(), value.fechaEmision(), value.fechaVencimiento(), value.moneda(), value.total(), value.estado(), value.notas(), null, null); values.add(saved); return saved; }
        @Override public List<DocumentoEmitido> findByClienteId(long id) { return values.stream().filter(value -> value.clienteId() == id).toList(); }
    }
}
