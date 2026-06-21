package ar.com.gaston.factx.config;

import org.flywaydb.core.Flyway;

import javax.sql.DataSource;

public class FlywayMigrator {
    private final DataSource dataSource;

    public FlywayMigrator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void migrate() {
        Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load()
                .migrate();
    }
}
