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
                "documento_recibido",
                "adjunto_recibido",
                "pago_realizado",
                "cliente",
                "documento_emitido",
                "adjunto_emitido",
                "cobro"
        ));

        assertTrue(missingTables.isEmpty());
    }

    @Test
    void reportsMissingCoreTablesInExpectedOrder() {
        List<String> missingTables = DatabaseBootstrap.missingCoreTables(List.of("proveedor", "pago_realizado", "cliente"));

        assertEquals(List.of("documento_recibido", "adjunto_recibido", "documento_emitido", "adjunto_emitido", "cobro"), missingTables);
    }

    @Test
    void matchesTableNamesCaseInsensitively() {
        List<String> missingTables = DatabaseBootstrap.missingCoreTables(List.of(
                "PROVEEDOR",
                "Documento_Recibido",
                "ADJUNTO_RECIBIDO",
                "pago_realizado",
                "CLIENTE",
                "Documento_Emitido",
                "ADJUNTO_EMITIDO",
                "cobro"
        ));

        assertTrue(missingTables.isEmpty());
    }
}
