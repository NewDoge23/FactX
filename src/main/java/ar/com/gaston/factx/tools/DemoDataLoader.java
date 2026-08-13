package ar.com.gaston.factx.tools;

import ar.com.gaston.factx.config.AppConfig;
import ar.com.gaston.factx.config.DatabaseBootstrap;
import ar.com.gaston.factx.config.DatabaseConfig;
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
import ar.com.gaston.factx.service.ClienteService;
import ar.com.gaston.factx.service.DocumentoEmitidoService;
import ar.com.gaston.factx.service.DocumentoRecibidoService;
import ar.com.gaston.factx.service.ProveedorService;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public final class DemoDataLoader {
    private static final String DEMO_CUIT = "00-00000000-0";
    private static final List<ReceivedFixture> RECEIVED_FIXTURES = List.of(
            new ReceivedFixture("FactX Demo Proveedora Alfa", TipoDocumento.FACTURA, "DEMO-R-0001", "125000.00", EstadoDocumentoRecibido.PENDIENTE),
            new ReceivedFixture("FactX Demo Insumos Beta", TipoDocumento.TICKET, "DEMO-R-0002", "987.65", EstadoDocumentoRecibido.PAGADO),
            new ReceivedFixture("FactX Demo Servicios Gamma", TipoDocumento.PRESUPUESTO, null, "45500.00", EstadoDocumentoRecibido.PARCIAL),
            new ReceivedFixture("FactX Demo Logistica Delta", TipoDocumento.NOTA_CREDITO, "DEMO-R-0004", "1200.00", EstadoDocumentoRecibido.ANULADO),
            new ReceivedFixture("FactX Demo Comercial Epsilon", TipoDocumento.OTRO, "DEMO-R-0005", "785000.99", EstadoDocumentoRecibido.PENDIENTE),
            new ReceivedFixture("FactX Demo Proveedora Alfa", TipoDocumento.FACTURA, "DEMO-R-0006", "310000.50", EstadoDocumentoRecibido.PENDIENTE)
    );
    private static final List<IssuedFixture> ISSUED_FIXTURES = List.of(
            new IssuedFixture("FactX Demo Cliente Norte", TipoDocumento.FACTURA, "DEMO-E-0001", "89000.00", EstadoDocumentoEmitido.PENDIENTE),
            new IssuedFixture("FactX Demo Cliente Sur", TipoDocumento.TICKET, "DEMO-E-0002", "1250.50", EstadoDocumentoEmitido.COBRADO),
            new IssuedFixture("FactX Demo Cliente Este", TipoDocumento.PRESUPUESTO, "DEMO-E-0003", "45000.00", EstadoDocumentoEmitido.PARCIAL),
            new IssuedFixture("FactX Demo Cliente Oeste", TipoDocumento.NOTA_CREDITO, "DEMO-E-0004", "3400.00", EstadoDocumentoEmitido.ANULADO),
            new IssuedFixture("FactX Demo Cliente Norte", TipoDocumento.FACTURA, "DEMO-E-0005", "167500.75", EstadoDocumentoEmitido.PENDIENTE)
    );

    private final ProveedorService proveedorService;
    private final DocumentoRecibidoService recibidoService;
    private final ClienteService clienteService;
    private final DocumentoEmitidoService emitidoService;

    public DemoDataLoader(
            ProveedorService proveedorService,
            DocumentoRecibidoService recibidoService,
            ClienteService clienteService,
            DocumentoEmitidoService emitidoService
    ) {
        this.proveedorService = Objects.requireNonNull(proveedorService, "proveedorService");
        this.recibidoService = Objects.requireNonNull(recibidoService, "recibidoService");
        this.clienteService = Objects.requireNonNull(clienteService, "clienteService");
        this.emitidoService = Objects.requireNonNull(emitidoService, "emitidoService");
    }

    public static void main(String[] args) {
        DatabaseCheck.configureDevelopmentTimeZone();
        try {
            DatabaseConfig databaseConfig = new DatabaseConfig(AppConfig.fromEnvironment());
            try (HikariDataSource dataSource = databaseConfig.dataSource()) {
                new DatabaseBootstrap(dataSource).run();
                Jdbi jdbi = databaseConfig.jdbi(dataSource);
                DemoDataLoader loader = new DemoDataLoader(
                        new ProveedorService(new ProveedorRepository(jdbi)),
                        new DocumentoRecibidoService(new DocumentoRecibidoRepository(jdbi), new ProveedorRepository(jdbi)),
                        new ClienteService(new ClienteRepository(jdbi)),
                        new DocumentoEmitidoService(new DocumentoEmitidoRepository(jdbi), new ClienteRepository(jdbi))
                );
                System.out.println(loader.load());
            }
        } catch (Exception ex) {
            System.err.println("Demo data loader failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    public LoadResult load() {
        int suppliersCreated = 0;
        int receivedCreated = 0;
        for (ReceivedFixture fixture : RECEIVED_FIXTURES) {
            Proveedor supplier = proveedorService.findAll().stream()
                    .filter(value -> value.nombre().equals(fixture.supplierName()))
                    .findFirst()
                    .orElse(null);
            if (supplier == null) {
                supplier = proveedorService.create(fixture.supplierName(), DEMO_CUIT, "Synthetic FactX demo supplier.");
                suppliersCreated++;
            }
            if (recibidoService.findByProveedorId(supplier.id()).stream().noneMatch(value ->
                    fixture.marker().equals(value.notas())
                            || (value.notas() != null && value.notas().startsWith("FactX Demo Dataset v0.0.7")))) {
                recibidoService.create(supplier.id(), fixture.tipo(), fixture.numero(), LocalDate.of(2026, 1, 15), LocalDate.of(2026, 2, 15), DocumentoRecibido.DEFAULT_MONEDA, new BigDecimal(fixture.total()), fixture.estado(), fixture.marker());
                receivedCreated++;
            }
        }
        int customersCreated = 0;
        int issuedCreated = 0;
        for (IssuedFixture fixture : ISSUED_FIXTURES) {
            Cliente customer = clienteService.findAll().stream()
                    .filter(value -> value.nombre().equals(fixture.customerName()))
                    .findFirst()
                    .orElse(null);
            if (customer == null) {
                customer = clienteService.create(fixture.customerName(), null, DEMO_CUIT, "Synthetic FactX demo customer.");
                customersCreated++;
            }
            if (emitidoService.findByClienteId(customer.id()).stream().noneMatch(value -> fixture.marker().equals(value.notas()))) {
                emitidoService.create(customer.id(), fixture.tipo(), fixture.numero(), LocalDate.of(2026, 1, 20), LocalDate.of(2026, 2, 20), DocumentoEmitido.DEFAULT_MONEDA, new BigDecimal(fixture.total()), fixture.estado(), fixture.marker());
                issuedCreated++;
            }
        }
        return new LoadResult(suppliersCreated, receivedCreated, customersCreated, issuedCreated);
    }

    public record LoadResult(int demoSuppliers, int receivedDocumentsCreated, int demoCustomers, int issuedDocumentsCreated) {
    }

    private record ReceivedFixture(String supplierName, TipoDocumento tipo, String numero, String total, EstadoDocumentoRecibido estado) {
        String marker() { return "FactX Demo Received v0.0.10 - " + numero; }
    }

    private record IssuedFixture(String customerName, TipoDocumento tipo, String numero, String total, EstadoDocumentoEmitido estado) {
        String marker() { return "FactX Demo Issued v0.0.10 - " + numero; }
    }
}
