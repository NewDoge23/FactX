package ar.com.gaston.factx.ui;

public enum NavigationDestination {
    HOME(
            "Prepará tu espacio de trabajo",
            "Esta pantalla organiza la futura gestión interna de comprobantes."
    ),
    SUPPLIERS(
            "Proveedores — disponible en v0.1.x",
            "La gestión de proveedores se incorporará en una etapa posterior."
    ),
    DOCUMENTS(
            "Documentos — disponible en v0.2.x",
            "La gestión de documentos se incorporará después de proveedores."
    );

    private final String contentTitle;
    private final String contentMessage;

    NavigationDestination(String contentTitle, String contentMessage) {
        this.contentTitle = contentTitle;
        this.contentMessage = contentMessage;
    }

    public String contentTitle() {
        return contentTitle;
    }

    public String contentMessage() {
        return contentMessage;
    }
}
