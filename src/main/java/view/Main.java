// File: view/Main.java (atau tetap GameRenderer.java)
package view;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        LandingPage landingPage = new LandingPage(stage);
        stage.setScene(landingPage.createScene(1280, 720));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
