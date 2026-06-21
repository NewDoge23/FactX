package ar.com.gaston.factx.config;

import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public final class DatabaseBootstrap {
    public static final List<String> CORE_TABLES = List.of(
            "proveedor",
            "documento",
            "adjunto",
            "pago"
    );

    private final FlywayMigrator flywayMigrator;
    private final Jdbi jdbi;

    public DatabaseBootstrap(DataSource dataSource) {
        this(new FlywayMigrator(dataSource), Jdbi.create(dataSource));
    }

    DatabaseBootstrap(FlywayMigrator flywayMigrator, Jdbi jdbi) {
        this.flywayMigrator = flywayMigrator;
        this.jdbi = jdbi;
    }

    public BootstrapResult run() {
        flywayMigrator.migrate();
        validateConnection();
        verifyCoreTables();
        return new BootstrapResult(true, true, true);
    }

    void validateConnection() {
        Integer result = jdbi.withHandle(handle ->
                handle.createQuery("SELECT 1")
                        .mapTo(Integer.class)
                        .one()
        );

        if (result == null || result != 1) {
            throw new IllegalStateException("Database connection validation failed: SELECT 1 did not return 1.");
        }
    }

    void verifyCoreTables() {
        List<String> existingTables = jdbi.withHandle(handle ->
                handle.createQuery("""
                                SELECT table_name
                                FROM information_schema.tables
                                WHERE table_schema = 'public'
                                """)
                        .mapTo(String.class)
                        .list()
        );

        List<String> missingTables = missingCoreTables(existingTables);
        if (!missingTables.isEmpty()) {
            throw new IllegalStateException("Missing core database tables: " + String.join(", ", missingTables));
        }
    }

    public static List<String> missingCoreTables(Collection<String> existingTables) {
        Set<String> normalized = existingTables.stream()
                .map(tableName -> tableName.toLowerCase(Locale.ROOT))
                .collect(Collectors.toSet());

        return CORE_TABLES.stream()
                .filter(tableName -> !normalized.contains(tableName))
                .toList();
    }

    public record BootstrapResult(
            boolean connectionOk,
            boolean migrationsOk,
            boolean coreTablesOk
    ) {
    }
}
