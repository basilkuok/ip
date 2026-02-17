package jarvis.ui;

import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.shape.Circle;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DialogBox extends HBox {
    private static final double AVATAR_SIZE = 36.0;
    private static final double BUBBLE_MAX_WIDTH_RATIO = 0.72;
    private static final Pattern PRIORITY_TOKEN_PATTERN = Pattern.compile("\\(p:[A-Z]+\\)");

    @FXML
    private TextFlow dialog;

    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/view/DialogBox.fxml"));
            fxmlLoader.setRoot(this);
            fxmlLoader.setController(this);
            fxmlLoader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load DialogBox.fxml.", e);
        }

        displayPicture.setImage(img);

        dialog.getStyleClass().add("bubble");
        dialog.setMinHeight(Region.USE_PREF_SIZE);
        dialog.maxWidthProperty().bind(createBubbleMaxWidthBinding());

        setAvatarSizingAndClip();

        setDialogText(text);
    }

    private DoubleBinding createBubbleMaxWidthBinding() {
        return widthProperty().multiply(BUBBLE_MAX_WIDTH_RATIO);
    }

    private void setAvatarSizingAndClip() {
        displayPicture.setFitWidth(AVATAR_SIZE);
        displayPicture.setFitHeight(AVATAR_SIZE);
        displayPicture.setPreserveRatio(true);

        Circle clip = new Circle();
        clip.centerXProperty().bind(displayPicture.fitWidthProperty().divide(2));
        clip.centerYProperty().bind(displayPicture.fitHeightProperty().divide(2));
        clip.radiusProperty().bind(displayPicture.fitWidthProperty().divide(2));
        displayPicture.setClip(clip);
    }

    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
        this.setAlignment(Pos.TOP_LEFT);
    }

    private void setDialogText(String text) {
        dialog.getChildren().clear();
        appendTextWithPriorityHighlight(text);
    }

    private void appendTextWithPriorityHighlight(String text) {
        Matcher matcher = PRIORITY_TOKEN_PATTERN.matcher(text);
        int startIndex = 0;
        while (matcher.find()) {
            appendText(text.substring(startIndex, matcher.start()), false);
            appendText(matcher.group(), true);
            startIndex = matcher.end();
        }
        appendText(text.substring(startIndex), false);
    }

    private void appendText(String segment, boolean isPriorityToken) {
        if (segment.isEmpty()) {
            return;
        }

        String[] parts = segment.split("\n", -1);
        for (int index = 0; index < parts.length; index++) {
            if (!parts[index].isEmpty()) {
                Text text = new Text(parts[index]);
                if (isPriorityToken) {
                    text.getStyleClass().add("priority-text");
                }
                dialog.getChildren().add(text);
            }

            if (index < parts.length - 1) {
                dialog.getChildren().add(new Text("\n"));
            }
        }
    }

    public static DialogBox getUserDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("user-bubble");
        dialogBox.getStyleClass().add("dialog-box");
        dialogBox.setAlignment(Pos.TOP_RIGHT);
        return dialogBox;
    }

    public static DialogBox getJarvisDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("bot-bubble");
        dialogBox.getStyleClass().add("dialog-box");
        dialogBox.flip();
        return dialogBox;
    }

    public static DialogBox getErrorDialog(String text, Image img) {
        DialogBox dialogBox = new DialogBox(text, img);
        dialogBox.dialog.getStyleClass().add("error-bubble");
        dialogBox.getStyleClass().add("dialog-box");
        dialogBox.flip();
        return dialogBox;
    }
}
