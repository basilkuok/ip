package jarvis;

import jarvis.ui.MainWindow;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.application.Application;

import java.io.IOException;
import java.nio.file.Path;

/**
 * A GUI for Jarvis using FXML.
 */
public class Main extends Application {
    private static final Path DEFAULT_DATA_FILE_PATH = Path.of("data", "Jarvis.txt");

    private final JarvisGui jarvis = new JarvisGui(DEFAULT_DATA_FILE_PATH);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(
                Main.class.getResource("/view/styles.css").toExternalForm()
            );

            stage.setScene(scene);
            stage.setWidth(400.0);
            stage.setHeight(600.0);
            stage.setMinWidth(400.0);
            stage.setMinHeight(600.0);
            stage.setResizable(true);
            stage.setTitle("Jarvis");

            fxmlLoader.<MainWindow>getController().setJarvis(jarvis);
            stage.show();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load MainWindow.fxml.", e);
        }
    }
}
