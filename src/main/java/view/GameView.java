// File: view/GameView.java
package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.chef.Chef;
import model.enums.Direction;
import model.enums.GameStatus;
import model.map.Map;
import model.order.Order;
import model.position.Position;

public class GameView {
    private Stage stage;
    private GameController controller;
    private Canvas canvas;
    private GraphicsContext gc;
    private AnimationTimer gameLoop;

    // UI components untuk update (BUKAN data!)
    private Text scoreText;
    private Text timeText;
    private VBox ordersBox;

    private static final int TILE_SIZE = 40;
    private static final int GAME_DURATION = 180;

    // Color mapping untuk tile symbols (static, tidak berubah)
    private static final java.util.Map<Character, Color> TILE_COLORS = java.util.Map.ofEntries(
            java.util.Map.entry('X', Color.GRAY),
            java.util.Map.entry('.', Color.web("#2c3e50")),
            java.util.Map.entry('C', Color.ORANGE),
            java.util.Map.entry('R', Color.RED),
            java.util.Map.entry('A', Color.LIGHTBLUE),
            java.util.Map.entry('S', Color.GOLD),
            java.util.Map.entry('W', Color.CYAN),
            java.util.Map.entry('I', Color.GREEN),
            java.util.Map.entry('P', Color.LIGHTGRAY),
            java.util.Map.entry('T', Color.BROWN)
    );

    public GameView(Stage stage) {
        this.stage = stage;
        this.controller = new GameController(GAME_DURATION);
    }

    public Scene createScene(int width, int height) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #34495e;");

        // Canvas - ukuran dari model.map
        Map gameMap = controller.getGameMap();
        canvas = new Canvas(
                gameMap.getWidth() * TILE_SIZE,
                gameMap.getHeight() * TILE_SIZE
        );
        gc = canvas.getGraphicsContext2D();

        // Info panel
        VBox infoPanel = createInfoPanel();

        root.setCenter(canvas);
        root.setRight(infoPanel);

        Scene scene = new Scene(root, width, height);

        // Input handler
        setupInputHandler(scene);

        // Start game
        controller.startGame();
        startGameLoop();

