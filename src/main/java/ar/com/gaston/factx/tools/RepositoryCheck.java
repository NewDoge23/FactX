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
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;

public final class RepositoryCheck {
    private RepositoryCheck() { }

    public static void main(String[] args) {
        DatabaseCheck.configureDevelopmentTimeZone();
        try {
            DatabaseConfig config = new DatabaseConfig(AppConfig.fromEnvironment());
            try (HikariDataSource dataSource = config.dataSource()) {
                new DatabaseBootstrap(dataSource).run();
                Jdbi jdbi = config.jdbi(dataSource);
                ProveedorRepository suppliers = new ProveedorRepository(jdbi);
                DocumentoRecibidoRepository received = new DocumentoRecibidoRepository(jdbi);
                ClienteRepository customers = new ClienteRepository(jdbi);
                DocumentoEmitidoRepository issued = new DocumentoEmitidoRepository(jdbi);
                Proveedor supplier = suppliers.create(Proveedor.create("FactX Repository Supplier", "00-00000000-0", "Synthetic check row"));
                Cliente customer = customers.create(Cliente.create("FactX Repository Customer", null, "00-00000000-0", "Synthetic check row"));
                DocumentoRecibido receivedDocument = received.create(DocumentoRecibido.create(supplier.id(), TipoDocumento.FACTURA, "CHECK-R", null, null, "ARS", BigDecimal.ONE, EstadoDocumentoRecibido.PENDIENTE, "Synthetic check row"));
                DocumentoEmitido issuedDocument = issued.create(DocumentoEmitido.create(customer.id(), TipoDocumento.FACTURA, "CHECK-E", null, null, "ARS", BigDecimal.ONE, EstadoDocumentoEmitido.PENDIENTE, "Synthetic check row"));
                verify(suppliers.findById(supplier.id()).isPresent(), "Supplier repository failed.");
                verify(received.findById(receivedDocument.id()).isPresent(), "Received document repository failed.");
                verify(customers.findById(customer.id()).isPresent(), "Customer repository failed.");
                verify(issued.findById(issuedDocument.id()).isPresent(), "Issued document repository failed.");
                issued.delete(issuedDocument.id());
                received.delete(receivedDocument.id());
                customers.delete(customer.id());
                suppliers.delete(supplier.id());
                System.out.println("Supplier, received document, customer and issued document repositories: OK");
            }
        } catch (Exception ex) {
            System.err.println("Repository check failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static void verify(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
