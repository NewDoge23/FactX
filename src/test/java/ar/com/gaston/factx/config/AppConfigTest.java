package ar.com.gaston.factx.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppConfigTest {

    @Test
    void usesDevelopmentDefaultsWhenValuesAreMissing() {
        AppConfig config = AppConfig.from(Map.of());

        assertEquals(AppConfig.DEFAULT_DB_URL, config.dbUrl());
        assertEquals(AppConfig.DEFAULT_DB_USER, config.dbUser());
        assertEquals(AppConfig.DEFAULT_DB_PASSWORD, config.dbPassword());
        assertEquals(AppConfig.DEFAULT_DB_POOL_SIZE, config.dbPoolSize());
    }

    @Test
    void readsConfiguredValuesFromMap() {
        AppConfig config = AppConfig.from(Map.of(
                AppConfig.ENV_DB_URL, "jdbc:postgresql://localhost:5433/custom",
                AppConfig.ENV_DB_USER, "custom_user",
                AppConfig.ENV_DB_PASSWORD, "custom_password",
                AppConfig.ENV_DB_POOL_SIZE, "8"
        ));

        assertEquals("jdbc:postgresql://localhost:5433/custom", config.dbUrl());
        assertEquals("custom_user", config.dbUser());
        assertEquals("custom_password", config.dbPassword());
        assertEquals(8, config.dbPoolSize());
    }

    @Test
    void trimsTextValues() {
        AppConfig config = AppConfig.from(Map.of(
                AppConfig.ENV_DB_URL, "  jdbc:postgresql://localhost:5432/factx_test  ",
                AppConfig.ENV_DB_USER, "  factx_test  ",
                AppConfig.ENV_DB_PASSWORD, "  secret  "
        ));

        assertEquals("jdbc:postgresql://localhost:5432/factx_test", config.dbUrl());
        assertEquals("factx_test", config.dbUser());
        assertEquals("secret", config.dbPassword());
    }

    @Test
    void invalidPoolSizeFallsBackToDefault() {
        AppConfig textValue = AppConfig.from(Map.of(AppConfig.ENV_DB_POOL_SIZE, "nope"));
        AppConfig zeroValue = AppConfig.from(Map.of(AppConfig.ENV_DB_POOL_SIZE, "0"));
        AppConfig negativeValue = AppConfig.from(Map.of(AppConfig.ENV_DB_POOL_SIZE, "-1"));

        assertEquals(AppConfig.DEFAULT_DB_POOL_SIZE, textValue.dbPoolSize());
        assertEquals(AppConfig.DEFAULT_DB_POOL_SIZE, zeroValue.dbPoolSize());
        assertEquals(AppConfig.DEFAULT_DB_POOL_SIZE, negativeValue.dbPoolSize());
    }
}
