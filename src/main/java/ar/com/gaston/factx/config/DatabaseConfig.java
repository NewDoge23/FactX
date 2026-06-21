package ar.com.gaston.factx.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

import javax.sql.DataSource;

public class DatabaseConfig {
    public static final String POOL_NAME = "FactXPool";

    private final AppConfig appConfig;

    public DatabaseConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(appConfig.dbUrl());
        config.setUsername(appConfig.dbUser());
        config.setPassword(appConfig.dbPassword());
        config.setMaximumPoolSize(appConfig.dbPoolSize());
        config.setPoolName(POOL_NAME);
        return config;
    }

    public HikariDataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    public Jdbi jdbi(DataSource dataSource) {
        return Jdbi.create(dataSource);
    }
}
