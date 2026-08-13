package ar.com.gaston.factx.tools;

import ar.com.gaston.factx.config.AppConfig;
import ar.com.gaston.factx.config.DatabaseBootstrap;
import ar.com.gaston.factx.config.DatabaseConfig;
import ar.com.gaston.factx.domain.Documento;
import ar.com.gaston.factx.domain.EstadoDocumento;
import ar.com.gaston.factx.domain.Proveedor;
import ar.com.gaston.factx.domain.TipoDocumento;
import ar.com.gaston.factx.repository.DocumentoRepository;
import ar.com.gaston.factx.repository.ProveedorRepository;
import ar.com.gaston.factx.service.DocumentoService;
import ar.com.gaston.factx.service.ProveedorService;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DemoDataLoader {
    private static final String DEMO_CUIT = "00-00000000-0";
    private static final List<DemoSupplier> DATASET = List.of(
            new DemoSupplier(
                    "FactX Demo Proveedora Alfa",
                    List.of(
                            new DemoDocument(
                                    TipoDocumento.FACTURA,
                                    "DEMO-A-0001",
                                    LocalDate.of(2026, 1, 15),
                                    LocalDate.of(2026, 2, 15),
                                    new BigDecimal("125000.00"),
                                    EstadoDocumento.PENDIENTE,
                                    "FactX Demo Dataset v0.0.7 - document 01"
                            ),
                            new DemoDocument(
                                    TipoDocumento.TICKET,
                                    "DEMO-T-0002",
                                    LocalDate.of(2026, 1, 20),
                                    null,
                                    new BigDecimal("987.65"),
                                    EstadoDocumento.PAGADO,
                                    "FactX Demo Dataset v0.0.7 - document 02"
                            )
                    )
            ),
            new DemoSupplier(
                    "FactX Demo Insumos Beta",
                    List.of(
                            new DemoDocument(
                                    TipoDocumento.PRESUPUESTO,
                                    null,
                                    LocalDate.of(2026, 2, 1),
                                    LocalDate.of(2026, 2, 10),
                                    new BigDecimal("45500.00"),
                                    EstadoDocumento.PENDIENTE,
                                    "FactX Demo Dataset v0.0.7 - document 03"
                            )
                    )
            ),
            new DemoSupplier(
                    "FactX Demo Servicios Gamma",
                    List.of(
                            new DemoDocument(
                                    TipoDocumento.NOTA_CREDITO,
                                    "DEMO-NC-0004",
                                    LocalDate.of(2026, 2, 5),
                                    null,
                                    new BigDecimal("1200.00"),
                                    EstadoDocumento.ANULADO,
                                    "FactX Demo Dataset v0.0.7 - document 04"
                            )
                    )
            ),
            new DemoSupplier(
                    "FactX Demo Logistica Delta",
                    List.of(
                            new DemoDocument(
                                    TipoDocumento.OTRO,
                                    "DEMO-O-0005",
                                    LocalDate.of(2026, 3, 1),
                                    LocalDate.of(2026, 3, 30),
                                    new BigDecimal("785000.99"),
                                    EstadoDocumento.PENDIENTE,
                                    "FactX Demo Dataset v0.0.7 - document 05"
                            )
                    )
            ),
            new DemoSupplier(
                    "FactX Demo Comercial Epsilon",
                    List.of(
                            new DemoDocument(
                                    TipoDocumento.FACTURA,
                                    "DEMO-B-0006",
                                    LocalDate.of(2025, 12, 10),
                                    LocalDate.of(2026, 1, 10),
                                    new BigDecimal("310000.50"),
                                    EstadoDocumento.PENDIENTE,
                                    "FactX Demo Dataset v0.0.7 - document 06"
                            )
                    )
            )
    );

    private final ProveedorService proveedorService;
    private final DocumentoService documentoService;

    public DemoDataLoader(ProveedorService proveedorService, DocumentoService documentoService) {
        this.proveedorService = Objects.requireNonNull(proveedorService, "proveedorService");
        this.documentoService = Objects.requireNonNull(documentoService, "documentoService");
    }

    public static void main(String[] args) {
        DatabaseCheck.configureDevelopmentTimeZone();
        System.out.println("FactX demo data loader");

        try {
            AppConfig appConfig = AppConfig.fromEnvironment();
            DatabaseConfig databaseConfig = new DatabaseConfig(appConfig);

            try (HikariDataSource dataSource = databaseConfig.dataSource()) {
                new DatabaseBootstrap(dataSource).run();

                Jdbi jdbi = databaseConfig.jdbi(dataSource);
                ProveedorRepository proveedorRepository = new ProveedorRepository(jdbi);
                DocumentoRepository documentoRepository = new DocumentoRepository(jdbi);
                DemoDataLoader loader = new DemoDataLoader(
                        new ProveedorService(proveedorRepository),
                        new DocumentoService(documentoRepository, proveedorRepository)
                );

                LoadResult result = loader.load();
                System.out.println("Suppliers created: " + result.suppliersCreated());
                System.out.println("Suppliers already present: " + result.suppliersAlreadyPresent());
                System.out.println("Documents created: " + result.documentsCreated());
                System.out.println("Documents already present: " + result.documentsAlreadyPresent());
            }
        } catch (Exception ex) {
            System.err.println("Demo data loader failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    public LoadResult load() {
        List<Proveedor> proveedores = new ArrayList<>(proveedorService.findAll());
        int suppliersCreated = 0;
        int suppliersAlreadyPresent = 0;
        int documentsCreated = 0;
        int documentsAlreadyPresent = 0;

        for (DemoSupplier demoSupplier : DATASET) {
            Proveedor proveedor = findSupplierByName(proveedores, demoSupplier.nombre());
            if (proveedor == null) {
                proveedor = proveedorService.create(
                        demoSupplier.nombre(),
                        DEMO_CUIT,
                        "Synthetic supplier for FactX demo data."
                );
                proveedores.add(proveedor);
                suppliersCreated++;
            } else {
                suppliersAlreadyPresent++;
            }

            List<Documento> documentos = new ArrayList<>(documentoService.findByProveedorId(proveedor.id()));
            for (DemoDocument demoDocument : demoSupplier.documentos()) {
                if (containsDocumentMarker(documentos, demoDocument.marker())) {
                    documentsAlreadyPresent++;
                    continue;
                }

                Documento created = documentoService.create(
                        proveedor.id(),
                        demoDocument.tipo(),
                        demoDocument.numero(),
                        demoDocument.fechaEmision(),
                        demoDocument.fechaVencimiento(),
                        Documento.DEFAULT_MONEDA,
                        demoDocument.total(),
                        demoDocument.estado(),
                        demoDocument.marker()
                );
                documentos.add(created);
                documentsCreated++;
            }
        }

        return new LoadResult(suppliersCreated, suppliersAlreadyPresent, documentsCreated, documentsAlreadyPresent);
    }

    private static Proveedor findSupplierByName(List<Proveedor> proveedores, String nombre) {
        return proveedores.stream()
                .filter(proveedor -> proveedor.nombre().equals(nombre))
                .findFirst()
                .orElse(null);
    }

    private static boolean containsDocumentMarker(List<Documento> documentos, String marker) {
        return documentos.stream()
                .anyMatch(documento -> marker.equals(documento.notas()));
    }

    public record LoadResult(
            int suppliersCreated,
            int suppliersAlreadyPresent,
            int documentsCreated,
            int documentsAlreadyPresent
    ) {
    }

    private record DemoSupplier(String nombre, List<DemoDocument> documentos) {
    }

    private record DemoDocument(
            TipoDocumento tipo,
            String numero,
            LocalDate fechaEmision,
            LocalDate fechaVencimiento,
            BigDecimal total,
            EstadoDocumento estado,
            String marker
    ) {
    }
}
