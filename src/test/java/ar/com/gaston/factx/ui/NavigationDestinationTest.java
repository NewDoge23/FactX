package ar.com.gaston.factx.ui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class NavigationDestinationTest {

    @Test
    void keepsTheThreeShellDestinationsInTheExpectedOrder() {
        assertArrayEquals(
                new NavigationDestination[]{
                        NavigationDestination.HOME,
                        NavigationDestination.SUPPLIERS,
                        NavigationDestination.DOCUMENTS
                },
                NavigationDestination.values()
        );
    }

    @Test
    void keepsSupplierAndDocumentScreensAsExplicitFuturePlaceholders() {
        assertEquals("Proveedores — disponible en v0.1.x", NavigationDestination.SUPPLIERS.contentTitle());
        assertEquals("Documentos — disponible en v0.2.x", NavigationDestination.DOCUMENTS.contentTitle());
    }
}
