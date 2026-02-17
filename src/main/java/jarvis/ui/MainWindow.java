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
        JarvisGui.ResponseResult response = Jarvis.getResponseWithStatus(userText);

        if (userText != null && !userText.isBlank()) {
            dialogContainer.getChildren().add(DialogBox.getUserDialog(userText, userImage));
        }

        dialogContainer.getChildren().add(
                response.isError()
                        ? DialogBox.getErrorDialog(response.text(), JarvisImage)
                        : DialogBox.getJarvisDialog(response.text(), JarvisImage)
        );
        userInput.clear();
        userInput.requestFocus();

        if (Jarvis.shouldExit(userText)) {
            Platform.exit();
        }
    }
}
