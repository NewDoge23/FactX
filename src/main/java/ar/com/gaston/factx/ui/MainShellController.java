package ar.com.gaston.factx.ui;

import ar.com.gaston.factx.app.AppInfo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public final class MainShellController {
    private static final String ACTIVE_NAVIGATION_STYLE = "navigation-button-active";

    @FXML
    private Label versionLabel;

    @FXML
    private Label contentTitle;

    @FXML
    private Label contentMessage;

    @FXML
    private Button homeButton;

    @FXML
    private Button suppliersButton;

    @FXML
    private Button documentsButton;

    @FXML
    private void initialize() {
        versionLabel.setText("v" + AppInfo.VERSION);
        show(NavigationDestination.HOME);
    }

    @FXML
    private void showHome() {
        show(NavigationDestination.HOME);
    }

    @FXML
    private void showSuppliers() {
        show(NavigationDestination.SUPPLIERS);
    }

    @FXML
    private void showDocuments() {
        show(NavigationDestination.DOCUMENTS);
    }

    private void show(NavigationDestination destination) {
        contentTitle.setText(destination.contentTitle());
        contentMessage.setText(destination.contentMessage());
        updateNavigationState(destination);
    }

    private void updateNavigationState(NavigationDestination destination) {
        updateNavigationButton(homeButton, destination == NavigationDestination.HOME);
        updateNavigationButton(suppliersButton, destination == NavigationDestination.SUPPLIERS);
        updateNavigationButton(documentsButton, destination == NavigationDestination.DOCUMENTS);
    }

    private void updateNavigationButton(Button button, boolean active) {
        button.getStyleClass().remove(ACTIVE_NAVIGATION_STYLE);
        if (active) {
            button.getStyleClass().add(ACTIVE_NAVIGATION_STYLE);
        }
    }
}
