package ar.com.gaston.factx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/ui/main-view.fxml"));
        Scene scene = new Scene(loader.load(), 900, 560);
        scene.getStylesheets().add(Main.class.getResource("/ui/styles.css").toExternalForm());

        stage.setTitle("FactX");
        stage.setMinWidth(720);
        stage.setMinHeight(420);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
