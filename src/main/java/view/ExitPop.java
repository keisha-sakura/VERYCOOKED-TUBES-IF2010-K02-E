package view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ExitPop {
    public static void show(StackPane root) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPickOnBounds(true);

        ImageView container = new ImageView(
                new Image(ExitPop.class.getResource("/ExitPop.png").toExternalForm())
        );
        container.setPreserveRatio(true);
        container.setFitWidth(810);

        Button yes = createImageButton("yes.png");
        Button no = createImageButton("no.png");

        yes.setCursor(javafx.scene.Cursor.HAND);
        no.setCursor(javafx.scene.Cursor.HAND);

        addHoverEffect(yes);
        addHoverEffect(no);

        yes.setOnAction( e -> {
            Platform.exit();
        });

        no.setOnAction(e ->
                root.getChildren().remove(overlay)
        );

        VBox chosenBox = new VBox(20, yes, no);
        chosenBox.setAlignment(Pos.CENTER);

        StackPane popup = new StackPane(container, chosenBox);
        StackPane.setMargin(chosenBox, new Insets(80, 0, 0, 0));
        popup.setPadding(new Insets(20));

        overlay.getChildren().add(popup);
        StackPane.setAlignment(popup, Pos.CENTER);

        root.getChildren().add(overlay);
        playPopAnimation(popup);
    }

    private static void playPopAnimation(Node node) {
        node.setScaleX(0.8);
        node.setScaleY(0.8);
        node.setOpacity(0);

        ScaleTransition st = new ScaleTransition(Duration.millis(200), node);
        st.setFromX(0.8);
        st.setFromY(0.8);
        st.setToX(1.0);
        st.setToY(1.0);

        FadeTransition ft = new FadeTransition(Duration.millis(200), node);
        ft.setFromValue(0);
        ft.setToValue(1);

        new ParallelTransition(st, ft).play();
    }

    private static Button createImageButton(String fileName) {
        ImageView iv = new ImageView(
                new Image(ExitPop.class.getResource("/"+fileName).toExternalForm())
        );
        iv.setPreserveRatio(true);
        iv.setFitWidth(500);

        Button button = new Button();
        button.setGraphic(iv);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);

        return button;
    }

    private static void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
            button.setOpacity(0.9);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
            button.setOpacity(1.0);
        });
    }
}