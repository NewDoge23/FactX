package ar.com.gaston.factx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ui/main-shell.fxml"));
        Scene scene = new Scene(loader.load(), 1000, 640);
        scene.getStylesheets().add(Main.class.getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("FactX - Control interno de comprobantes");
        stage.setMinWidth(860);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
