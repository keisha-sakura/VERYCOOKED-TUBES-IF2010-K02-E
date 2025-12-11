package view;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        LandingPage landingPage = new LandingPage(stage);
        Scene scene = landingPage.createScene(1280, 720);
        stage.setScene(scene);
        stage.setTitle("Verycooked Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}