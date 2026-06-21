package ar.com.gaston.factx.config;

import com.zaxxer.hikari.HikariConfig;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseConfigTest {

    @Test
    void buildsHikariConfigFromAppConfig() {
        AppConfig appConfig = AppConfig.from(Map.of(
                AppConfig.ENV_DB_URL, "jdbc:postgresql://localhost:5432/factx_test",
                AppConfig.ENV_DB_USER, "tester",
                AppConfig.ENV_DB_PASSWORD, "secret",
                AppConfig.ENV_DB_POOL_SIZE, "3"
        ));

        HikariConfig hikariConfig = new DatabaseConfig(appConfig).hikariConfig();

        assertEquals("jdbc:postgresql://localhost:5432/factx_test", hikariConfig.getJdbcUrl());
        assertEquals("tester", hikariConfig.getUsername());
        assertEquals("secret", hikariConfig.getPassword());
        assertEquals(3, hikariConfig.getMaximumPoolSize());
        assertEquals(DatabaseConfig.POOL_NAME, hikariConfig.getPoolName());
    }
}