        return scene;
    }

    /**
     * Setup input handler - HANYA handle input, TIDAK store state!
     */
    private void setupInputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            // LANGSUNG delegate ke controller, JANGAN simpan state di view
            switch (code) {
                case W, UP -> {
                    controller.moveChef(Direction.UP);
                    System.out.println("↑ Move Up");
                }
                case A, LEFT -> {
                    controller.moveChef(Direction.LEFT);
                    System.out.println("← Move Left");
                }
                case S, DOWN -> {
                    controller.moveChef(Direction.DOWN);
                    System.out.println("↓ Move Down");
                }
                case D, RIGHT -> {
                    controller.moveChef(Direction.RIGHT);
                    System.out.println("→ Move Right");
                }
                case C -> {
                    controller.pickupDrop();
                    System.out.println("✋ Pickup/Drop");
                }
                case V -> {
                    controller.interact();
                    System.out.println("⚡ Interact");
                }
                case B -> controller.switchChef();
                case Q, ESCAPE -> {
                    stopGameLoop();
                    backToMenu();
                }
            }
        });
    }

    /**
     * Game loop - PULL data dari model setiap frame
     */
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Update setiap ~16ms (60 FPS) atau sesuai kebutuhan
                if (now - lastUpdate >= 16_000_000) {
                    renderGame();
                    updateInfoPanel();
                    controller.checkGameOver();
                    lastUpdate = now;
                }

                // Check game over
                if (controller.getGameStatus() != GameStatus.PLAYING) {
                    stop();
                    showGameOver();
                }
            }
        };
        gameLoop.start();
    }

    private void stopGameLoop() {
        if (gameLoop != null) {
            gameLoop.stop();
        }
    }

    /**
     * Render game - PULL dari model, NO caching!
     */
    private void renderGame() {
        // Clear canvas
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // PULL map grid dari model (via controller)
        Map gameMap = controller.getGameMap();
        char[][] grid = gameMap.getGrid();

        // Render tiles - data LANGSUNG dari model
        int height = gameMap.getHeight();
        int width = gameMap.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                renderTile(x, y, grid[y][x]);
            }
        }

        // PULL chef list dari controller
        for (Chef chef : controller.getAllChefs()) {
            renderChef(chef);
        }
    }

    /**
     * Render single tile - hanya rendering logic
     */
    private void renderTile(int x, int y, char symbol) {
        double px = x * TILE_SIZE;
        double py = y * TILE_SIZE;

        // Get color dari static mapping (bukan dari model)
        Color color = TILE_COLORS.getOrDefault(symbol, Color.DARKGRAY);

        // Draw tile
        gc.setFill(color);
        gc.fillRect(px, py, TILE_SIZE, TILE_SIZE);

        // Draw border
        gc.setStroke(Color.web("#1a1a1a"));
        gc.setLineWidth(1);
        gc.strokeRect(px, py, TILE_SIZE, TILE_SIZE);

        // Draw symbol
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Monospaced", 14));
        gc.fillText(String.valueOf(symbol), px + 13, py + 24);
    }

    /**
     * Render chef - PULL position dari chef object
     */
    private void renderChef(Chef chef) {
        // PULL position dari model (chef object)
        Position pos = chef.getPosition();

        // Safety check (avoid crash)
        if (pos == null) {
            System.err.println("WARNING: Chef position is null!");
            return;
        }

        double px = pos.getX() * TILE_SIZE;
        double py = pos.getY() * TILE_SIZE;

        // Draw chef circle
        if (chef.isActive()) {
            gc.setFill(Color.YELLOW);
        } else {
            gc.setFill(Color.LIGHTGRAY);
        }
        gc.fillOval(px + 5, py + 5, TILE_SIZE - 10, TILE_SIZE - 10);

        // Draw chef symbol
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Monospaced", 24));
        String symbol = chef.isActive() ? "@" : "*";
        gc.fillText(symbol, px + 11, py + 29);

        // Draw inventory indicator (jika ada item)
        if (chef.getInventory() != null) {
            gc.setFill(Color.LIME);
            gc.fillOval(px + 28, py + 2, 8, 8);
        }
    }

    /**
     * Info panel - HANYA UI components, data di-update di updateInfoPanel()
     */
    private VBox createInfoPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(20));
        panel.setStyle("-fx-background-color: #2c3e50;");
        panel.setPrefWidth(320);

        // Title
        Text title = new Text("GAME INFO");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 20));

        // Score text (REFERENCE saja, data diupdate di updateInfoPanel)
        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.GOLD);
        scoreText.setFont(Font.font("Arial", 16));

        // Time text
        timeText = new Text("Time: 03:00");
        timeText.setFill(Color.LIGHTBLUE);
        timeText.setFont(Font.font("Arial", 16));

        // Orders title
        Text ordersTitle = new Text("\nACTIVE ORDERS:");
        ordersTitle.setFill(Color.WHITE);
        ordersTitle.setFont(Font.font("Arial", 14));

        // Orders box (akan diisi di updateInfoPanel)
        ordersBox = new VBox(5);
        ordersBox.setStyle("-fx-padding: 5;");

        // Chef info
        Text chefTitle = new Text("\nCHEFS:");
        chefTitle.setFill(Color.WHITE);
        chefTitle.setFont(Font.font("Arial", 14));

        VBox chefBox = new VBox(5);
        chefBox.setStyle("-fx-padding: 5;");

        // Update chef info setiap frame
        final VBox finalChefBox = chefBox;
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                // PULL dari controller
                finalChefBox.getChildren().clear();
                for (Chef chef : controller.getAllChefs()) {
                    String status = chef.isActive() ? "[@]" : "[*]";
                    String inventory = chef.getInventory() != null ?
                            " | " + chef.getInventory().getName() : " | Empty";

                    Text chefText = new Text(
                            status + " " + chef.getName() +
                                    " (" + chef.getPosition().getX() + "," + chef.getPosition().getY() + ")" +
                                    inventory
                    );
                    chefText.setFill(chef.isActive() ? Color.YELLOW : Color.LIGHTGRAY);
                    chefText.setFont(Font.font("Monospaced", 11));
                    finalChefBox.getChildren().add(chefText);
                }
            }
        }.start();

        // Controls
        Text controlsTitle = new Text("\nCONTROLS:");
        controlsTitle.setFill(Color.WHITE);
        controlsTitle.setFont(Font.font("Arial", 14));

        Text controls = new Text(
                "WASD/Arrows - Move\n" +
                        "C - Pickup/Drop\n" +
                        "V - Interact\n" +
                        "B - Switch Chef\n" +
                        "Q/ESC - Quit"
        );
        controls.setFill(Color.LIGHTGRAY);
        controls.setFont(Font.font("Arial", 12));

        panel.getChildren().addAll(
                title, scoreText, timeText,
                ordersTitle, ordersBox,
                chefTitle, chefBox,
                controlsTitle, controls
        );

        return panel;
    }

    /**
     * Update info panel - PULL data dari controller
     */
    private void updateInfoPanel() {
        // PULL score dari controller
        scoreText.setText("Score: " + controller.getScore());

        // PULL remaining time dari controller
        int time = controller.getRemainingTime();
        timeText.setText(String.format("Time: %02d:%02d", time / 60, time % 60));

        // PULL orders dari controller
        ordersBox.getChildren().clear();
        for (Order order : controller.getOrderManager().getActiveOrders()) {
            String orderInfo = String.format(
                    "[%d] %s - %ds",
                    order.getPosition(),
                    order.getRecipe().getName(),
                    order.getTimeRemaining()
            );

            Text orderText = new Text(orderInfo);
            orderText.setFill(Color.WHITE);
            orderText.setFont(Font.font("Monospaced", 12));
            ordersBox.getChildren().add(orderText);
        }
    }

    private void showGameOver() {
        // PULL final score dari controller
        int finalScore = controller.getScore();
        GameStatus status = controller.getGameStatus();

        System.out.println("=== GAME OVER ===");
        System.out.println("Final Score: " + finalScore);
        System.out.println("Status: " + status);

        // TODO: Bisa tambahkan dialog JavaFX
    }

    private void backToMenu() {
        LandingPage landingPage = new LandingPage(stage);
        Scene menuScene = landingPage.createScene(1280, 720);
        stage.setScene(menuScene);
    }
}
