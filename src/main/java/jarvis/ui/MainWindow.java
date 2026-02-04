package jarvis.ui;

import jarvis.JarvisGui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

/**
 * Controller for {@code MainWindow.fxml}.
 */
public class MainWindow {
    @FXML
    private ScrollPane scrollPane;

    @FXML
    private VBox dialogContainer;

    @FXML
    private TextField userInput;

    private JarvisGui Jarvis;
    private Image JarvisImage;
    private Image userImage;

    /**
     * Injects the Jarvis GUI logic.
     */
    public void setJarvis(JarvisGui a) {
        Jarvis = a;
        dialogContainer.getChildren().add(
                DialogBox.getJarvisDialog(Jarvis.getWelcomeMessage(), JarvisImage)
        );
    }

    @FXML
    public void initialize() {
        scrollPane.setContent(dialogContainer);
        dialogContainer.heightProperty().addListener((observable) -> scrollPane.setVvalue(1.0));

        JarvisImage = new Image(this.getClass().getResourceAsStream("/images/DaDuke.png"));
        userImage = new Image(this.getClass().getResourceAsStream("/images/DaUser.png"));
    }

    @FXML
    private void handleUserInput() {
        String userText = userInput.getText();
        String JarvisText = Jarvis.getResponse(userText);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(userText, userImage),
                DialogBox.getJarvisDialog(JarvisText, JarvisImage)
        );
        userInput.clear();

        if (Jarvis.shouldExit(userText)) {
            Platform.exit();
        }
    }
}
