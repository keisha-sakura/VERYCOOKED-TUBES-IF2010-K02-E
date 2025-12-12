import javafx.application.Application;
import javafx.stage.Stage;
import view.LandingPage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("VERYCOOKED - Pizza Edition");

        LandingPage landingPage = new LandingPage(primaryStage);

        primaryStage.setScene(landingPage.createScene(1280, 720));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
