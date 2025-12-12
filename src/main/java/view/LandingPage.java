package view;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LandingPage {
    private final Stage stage;

    public LandingPage(Stage stage) {
        this.stage = stage;
    }

    public Scene createScene(double width, double height) {
        StackPane root = new StackPane();

        // background
        String bgUrl = getClass().getResource("/bg.png").toExternalForm();
        root.setStyle(
                "-fx-background-image: url('" + bgUrl + "');" +
                        "-fx-background-size: cover;" +
                        "-fx-background-repeat: no-repeat;" +
                        "-fx-background-position: center;"
        );

        ImageView headerView = new ImageView(
                new Image(getClass().getResource("/VERYCOOKED.png").toExternalForm())
        );
        headerView.setPreserveRatio(true);
        headerView.setFitWidth(800);

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

        addFloatingAnimation(cloud1);
        addFloatingAnimation1(cloud2);
        addFloatingAnimation(cloud3);
        addFloatingAnimation1(cloud4);

        ImageView pikachu = new ImageView(
                new Image(getClass().getResource("/pikachu.png").toExternalForm())
        );
        pikachu.setPreserveRatio(true);
        pikachu.setFitWidth(90);

        ImageView jigglypuf = new ImageView(
                new Image(getClass().getResource("/jigglypuf.png").toExternalForm())
        );
        jigglypuf.setPreserveRatio(true);
        jigglypuf.setFitWidth(90);

        StackPane headerLayer = new StackPane();
        headerLayer.getChildren().addAll(headerView, pikachu, jigglypuf);

        StackPane.setAlignment(headerView, Pos.CENTER);

        StackPane.setAlignment(pikachu, Pos.TOP_LEFT);
        StackPane.setMargin(pikachu, new Insets(60, 0, 0, 160));

        StackPane.setAlignment(jigglypuf, Pos.TOP_RIGHT);
        StackPane.setMargin(jigglypuf, new Insets(-45, 160, 0, 0));

        Button startBtn = createImageButton("start.png");
        Button tutorialBtn = createImageButton("tutorial.png");
        Button exitBtn = createImageButton("exit.png");

        startBtn.setCursor(javafx.scene.Cursor.HAND);
        tutorialBtn.setCursor(javafx.scene.Cursor.HAND);
        exitBtn.setCursor(javafx.scene.Cursor.HAND);

        addHoverEffect(startBtn);
        addHoverEffect(tutorialBtn);
        addHoverEffect(exitBtn);

        startBtn.setOnAction(e -> {
            ChooseLevel chooseLevel = new ChooseLevel(stage);
            stage.setScene(chooseLevel.createScene(1280, 720));
        });

        tutorialBtn.setOnAction(e -> HowPop.show(root));
        exitBtn.setOnAction(e -> ExitPop.show(root));

        VBox buttonBox = new VBox(20, startBtn, tutorialBtn, exitBtn);
        buttonBox.setAlignment(Pos.CENTER);

        VBox mainBox = new VBox(70, headerLayer, buttonBox);
        mainBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(cloudLayer, mainBox);
        StackPane.setAlignment(cloudLayer, Pos.TOP_LEFT);

        return new Scene(root, width, height);
    }

    private Button createImageButton(String imageFileName) {
        ImageView iv = new ImageView(
                new Image(getClass().getResource("/"+imageFileName).toExternalForm())
        );
        iv.setPreserveRatio(true);
        iv.setFitWidth(600);

        Button button = new Button();
        button.setGraphic(iv);
        button.setBackground(Background.EMPTY);
        button.setBorder(Border.EMPTY);

        return button;
    }

    private void addFloatingAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.seconds(3), node);
        tt.setByX(-15);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();
    }

    private void addFloatingAnimation1(Node node) {
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
}
