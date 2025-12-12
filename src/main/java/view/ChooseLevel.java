package view;

import javafx.animation.Interpolator;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.geometry.*;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;
import javafx.scene.Node;

public class ChooseLevel {

    private final Stage stage;

    public ChooseLevel(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene(double width, double height) {
        BorderPane root = new BorderPane();

        String bgUrl = getClass().getResource("/bg.png").toExternalForm();
        root.setStyle(
                "-fx-background-image: url('" + bgUrl + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-position: center;"
        );

        ImageView header = new ImageView(
                new Image(getClass().getResource("/ChooseLevel.png").toExternalForm())
        );
        header.setPreserveRatio(true);
        header.setFitWidth(900);

        //cloud
        ImageView cloud1 = new ImageView(
                new Image(getClass().getResource("/cloud1.png").toExternalForm())
        );
        cloud1.setPreserveRatio(true);
        cloud1.setFitWidth(200);

        ImageView cloud2 = new ImageView(
                new Image(getClass().getResource("/cloud2.png").toExternalForm())
        );
        cloud2.setPreserveRatio(true);
        cloud2.setFitWidth(200);

        ImageView cloud3 = new ImageView(
                new Image(getClass().getResource("/cloud3.png").toExternalForm())
        );
        cloud3.setPreserveRatio(true);
        cloud3.setFitWidth(200);

        ImageView cloud4 = new ImageView(
                new Image(getClass().getResource("/cloud4.png").toExternalForm())
        );
        cloud4.setPreserveRatio(true);
        cloud4.setFitWidth(200);

        Pane cloudLayer = new Pane();
        cloudLayer.getChildren().addAll(cloud1, cloud2, cloud3, cloud4);

        cloud1.setLayoutX(955);
        cloud1.setLayoutY(225);

        cloud2.setLayoutX(400);
        cloud2.setLayoutY(620);

        cloud3.setLayoutX(100);
        cloud3.setLayoutY(440);

        cloud4.setLayoutX(725);
        cloud4.setLayoutY(130);

        addFloatingAnimation1(cloud1);
        addFloatingAnimation2(cloud2);
        addFloatingAnimation1(cloud3);
        addFloatingAnimation2(cloud4);

        Button level1Btn = createImageButton("Medium.png");
        Button level2Btn = createImageButton("Easy.png");
        Button level3Btn = createImageButton("Hard.png");

        VBox level1Box = new VBox(level1Btn);
        VBox level2Box = new VBox(level2Btn);
        VBox level3Box = new VBox(level3Btn);

        level1Box.setAlignment(Pos.TOP_CENTER);
        level2Box.setAlignment(Pos.TOP_CENTER);
        level3Box.setAlignment(Pos.TOP_CENTER);

        addFloatingAnimation(level1Box);
        addFloatingAnimation(level2Box);
        addFloatingAnimation(level3Box);

        level1Box.setCursor(javafx.scene.Cursor.HAND);
        level2Box.setCursor(javafx.scene.Cursor.HAND);
        level3Box.setCursor(javafx.scene.Cursor.HAND);

        addHoverEffect(level1Btn);
        addHoverEffect(level2Btn);
        addHoverEffect(level3Btn);

        level1Box.setPadding(new Insets(40, 0, 0, 0));
        level3Box.setPadding(new Insets(40, 0, 0, 0));

        Button back = createBackButton();

        back.setCursor(javafx.scene.Cursor.HAND);
        addHoverEffect(back);

        level1Btn.setOnAction(e -> System.out.println("Level Medium masih terkunci!"));
        level3Btn.setOnAction(e -> System.out.println("Level Hard masih terkunci!"));

        back.setOnAction(e -> {
            LandingPage landingPage = new LandingPage(stage);
            Scene scene = landingPage.createScene(width, height);
            stage.setScene(scene);
        });

        level2Btn.setOnAction(e -> startGame("Easy"));

        root.getChildren().addAll(cloudLayer);
        StackPane.setAlignment(cloudLayer, Pos.TOP_LEFT);

        HBox levelBox = new HBox(40, level1Box, level2Box, level3Box);
        levelBox.setAlignment(Pos.CENTER);
        BorderPane.setMargin(levelBox, new Insets(50, 0, 0, 0));
        root.setCenter(levelBox);

        HBox backBox = new HBox(back);
        backBox.setAlignment(Pos.CENTER);
        backBox.setPadding(new Insets(0, 0, 50, 0));
        root.setBottom(backBox);

        HBox headerBox = new HBox(header);
        headerBox.setAlignment(Pos.TOP_CENTER);
        headerBox.setPadding(new Insets(50, 0, 0, 0));
        root.setTop(headerBox);

        return new Scene(root, width, height);
    }

    private Button createImageButton(String imageFileName) {
        ImageView iv = new ImageView(
                new Image(getClass().getResource("/"+imageFileName).toExternalForm())
        );
        iv.setPreserveRatio(true);
        iv.setFitWidth(280);

        Button button = new Button();
        button.setGraphic(iv);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);

        return button;
    }

    private Button createBackButton() {
        ImageView iv = new ImageView(
                new Image(getClass().getResource("/Back.png").toExternalForm())
        );
        iv.setPreserveRatio(true);
        iv.setFitWidth(450);

        Button button = new Button();
        button.setGraphic(iv);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);

        return button;
    }

    private void addFloatingAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), node);
        tt.setFromY(0);
        tt.setToY(-13);
        tt.setToX(-2);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.play();
    }

    private void addFloatingAnimation1(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(3), node);
        tt.setByX(-15);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void addFloatingAnimation2(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(1.8), node);
        tt.setByX(5);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void addHoverEffect(Button button) {
        button.setOnMouseEntered(e -> {
            button.setScaleX(1.05);
            button.setScaleY(1.05);
        });

        button.setOnMouseExited(e -> {
            button.setScaleX(1.0);
            button.setScaleY(1.0);
        });
    }

    private void startGame(String difficulty) {
        System.out.println("Starting game with difficulty: " + difficulty);

        GamePlay gamePlay = new GamePlay(stage, difficulty);

        gamePlay.show();
    }
}