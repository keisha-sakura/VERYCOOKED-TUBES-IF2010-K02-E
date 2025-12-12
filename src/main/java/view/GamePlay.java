package view;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class GamePlay {

    private final Stage stage;
    private final MapPage mapPage;

    public GamePlay(Stage stage) {
        this.stage = stage;
        this.mapPage = new MapPage();
    }

    public void show() {
        GridPane mapGrid = mapPage.createGrid(64);
        StackPane root = new StackPane(mapGrid);
        Scene scene = new Scene(root, 896, 640);

        stage.setScene(scene);
        stage.show();
    }
}
