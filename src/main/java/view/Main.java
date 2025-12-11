package main.java.view;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX Berhasil Jalan!");
        Button button = new Button("Klik Aku");

        button.setOnAction(e -> label.setText("Tombol diklik!"));

        VBox root = new VBox(10);
        root.getChildren().addAll(label, button);

        Scene scene = new Scene(root, 300, 200);

        stage.setTitle("Test JavaFX");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
