// File: view/GamePlay.java
package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.chef.Chef;
import model.enums.Direction;
import model.enums.GameStatus;
import model.map.Map;
import model.map.Tile;
import model.map.TileState;
import model.order.Order;
import model.position.Position;

import java.util.HashMap;

public class GamePlay {
    private Stage stage;
    private GameController controller;
    private GridPane mapGrid;
    private Pane chefLayer;
    private AnimationTimer gameLoop;

    // UI components
    private Text scoreText;
    private Text timeText;
    private VBox ordersBox;
    private VBox chefsBox;

    // Tile images
    private Image[] tileImages = new Image[14];

    // Chef sprites
    private Image pikachuSprite;
    private Image jigglypuffSprite;

    // Chef ImageViews
    private java.util.Map<String, ImageView> chefViews = new HashMap<>();
    private java.util.Map<String, Integer> chefAnimFrames = new HashMap<>();

    private static final double TILE_SIZE = 60;
    private static final int SPRITE_WIDTH = 60;
    private static final int SPRITE_HEIGHT = 60;

    /**
     * Constructor
     */
    public GamePlay(Stage stage, String difficulty) {
        this.stage = stage;

        // Set duration based on difficulty
        int duration = switch (difficulty.toUpperCase()) {
            case "EASY" -> 240;   // 4 minutes
            case "MEDIUM" -> 180; // 3 minutes
            case "HARD" -> 120;   // 2 minutes
            default -> 180;
        };

        this.controller = new GameController(duration);
        loadTileImages();
        loadChefSprites();
    }

    private void loadTileImages() {
        try {
            tileImages[0]  = new Image(getClass().getResource("/map/tile-floor.png").toExternalForm());
            tileImages[1]  = new Image(getClass().getResource("/map/tile-wall.png").toExternalForm());
            tileImages[2]  = new Image(getClass().getResource("/map/station-assembly.png").toExternalForm());
            tileImages[3]  = new Image(getClass().getResource("/map/station-cutting.png").toExternalForm());
            tileImages[4]  = new Image(getClass().getResource("/map/station-cooking.png").toExternalForm());
            tileImages[5]  = new Image(getClass().getResource("/map/station-ingredient-cheese.png").toExternalForm());
            tileImages[6]  = new Image(getClass().getResource("/map/station-ingredient-chicken.png").toExternalForm());
            tileImages[7]  = new Image(getClass().getResource("/map/station-ingredient-dough.png").toExternalForm());
            tileImages[8]  = new Image(getClass().getResource("/map/station-ingredient-sausage.png").toExternalForm());
            tileImages[9]  = new Image(getClass().getResource("/map/station-ingredient-tomato.png").toExternalForm());
            tileImages[10] = new Image(getClass().getResource("/map/station-plate.png").toExternalForm());
            tileImages[11] = new Image(getClass().getResource("/map/station-serve.png").toExternalForm());
            tileImages[12] = new Image(getClass().getResource("/map/station-trash 1.png").toExternalForm());
            tileImages[13] = new Image(getClass().getResource("/map/station-washing.png").toExternalForm());

            System.out.println("✓ Tile images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading tile images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Load chef sprites
     */
    private void loadChefSprites() {
        try {
            // TODO: Ganti dengan spritesheet gabungan
            // Sementara pakai sprite individual (front-1 aja)
            pikachuSprite = new Image(getClass().getResource("/chefPikachu/pikachu-front-1.png").toExternalForm());
            jigglypuffSprite = new Image(getClass().getResource("/chefJig/jigglypuff-front-1.png").toExternalForm());

            System.out.println("✓ Chef sprites loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading chef sprites: " + e.getMessage());
            System.err.println("Make sure sprite files are in src/main/resources/chefs/");
        }
    }

    /**
     * Show gameplay screen (MAIN ENTRY POINT)
     */
    public void show() {
        System.out.println("GamePlay.show() called");

        // Root layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        // TOP: Header
        HBox header = createHeader();
        root.setTop(header);

        // CENTER: Map + Chefs
        StackPane gameArea = createGameArea();
        root.setCenter(gameArea);

        // RIGHT: Info Panel
        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);

        // Create scene
        Scene scene = new Scene(root, 1280, 720);

        // Setup input
        setupInputHandler(scene);

        // Start game
        controller.startGame();

        // Initial render
        renderMap();
        renderChefs();

        // Start game loop
        startGameLoop();

        // Show stage
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Create header (Score & Timer)
     */
    private HBox createHeader() {
        HBox header = new HBox(30);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #16213e;");

        Text title = new Text("VERYCOOKED - PIZZA MAP");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 24));

        scoreText = new Text("Score: 0");
        scoreText.setFill(Color.GOLD);
        scoreText.setFont(Font.font("Arial", 20));

        timeText = new Text("Time: 03:00");
        timeText.setFill(Color.LIGHTBLUE);
        timeText.setFont(Font.font("Arial", 20));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(title, spacer, scoreText, timeText);

        return header;
    }

    /**
     * Create game area (Map + Chefs)
     */
    private StackPane createGameArea() {
        StackPane gameArea = new StackPane();
        gameArea.setStyle("-fx-background-color: #0f3460;");
        gameArea.setPadding(new Insets(20));

        // Map grid (bottom layer)
        mapGrid = new GridPane();
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);

        // Chef layer (top layer)
        chefLayer = new Pane();

        gameArea.getChildren().addAll(mapGrid, chefLayer);

        return gameArea;
    }

