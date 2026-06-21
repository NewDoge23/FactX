package ar.com.gaston.factx.config;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseBootstrapTest {

    @Test
    void reportsNoMissingCoreTablesWhenAllArePresent() {
        List<String> missingTables = DatabaseBootstrap.missingCoreTables(List.of(
                "proveedor",
                "documento",
                "adjunto",
                "pago"
        ));

        assertTrue(missingTables.isEmpty());
    }

    @Test
    void reportsMissingCoreTablesInExpectedOrder() {
        List<String> missingTables = DatabaseBootstrap.missingCoreTables(List.of("proveedor", "pago"));

        assertEquals(List.of("documento", "adjunto"), missingTables);
    }

    @Test
    void matchesTableNamesCaseInsensitively() {
        List<String> missingTables = DatabaseBootstrap.missingCoreTables(List.of(
                "PROVEEDOR",
                "Documento",
                "ADJUNTO",
                "pago"
        ));

        assertTrue(missingTables.isEmpty());
    }
}
