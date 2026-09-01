package bro.gui;

import java.io.IOException;

import bro.Bro;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/** Configures and displays Bro's primary JavaFX stage. */
public class Main extends Application {
    private static final String MAIN_WINDOW_FXML = "/view/MainWindow.fxml";
    private static final String WINDOW_TITLE = "Bro";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(MAIN_WINDOW_FXML));
        Parent root = loader.load();
        MainWindow mainWindow = loader.getController();
        mainWindow.setBro(new Bro());

        stage.setMinHeight(520);
        stage.setMinWidth(380);
        stage.setScene(new Scene(root));
        stage.setTitle(WINDOW_TITLE);
        stage.show();
    }
}
