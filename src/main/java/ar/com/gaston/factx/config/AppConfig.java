package ar.com.gaston.factx.config;

import java.util.Map;

public record AppConfig(
        String dbUrl,
        String dbUser,
        String dbPassword,
        int dbPoolSize
) {
    public static final String ENV_DB_URL = "FACTX_DB_URL";
    public static final String ENV_DB_USER = "FACTX_DB_USER";
    public static final String ENV_DB_PASSWORD = "FACTX_DB_PASSWORD";
    public static final String ENV_DB_POOL_SIZE = "FACTX_DB_POOL_SIZE";

    public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost:5432/factx";
    public static final String DEFAULT_DB_USER = "factx";
    public static final String DEFAULT_DB_PASSWORD = "factx";
    public static final int DEFAULT_DB_POOL_SIZE = 5;

    public static AppConfig fromEnvironment() {
        return from(System.getenv());
    }

    public static AppConfig from(Map<String, String> values) {
        return new AppConfig(
                read(values, ENV_DB_URL, DEFAULT_DB_URL),
                read(values, ENV_DB_USER, DEFAULT_DB_USER),
                read(values, ENV_DB_PASSWORD, DEFAULT_DB_PASSWORD),
                readPositiveInt(values, ENV_DB_POOL_SIZE, DEFAULT_DB_POOL_SIZE)
        );
    }

    private static String read(Map<String, String> values, String key, String fallback) {
        if (values == null) {
            return fallback;
        }
        String value = values.get(key);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private static int readPositiveInt(Map<String, String> values, String key, int fallback) {
        String value = read(values, key, String.valueOf(fallback));
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
