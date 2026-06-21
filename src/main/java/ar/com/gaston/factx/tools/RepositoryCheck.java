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
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class RepositoryCheck {
    private RepositoryCheck() {
    }

    public static void main(String[] args) {
        DatabaseCheck.configureDevelopmentTimeZone();
        System.out.println("FactX repository check");

        Long proveedorId = null;
        Long documentoId = null;

        try {
            AppConfig appConfig = AppConfig.fromEnvironment();
            DatabaseConfig databaseConfig = new DatabaseConfig(appConfig);

            try (HikariDataSource dataSource = databaseConfig.dataSource()) {
                new DatabaseBootstrap(dataSource).run();

                Jdbi jdbi = databaseConfig.jdbi(dataSource);
                ProveedorRepository proveedorRepository = new ProveedorRepository(jdbi);
                DocumentoRepository documentoRepository = new DocumentoRepository(jdbi);

                String suffix = UUID.randomUUID().toString();
                Proveedor proveedor = proveedorRepository.create(Proveedor.create(
                        "FactX Repository Check " + suffix,
                        "00-00000000-0",
                        "Synthetic supplier for repository check"
                ));
                proveedorId = proveedor.id();

                Documento documento = documentoRepository.create(Documento.create(
                        proveedor.id(),
                        TipoDocumento.FACTURA,
                        "CHECK-" + suffix,
                        LocalDate.now(),
                        null,
                        "ARS",
                        BigDecimal.ONE,
                        EstadoDocumento.PENDIENTE,
                        "Synthetic document for repository check"
                ));
                documentoId = documento.id();

                verify(proveedorRepository.findById(proveedor.id()).isPresent(), "Created supplier was not found.");
                verify(documentoRepository.findById(documento.id()).isPresent(), "Created document was not found.");
                verify(
                        documentoRepository.findByProveedorId(proveedor.id()).stream()
                                .anyMatch(found -> found.id().equals(documento.id())),
                        "Document was not found by supplier."
                );

                System.out.println("Supplier repository: OK");
                System.out.println("Document repository: OK");

                documentoRepository.delete(documento.id());
                documentoId = null;
                proveedorRepository.delete(proveedor.id());
                proveedorId = null;
            }
        } catch (Exception ex) {
            System.err.println("Repository check failed: " + ex.getMessage());
            System.exit(1);
        } finally {
            if (documentoId != null || proveedorId != null) {
                System.err.println("Repository check cleanup warning: synthetic data may need manual cleanup.");
            }
        }
    }

    private static void verify(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
