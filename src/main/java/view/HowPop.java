package view;

import javafx.animation.*;
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
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

public class HowPop {
    public static void show(StackPane root) {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPickOnBounds(true);

        ImageView container = new ImageView(
                new Image(HowPop.class.getResource("/HowPop.png").toExternalForm())
        );
        container.setPreserveRatio(true);
        container.setFitWidth(810);

        Text text = new Text("Untuk memainkan VeryCooked, pemain disediakan berbagai tombol control (key button) \n yang berfungsi untuk mengoperasikan karakter serta melakukan berbagai \naksi di dalam permainan. Setiap tombol memiliki peran tertentu dan membantu pemain.\n" +
                "W: Menggerakan chef ke arah atas\n" +
                "A: Menggerakan chef ke arah kiri\n" +
                "S: Menggerakan chef ke arah bawah\n" +
                "D: Menggerakan chef ke arah kanan\n" +
                "C: Melakukan aksi drop/pickup item\n" +
                "V: Melakukan interaksi engan station\n" +
                "B: Mengganti chef yang aktif\n" +
                "Q: Keluar dari permainan");

        text.setFont(Font.font("Arial", 17));

        Button back = createBackButton();

        VBox backBox = new VBox(back);
        backBox.setAlignment(Pos.BOTTOM_CENTER);
        backBox.setPadding(new Insets(0, 0, 25, 0));
        back.setCursor(javafx.scene.Cursor.HAND);
        addHoverEffect(back);
        back.setOnAction(e -> root.getChildren().remove(overlay));

        StackPane popup = new StackPane(container, backBox);
        popup.setPadding(new Insets(20));

        overlay.getChildren().add(popup);
        StackPane.setAlignment(popup, Pos.CENTER);

        overlay.getChildren().add(text);
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

    private static Button createBackButton() {
        ImageView iv = new ImageView(
                new Image(HowPop.class.getResource("/Back.png").toExternalForm())
        );
        iv.setPreserveRatio(true);
        iv.setFitWidth(450);

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
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }
}