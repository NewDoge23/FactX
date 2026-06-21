package ar.com.gaston.factx;

import ar.com.gaston.factx.app.AppInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppInfoTest {

    @Test
    void exposesInitialVersion() {
        assertEquals("FactX", AppInfo.NAME);
        assertEquals("0.0.2", AppInfo.VERSION);
    }
}