    /**
     * Create right panel (Orders & Chefs info)
     */
    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(320);
        panel.setStyle("-fx-background-color: #16213e;");

        // Orders section
        Text ordersTitle = new Text("ACTIVE ORDERS");
        ordersTitle.setFill(Color.WHITE);
        ordersTitle.setFont(Font.font("Arial", 18));

        ordersBox = new VBox(8);
        ordersBox.setPadding(new Insets(10));
        ordersBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        // Chefs section
        Text chefsTitle = new Text("CHEFS");
        chefsTitle.setFill(Color.WHITE);
        chefsTitle.setFont(Font.font("Arial", 18));

        chefsBox = new VBox(8);
        chefsBox.setPadding(new Insets(10));
        chefsBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        // Controls section
        Text controlsTitle = new Text("CONTROLS");
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
        controls.setFont(Font.font("Monospaced", 12));

        VBox controlsBox = new VBox(5, controlsTitle, controls);
        controlsBox.setPadding(new Insets(10));
        controlsBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        panel.getChildren().addAll(
                ordersTitle, ordersBox,
                chefsTitle, chefsBox,
                controlsBox
        );

        return panel;
    }

    /**
     * Render map - PULL dari model
     */
    private void renderMap() {
        System.out.println("\n=== RENDER MAP DEBUG ===");

        mapGrid.getChildren().clear();

        Map gameMap = controller.getGameMap();

        if (gameMap == null) {
            System.err.println("❌ ERROR: gameMap is NULL!");
            return;
        }

        int width = gameMap.getWidth();
        int height = gameMap.getHeight();

        System.out.println("Map size: " + width + "x" + height);
        System.out.println("TILE_SIZE: " + TILE_SIZE);

        int successCount = 0;
        int nullTileCount = 0;
        int nullImageCount = 0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = gameMap.getTile(x, y);

                if (tile == null) {
                    System.err.println("❌ Tile at (" + x + "," + y + ") is NULL!");
                    nullTileCount++;
                    continue;
                }

                int imageId = getTileImageId(tile, x, y);
                Image img = tileImages[imageId];

                if (img == null) {
                    System.err.println("❌ Image[" + imageId + "] at (" + x + "," + y + ") is NULL! State: " + tile.getState());
                    nullImageCount++;
                    continue;
                }

                ImageView iv = new ImageView(img);
                iv.setFitWidth(TILE_SIZE);
                iv.setFitHeight(TILE_SIZE);

                mapGrid.add(iv, x, y);
                successCount++;

                // Debug first tile
                if (x == 0 && y == 0) {
                    System.out.println("First tile (0,0):");
                    System.out.println("  State: " + tile.getState());
                    System.out.println("  ImageID: " + imageId);
                    System.out.println("  Image size: " + img.getWidth() + "x" + img.getHeight());
                    System.out.println("  ImageView bounds: " + iv.getBoundsInParent());
                }
            }
        }

        System.out.println("\n=== RENDER SUMMARY ===");
        System.out.println("✓ Success: " + successCount + " tiles");
        System.out.println("❌ NULL tiles: " + nullTileCount);
        System.out.println("❌ NULL images: " + nullImageCount);
        System.out.println("MapGrid children: " + mapGrid.getChildren().size());
        System.out.println("MapGrid bounds: " + mapGrid.getBoundsInParent());
        System.out.println("======================\n");
    }


    /**
     * Get tile image ID
     */
    private int getTileImageId(Tile tile, int x, int y) {
        TileState state = tile.getState();

        // Ingredient storage: berbeda per posisi
        if (state == TileState.INGREDIENT_STORAGE) {
            if (y == 2 && x == 3) return 7;  // Dough
            if (y == 4) {
                if (x == 4) return 9;        // Tomato
                if (x == 6) return 5;        // Cheese
                if (x == 8) return 8;        // Sausage
                if (x == 10) return 6;       // Chicken
            }
            if (y == 9 && x == 6) return 7;  // Dough
            return 7; // Default: Dough
        }

        return switch (state) {
            case WALL -> 1;
            case ASSEMBLY_STATION -> 2;
            case CUTTING_STATION -> 3;
            case COOKING_STATION -> 4;
            case PLATE_STORAGE -> 10;
            case SERVING_COUNTER -> 11;
            case TRASH_STATION -> 12;
            case WASHING_STATION -> 13;
            default -> 0; // Floor
        };
    }

    /**
     * Render chefs
     */
    private void renderChefs() {
        chefLayer.getChildren().clear();
        chefViews.clear();

        int chefIndex = 0;
        for (Chef chef : controller.getAllChefs()) {
            if (chef == null || chef.getPosition() == null) {
                System.err.println("WARNING: Chef or position is null!");
                continue;
            }

            Position pos = chef.getPosition();

            // Pilih sprite
            Image sprite = (chefIndex == 0) ? pikachuSprite : jigglypuffSprite;

            ImageView chefView = new ImageView(sprite);
            chefView.setFitWidth(80);
            chefView.setFitHeight(TILE_SIZE);
            chefView.setPreserveRatio(true);

            // Set position
            chefView.setLayoutX(pos.getX() * TILE_SIZE);
            chefView.setLayoutY(pos.getY() * TILE_SIZE);

            // Glow effect for active chef
            if (chef.isActive()) {
                chefView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 15, 0.8, 0, 0);");
            }

            chefLayer.getChildren().add(chefView);
            chefViews.put(chef.getId(), chefView);
            chefAnimFrames.put(chef.getId(), 0);

            chefIndex++;
        }
    }

    /**
     * Update chef positions & animation
     */
    private void updateChefPositions() {
        for (Chef chef : controller.getAllChefs()) {
            if (chef == null || chef.getPosition() == null) continue;

            ImageView chefView = chefViews.get(chef.getId());
            if (chefView != null) {
                Position pos = chef.getPosition();

                // Update position
                chefView.setLayoutX(pos.getX() * TILE_SIZE);
                chefView.setLayoutY(pos.getY() * TILE_SIZE);

                // Update glow
                if (chef.isActive()) {
                    chefView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 15, 0.8, 0, 0);");
                } else {
                    chefView.setStyle("");
                }
            }
        }
    }

    /**
     * Setup input handler
     */
    private void setupInputHandler(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

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
                case B -> {
                    controller.switchChef();
                    System.out.println("🔄 Switch Chef");
                }
                case Q, ESCAPE -> {
                    stopGameLoop();
                    backToMenu();
                }
            }

            // Update immediately
            updateChefPositions();
            updateInfoPanel();
        });
    }

    /**
     * Start game loop
     */
    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                // Update every 500ms
                if (now - lastUpdate >= 500_000_000) {
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
     * Update info panel - PULL dari controller
     */
    private void updateInfoPanel() {
        // Score
        scoreText.setText("Score: " + controller.getScore());

        // Time
        int time = controller.getRemainingTime();
        timeText.setText(String.format("Time: %02d:%02d", time / 60, time % 60));

        // Orders
        ordersBox.getChildren().clear();
        for (Order order : controller.getOrderManager().getActiveOrders()) {
            HBox orderRow = new HBox(10);
            orderRow.setAlignment(Pos.CENTER_LEFT);
            orderRow.setPadding(new Insets(5));
            orderRow.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 3;");

            Text orderNum = new Text("[" + order.getPosition() + "]");
            orderNum.setFill(Color.ORANGE);
            orderNum.setFont(Font.font("Monospaced", 14));

            Text orderName = new Text(order.getRecipe().getName());
            orderName.setFill(Color.WHITE);
            orderName.setFont(Font.font("Arial", 13));

            Text orderTime = new Text(order.getTimeRemaining() + "s");
            orderTime.setFill(order.getTimeRemaining() < 30 ? Color.RED : Color.LIGHTGREEN);
            orderTime.setFont(Font.font("Monospaced", 13));

            orderRow.getChildren().addAll(orderNum, orderName, orderTime);
            ordersBox.getChildren().add(orderRow);
        }

        // Chefs
        chefsBox.getChildren().clear();
        for (Chef chef : controller.getAllChefs()) {
            HBox chefRow = new HBox(8);
            chefRow.setAlignment(Pos.CENTER_LEFT);
            chefRow.setPadding(new Insets(5));
            chefRow.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 3;");

            Text status = new Text(chef.isActive() ? "[@]" : "[*]");
            status.setFill(chef.isActive() ? Color.YELLOW : Color.GRAY);
            status.setFont(Font.font("Monospaced", 14));

            Text name = new Text(chef.getName());
            name.setFill(Color.WHITE);
            name.setFont(Font.font("Arial", 12));

            String invText = chef.getInventory() != null ?
                    chef.getInventory().getName() : "Empty";
            Text inventory = new Text("| " + invText);
            inventory.setFill(Color.LIGHTGRAY);
            inventory.setFont(Font.font("Monospaced", 11));

            chefRow.getChildren().addAll(status, name, inventory);
            chefsBox.getChildren().add(chefRow);
        }
    }

    private void showGameOver() {
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Score: " + controller.getScore());
        System.out.println("Status: " + controller.getGameStatus());
    }

    private void backToMenu() {
//        LandingPage landingPage = new LandingPage(stage);
////        Scene scene = landingPage.createScene(width, height);
//        stage.setScene(scene);
    }
}