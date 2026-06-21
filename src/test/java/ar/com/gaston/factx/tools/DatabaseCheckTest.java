package ar.com.gaston.factx.tools;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseCheckTest {
    private TimeZone previousTimeZone;
    private String previousUserTimeZone;

    @BeforeEach
    void rememberTimeZone() {
        previousTimeZone = TimeZone.getDefault();
        previousUserTimeZone = System.getProperty("user.timezone");
    }

    @AfterEach
    void restoreTimeZone() {
        TimeZone.setDefault(previousTimeZone);
        if (previousUserTimeZone == null) {
            System.clearProperty("user.timezone");
        } else {
            System.setProperty("user.timezone", previousUserTimeZone);
        }
    }

    @Test
    void configuresPostgresCompatibleDevelopmentTimeZone() {
        DatabaseCheck.configureDevelopmentTimeZone();

        assertEquals(DatabaseCheck.DEVELOPMENT_TIME_ZONE, TimeZone.getDefault().getID());
        assertEquals(DatabaseCheck.DEVELOPMENT_TIME_ZONE, System.getProperty("user.timezone"));
    }
}
