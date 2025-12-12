// File: view/GamePlay.java
package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import model.item.Item;
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

    // Item images
    private java.util.Map<String, Image> itemImages = new HashMap<>();

    // Chef ImageViews
    private java.util.Map<String, ImageView> chefViews = new HashMap<>();
    private java.util.Map<String, Integer> chefAnimFrames = new HashMap<>();

    private static final double TILE_SIZE = 60;
    private static final int SPRITE_WIDTH = 60;
    private static final int SPRITE_HEIGHT = 60;

    public GamePlay(Stage stage, String difficulty) {
        this.stage = stage;

        int duration = switch (difficulty.toUpperCase()) {
            case "EASY" -> 240;
            case "MEDIUM" -> 180;
            case "HARD" -> 120;
            default -> 180;
        };

        this.controller = new GameController(duration);
        loadTileImages();
        loadChefSprites();
        loadItemImages();  // Load item images
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

    private void loadChefSprites() {
        try {
            pikachuSprite = new Image(getClass().getResource("/chefPikachu/pikachu-front-1.png").toExternalForm());
            jigglypuffSprite = new Image(getClass().getResource("/chefJig/jigglypuff-front-1.png").toExternalForm());

            System.out.println("✓ Chef sprites loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading chef sprites: " + e.getMessage());
        }
    }

    /**
     * Load item images
     */
    private void loadItemImages() {
        System.out.println("Loading item images...");

        try {
            // Load dari folder /item/ sesuai nama file yang ada
            itemImages.put("Plate", new Image(getClass().getResource("/item/plate.png").toExternalForm()));
            itemImages.put("Dirty Plate", new Image(getClass().getResource("/item/plate-dirty.png").toExternalForm()));
            itemImages.put("Adonan", new Image(getClass().getResource("/item/dough-raw-export.png").toExternalForm()));
            itemImages.put("Tomat", new Image(getClass().getResource("/item/tomato-raw.png").toExternalForm()));
            itemImages.put("Keju", new Image(getClass().getResource("/item/cheese-raw.png").toExternalForm()));
            itemImages.put("Sosis", new Image(getClass().getResource("/item/sausage-raw.png").toExternalForm()));
            itemImages.put("Ayam", new Image(getClass().getResource("/item/chicken-raw.png").toExternalForm()));

            System.out.println("✓ Item images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading item images: " + e.getMessage());
            // Create placeholders if files not found
            createItemPlaceholders();
        }
    }

    /**
     * Create placeholder untuk item yang belum ada imagenya
     */
    private void createItemPlaceholders() {
        System.out.println("Creating item placeholders...");

        String[] itemNames = {
                "Plate", "Dirty Plate", "Dough", "Tomato", "Cheese",
                "Sausage", "Chicken"
        };

        for (String name : itemNames) {
            if (!itemImages.containsKey(name)) {
                itemImages.put(name, createItemPlaceholder(name));
            }
        }
    }

    private Image createItemPlaceholder(String itemName) {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(32, 32);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        Color color = switch(itemName) {
            case "Plate" -> Color.WHITE;
            case "Dirty Plate" -> Color.GRAY;
            case "Dough" -> Color.WHEAT;
            case "Tomato" -> Color.RED;
            case "Cheese" -> Color.YELLOW;
            case "Sausage" -> Color.PINK;
            case "Chicken" -> Color.LIGHTYELLOW;
            default -> Color.ORANGE;
        };

        gc.setFill(color);
        gc.fillOval(4, 4, 24, 24);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(4, 4, 24, 24);

        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    public void show() {
        System.out.println("GamePlay.show() called");

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        HBox header = createHeader();
        root.setTop(header);

        StackPane gameArea = createGameArea();
        root.setCenter(gameArea);

        VBox rightPanel = createRightPanel();
        root.setRight(rightPanel);

        Scene scene = new Scene(root, 1280, 720);

        setupInputHandler(scene);
        controller.startGame();

        renderMap();
        renderChefs();

        startGameLoop();

        stage.setScene(scene);
        stage.show();
    }

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

    private StackPane createGameArea() {
        StackPane gameArea = new StackPane();
        gameArea.setStyle("-fx-background-color: #0f3460;");
        gameArea.setPadding(new Insets(20));

        mapGrid = new GridPane();
        mapGrid.setHgap(0);
        mapGrid.setVgap(0);

        chefLayer = new Pane();

        gameArea.getChildren().addAll(mapGrid, chefLayer);

        return gameArea;
    }

    private VBox createRightPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(320);
        panel.setStyle("-fx-background-color: #16213e;");

        Text ordersTitle = new Text("ACTIVE ORDERS");
        ordersTitle.setFill(Color.WHITE);
        ordersTitle.setFont(Font.font("Arial", 18));

        ordersBox = new VBox(8);
        ordersBox.setPadding(new Insets(10));
        ordersBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        Text chefsTitle = new Text("CHEFS");
        chefsTitle.setFill(Color.WHITE);
        chefsTitle.setFont(Font.font("Arial", 18));

        chefsBox = new VBox(8);
        chefsBox.setPadding(new Insets(10));
        chefsBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

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

                if (x == 0 && y == 0) {
                    System.out.println("First tile (0,0):");
                    System.out.println("  State: " + tile.getState());
                    System.out.println("  ImageID: " + imageId);
                    System.out.println("  Image size: " + img.getWidth() + "x" + img.getHeight());
                }
            }
        }

        System.out.println("\n=== RENDER SUMMARY ===");
        System.out.println("✓ Success: " + successCount + " tiles");
        System.out.println("❌ NULL tiles: " + nullTileCount);
        System.out.println("❌ NULL images: " + nullImageCount);
        System.out.println("MapGrid children: " + mapGrid.getChildren().size());
        System.out.println("======================\n");
    }

    private int getTileImageId(Tile tile, int x, int y) {
        TileState state = tile.getState();

        if (state == TileState.INGREDIENT_STORAGE) {
            if (y == 2 && x == 3) return 7;
            if (y == 4) {
                if (x == 4) return 9;
                if (x == 6) return 5;
                if (x == 8) return 8;
                if (x == 10) return 6;
            }
            if (y == 9 && x == 6) return 7;
            return 7;
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
            default -> 0;
        };
    }

    /**
     * Render chefs DENGAN item overlay
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

            // === RENDER CHEF SPRITE ===
            Image sprite = (chefIndex == 0) ? pikachuSprite : jigglypuffSprite;

            ImageView chefView = new ImageView(sprite);
            chefView.setFitWidth(80);
            chefView.setFitHeight(TILE_SIZE);
            chefView.setPreserveRatio(true);

            chefView.setLayoutX(pos.getX() * TILE_SIZE);
            chefView.setLayoutY(pos.getY() * TILE_SIZE);

            if (chef.isActive()) {
                chefView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 15, 0.8, 0, 0);");
            }

            chefLayer.getChildren().add(chefView);
            chefViews.put(chef.getId(), chefView);
            chefAnimFrames.put(chef.getId(), 0);

            // === RENDER ITEM OVERLAY (kalau chef bawa item) ===
            if (chef.getInventory() != null) {
                ImageView itemView = createItemOverlay(chef);
                if (itemView != null) {
                    chefLayer.getChildren().add(itemView);
                }
            }

            chefIndex++;
        }
    }

    /**
     * Create item overlay di atas chef
     */
    private ImageView createItemOverlay(Chef chef) {
        Item item = chef.getInventory();
        if (item == null) return null;

        Position pos = chef.getPosition();

        // Get item image berdasarkan nama
        Image itemImg = itemImages.get(item.getName());

        if (itemImg == null) {
            // Fallback: coba cari dengan key lain atau buat placeholder
            itemImg = createItemPlaceholder(item.getName());
        }

        ImageView itemView = new ImageView(itemImg);
        itemView.setFitWidth(TILE_SIZE * 0.6);   // 60% ukuran tile
        itemView.setFitHeight(TILE_SIZE * 0.6);
        itemView.setPreserveRatio(true);

        // Posisi: di atas kepala chef
        itemView.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.2);  // Center
        itemView.setLayoutY(pos.getY() * TILE_SIZE - 25);  // Di atas chef

        // Shadow untuk item
        itemView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.7, 2, 2);");

        return itemView;
    }

    /**
     * Update chef positions - re-render semua (termasuk item overlay)
     */
    private void updateChefPositions() {
        renderChefs();  // Re-render semua chef + items
    }

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

            updateChefPositions();
            updateInfoPanel();
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 500_000_000) {
                    updateInfoPanel();
                    controller.checkGameOver();
                    lastUpdate = now;
                }

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

    private void updateInfoPanel() {
        scoreText.setText("Score: " + controller.getScore());

        int time = controller.getRemainingTime();
        timeText.setText(String.format("Time: %02d:%02d", time / 60, time % 60));

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
        // Uncomment kalau sudah fix LandingPage
        // LandingPage landingPage = new LandingPage(stage);
        // landingPage.show();
    }
}
