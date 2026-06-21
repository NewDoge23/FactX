package ar.com.gaston.factx.tools;

import ar.com.gaston.factx.config.AppConfig;
import ar.com.gaston.factx.config.DatabaseBootstrap;
import ar.com.gaston.factx.config.DatabaseConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.TimeZone;

public final class DatabaseCheck {
    static final String DEVELOPMENT_TIME_ZONE = "America/Argentina/Buenos_Aires";

    private DatabaseCheck() {
    }

    public static void main(String[] args) {
        configureDevelopmentTimeZone();
        System.out.println("FactX database check");

        try {
            AppConfig appConfig = AppConfig.fromEnvironment();
            DatabaseConfig databaseConfig = new DatabaseConfig(appConfig);

            try (HikariDataSource dataSource = databaseConfig.dataSource()) {
                DatabaseBootstrap.BootstrapResult result = new DatabaseBootstrap(dataSource).run();

                System.out.println("Connection: " + status(result.connectionOk()));
                System.out.println("Flyway migrations: " + status(result.migrationsOk()));
                System.out.println("Core tables: " + status(result.coreTablesOk()));
            }
        } catch (Exception ex) {
            System.err.println("Database check failed: " + ex.getMessage());
            System.exit(1);
        }
    }

    private static String status(boolean ok) {
        return ok ? "OK" : "ERROR";
    }

    static void configureDevelopmentTimeZone() {
        TimeZone timeZone = TimeZone.getTimeZone(DEVELOPMENT_TIME_ZONE);
        TimeZone.setDefault(timeZone);
        System.setProperty("user.timezone", DEVELOPMENT_TIME_ZONE);
    }
}
