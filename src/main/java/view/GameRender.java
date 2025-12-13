package view;

import controller.GameController;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.chef.Chef;
import model.enums.ChefAction;
import model.enums.Direction;
import model.enums.GameStatus;
import model.enums.IngredientState;
import model.item.Ingredient;
import model.item.Item;
import model.item.utensils.Plate;
import model.map.Map;
import model.map.Tile;
import model.order.Order;
import model.position.Position;
import model.station.CookingStation;
import model.station.Station;
import model.station.WashingStation;
import view.component.ImageLoader;
import view.component.PlateRenderer;
import view.component.ProgressBarView;

import java.util.HashMap;
import java.util.Iterator;

public class GameRender extends Application {
    private Stage stage;
    private GameController controller;
    private String difficulty;

    private StackPane root;
    private GridPane mapGrid;
    private Pane itemLayer;
    private Pane chefLayer;
    private Pane progressLayer;

    private AnimationTimer gameLoop;

    private Text scoreText;
    private Text timeText;
    private VBox ordersBox;
    private VBox chefsBox;

    private ImageLoader imageLoader;
    private PlateRenderer plateRenderer;
    private java.util.Map<String, ImageView> chefViews = new HashMap<>();
    private java.util.Map<String, ProgressBarView> activeProgressBars = new HashMap<>();

    private static final double TILE_SIZE = 60;

    public GameRender(Stage stage, String difficulty) {
        this.stage = stage;
        this.difficulty = difficulty;

        int duration = switch (difficulty.toUpperCase()) {
            case "EASY" -> 240;
            case "MEDIUM" -> 180;
            case "HARD" -> 120;
            default -> 180;
        };

        this.controller = new GameController(duration);
        this.imageLoader = new ImageLoader();
        this.plateRenderer = new PlateRenderer(imageLoader);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.controller = new GameController(180);
        this.imageLoader = new ImageLoader();
        this.plateRenderer = new PlateRenderer(imageLoader);

        stage.setTitle("VERYCOOKED");
        showGame();
    }

    public void showGame() {
        Scene scene = createScene();
        controller.startGame();

        renderMap();
        renderChefs();
        startGameLoop();

        stage.setScene(scene);
        stage.show();
    }

    private Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #1a1a2e;");

        mainLayout.setTop(createHeader());
        mainLayout.setCenter(createGameArea());
        mainLayout.setRight(createInfoPanel());

        root = new StackPane(mainLayout);
        Scene scene = new Scene(root, 1280, 720);
        setupInput(scene);

        return scene;
    }

    private HBox createHeader() {
        HBox header = new HBox(30);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #16213e;");

        Text title = new Text("VERYCOOKED");
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
        itemLayer = new Pane();
        chefLayer = new Pane();
        progressLayer = new Pane();

        gameArea.getChildren().addAll(mapGrid, itemLayer, chefLayer, progressLayer);
        return gameArea;
    }

    private VBox createInfoPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(380);
        panel.setStyle("-fx-background-color: #16213e;");

        Text ordersTitle = new Text("ORDERS");
        ordersTitle.setFill(Color.WHITE);
        ordersTitle.setFont(Font.font("Arial", 16));

        ordersBox = new VBox(8);
        ordersBox.setPadding(new Insets(5));

        ScrollPane ordersScroll = new ScrollPane(ordersBox);
        ordersScroll.setFitToWidth(true);
        ordersScroll.setPrefHeight(410);
        ordersScroll.setStyle("-fx-background: #0f3460; -fx-background-color: #0f3460;");
        ordersScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        ordersScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        Text chefsTitle = new Text("CHEFS");
        chefsTitle.setFill(Color.WHITE);
        chefsTitle.setFont(Font.font("Arial", 16));

        chefsBox = new VBox(5);
        chefsBox.setPadding(new Insets(5));
        chefsBox.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        VBox controlsBox = createControlsBox();

        panel.getChildren().addAll(ordersTitle, ordersScroll, chefsTitle, chefsBox, controlsBox);
        return panel;
    }

    private VBox createControlsBox() {
        Text title = new Text("CONTROLS");
        title.setFill(Color.WHITE);
        title.setFont(Font.font("Arial", 12));

        Text controls = new Text(
                "WASD - Move\n" +
                        "C - Pickup/Drop\n" +
                        "V - Interact\n" +
                        "B - Switch Chef\n" +
                        "Q - Quit"
        );
        controls.setFill(Color.LIGHTGRAY);
        controls.setFont(Font.font("Monospaced", 10));

        VBox box = new VBox(3, title, controls);
        box.setPadding(new Insets(8));
        box.setStyle("-fx-background-color: #0f3460; -fx-background-radius: 5;");

        return box;
    }

    private void renderMap() {
        mapGrid.getChildren().clear();

        Map gameMap = controller.getGameMap();
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = gameMap.getTile(x, y);
                if (tile == null) continue;

                Image tileImg = imageLoader.getTileImage(tile.getState(), x, y);
                ImageView iv = new ImageView(tileImg);
                iv.setFitWidth(TILE_SIZE);
                iv.setFitHeight(TILE_SIZE);

                mapGrid.add(iv, x, y);
            }
        }
    }

    private void renderChefs() {
        chefLayer.getChildren().clear();
        chefViews.clear();

        int chefIndex = 0;
        for (Chef chef : controller.getAllChefs()) {
            if (chef == null || chef.getPosition() == null) continue;

            Position pos = chef.getPosition();
            Direction direction = chef.getDirection();
            int frame = chef.getAnimationFrame();

            Image chefSprite = imageLoader.getChefSprite(chefIndex, direction, frame);

            ImageView chefView = new ImageView(chefSprite);
            chefView.setFitWidth(TILE_SIZE);
            chefView.setFitHeight(TILE_SIZE);
            chefView.setPreserveRatio(true);
            chefView.setLayoutX(pos.getX() * TILE_SIZE);
            chefView.setLayoutY(pos.getY() * TILE_SIZE);

            if (chef.isActive()) {
                chefView.setStyle("-fx-effect: dropshadow(gaussian, yellow, 15, 0.8, 0, 0);");
            }

            chefLayer.getChildren().add(chefView);
            chefViews.put(chef.getId(), chefView);

            if (chef.getInventory() != null) {
                renderItemOnChef(chef);
            }

            chefIndex++;
        }
    }

    private void renderItemOnChef(Chef chef) {
        Item item = chef.getInventory();
        Position pos = chef.getPosition();

        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            StackPane plateStack = plateRenderer.renderPlateWithContents(plate, TILE_SIZE * 0.6);
            plateStack.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.2);
            plateStack.setLayoutY(pos.getY() * TILE_SIZE - 25);
            plateStack.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.7, 2, 2);");

            chefLayer.getChildren().add(plateStack);
        } else {
            String itemKey = getItemImageKey(item);
            Image itemImg = imageLoader.getItemImage(itemKey);

            ImageView itemView = new ImageView(itemImg);
            itemView.setFitWidth(TILE_SIZE * 0.6);
            itemView.setFitHeight(TILE_SIZE * 0.6);
            itemView.setPreserveRatio(true);
            itemView.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.2);
            itemView.setLayoutY(pos.getY() * TILE_SIZE - 25);
            itemView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.7, 2, 2);");

            chefLayer.getChildren().add(itemView);
        }
    }

    private void renderItemsOnStationsAndFloor() {
        itemLayer.getChildren().clear();

        Map gameMap = controller.getGameMap();

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                Position pos = new Position(x, y);

                Station station = tile.getStation();
                if (station != null && station.hasItem()) {
                    renderItemOnStation(station.getItemOnStation(), pos);
                }

                if (tile.getItem() != null) {
                    renderItemOnFloor(tile.getItem(), pos);
                }
            }
        }

        renderOvenItems();
    }

    private void renderItemOnStation(Item item, Position pos) {
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            StackPane plateStack = plateRenderer.renderPlateWithContents(plate, TILE_SIZE * 0.5);
            plateStack.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.25);
            plateStack.setLayoutY(pos.getY() * TILE_SIZE + TILE_SIZE * 0.25);

            itemLayer.getChildren().add(plateStack);
        } else {
            String itemKey = getItemImageKey(item);
            Image itemImg = imageLoader.getItemImage(itemKey);

            ImageView itemView = new ImageView(itemImg);
            itemView.setFitWidth(TILE_SIZE * 0.5);
            itemView.setFitHeight(TILE_SIZE * 0.5);
            itemView.setPreserveRatio(true);
            itemView.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.25);
            itemView.setLayoutY(pos.getY() * TILE_SIZE + TILE_SIZE * 0.25);

            itemLayer.getChildren().add(itemView);
        }
    }

    private void renderItemOnFloor(Item item, Position pos) {
        if (item instanceof Plate) {
            Plate plate = (Plate) item;
            StackPane plateStack = plateRenderer.renderPlateWithContents(plate, TILE_SIZE * 0.5);
            plateStack.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.25);
            plateStack.setLayoutY(pos.getY() * TILE_SIZE + TILE_SIZE * 0.25);

            itemLayer.getChildren().add(plateStack);
        } else {
            String itemKey = getItemImageKey(item);
            Image itemImg = imageLoader.getItemImage(itemKey);

            ImageView itemView = new ImageView(itemImg);
            itemView.setFitWidth(TILE_SIZE * 0.5);
            itemView.setFitHeight(TILE_SIZE * 0.5);
            itemView.setPreserveRatio(true);
            itemView.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.25);
            itemView.setLayoutY(pos.getY() * TILE_SIZE + TILE_SIZE * 0.25);

            itemLayer.getChildren().add(itemView);
        }
    }

    private void renderOvenItems() {
        Map gameMap = controller.getGameMap();

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                Station station = tile.getStation();

                if (station instanceof CookingStation) {
                    CookingStation cookStation = (CookingStation) station;

                    if (!cookStation.getOven().isEmpty()) {
                        Position pos = new Position(x, y);

                        cookStation.getOven().getContents().forEach(prep -> {
                            if (prep instanceof Ingredient) {
                                Ingredient ing = (Ingredient) prep;
                                String itemKey = getItemImageKey(ing);
                                Image itemImg = imageLoader.getItemImage(itemKey);

                                ImageView itemView = new ImageView(itemImg);
                                itemView.setFitWidth(TILE_SIZE * 0.4);
                                itemView.setFitHeight(TILE_SIZE * 0.4);
                                itemView.setPreserveRatio(true);
                                itemView.setLayoutX(pos.getX() * TILE_SIZE + TILE_SIZE * 0.3);
                                itemView.setLayoutY(pos.getY() * TILE_SIZE + TILE_SIZE * 0.1);

                                itemLayer.getChildren().add(itemView);
                            }
                        });
                    }
                }
            }
        }
    }

    private String getItemImageKey(Item item) {
        if (item instanceof Ingredient) {
            Ingredient ing = (Ingredient) item;
            IngredientState state = ing.getState();
            String baseName = ing.getName();

            return switch (state) {
                case RAW -> baseName;
                case CHOPPED -> baseName + " (Chopped)";
                case COOKED -> baseName + " (Cooked)";
                case BURNED -> baseName + " (Burned)";
                case COOKING -> baseName;
            };
        }

        return item.getName();
    }

    private void updateProgressBars() {
        Map gameMap = controller.getGameMap();

        Iterator<java.util.Map.Entry<String, ProgressBarView>> iterator =
                activeProgressBars.entrySet().iterator();

        while (iterator.hasNext()) {
            java.util.Map.Entry<String, ProgressBarView> entry = iterator.next();
            ProgressBarView bar = entry.getValue();

            if (bar.isComplete()) {
                progressLayer.getChildren().remove(bar.getNode());
                iterator.remove();
            }
        }

        for (Chef chef : controller.getAllChefs()) {
            if (chef.getCurrentAction() == ChefAction.CHOPPING) {
                String chefKey = "chop_" + chef.getId();

                if (!activeProgressBars.containsKey(chefKey)) {
                    Position frontPos = chef.getFrontPosition();

                    ProgressBarView bar = new ProgressBarView(
                            frontPos.getX() * TILE_SIZE,
                            frontPos.getY() * TILE_SIZE - 12,
                            TILE_SIZE, 8, 3000
                    );

                    activeProgressBars.put(chefKey, bar);
                    progressLayer.getChildren().add(bar.getNode());
                }
            } else {
                String chefKey = "chop_" + chef.getId();
                ProgressBarView bar = activeProgressBars.get(chefKey);
                if (bar != null) {
                    progressLayer.getChildren().remove(bar.getNode());
                    activeProgressBars.remove(chefKey);
                }
            }
        }

        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Tile tile = gameMap.getTile(x, y);
                Station station = tile.getStation();

                if (station instanceof CookingStation) {
                    CookingStation cookStation = (CookingStation) station;
                    String cookKey = "cook_" + x + "_" + y;

                    if (cookStation.getOven().isCooking()) {
                        if (!activeProgressBars.containsKey(cookKey)) {
                            ProgressBarView bar = new ProgressBarView(
                                    x * TILE_SIZE, y * TILE_SIZE - 12,
                                    TILE_SIZE, 8, 12000
                            );

                            activeProgressBars.put(cookKey, bar);
                            progressLayer.getChildren().add(bar.getNode());
                        }
                    } else {
                        ProgressBarView bar = activeProgressBars.get(cookKey);
                        if (bar != null) {
                            progressLayer.getChildren().remove(bar.getNode());
                            activeProgressBars.remove(cookKey);
                        }
                    }
                }

                if (station instanceof WashingStation) {
                    WashingStation washStation = (WashingStation) station;
                    String washKey = "wash_" + x + "_" + y;

                    if (washStation.isWashing()) {
                        if (!activeProgressBars.containsKey(washKey)) {
                            ProgressBarView bar = new ProgressBarView(
                                    x * TILE_SIZE, y * TILE_SIZE - 12,
                                    TILE_SIZE, 8, 3000
                            );

                            activeProgressBars.put(washKey, bar);
                            progressLayer.getChildren().add(bar.getNode());
                        }
                    } else {
                        ProgressBarView bar = activeProgressBars.get(washKey);
                        if (bar != null) {
                            progressLayer.getChildren().remove(bar.getNode());
                            activeProgressBars.remove(washKey);
                        }
                    }
                }
            }
        }
    }

    private void setupInput(Scene scene) {
        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();

            switch (code) {
                case W, UP -> controller.moveChef(Direction.UP);
                case A, LEFT -> controller.moveChef(Direction.LEFT);
                case S, DOWN -> controller.moveChef(Direction.DOWN);
                case D, RIGHT -> controller.moveChef(Direction.RIGHT);
                case C -> controller.pickupDrop();
                case V -> controller.interact();
                case B -> controller.switchChef();
                case Q, ESCAPE -> { stopGameLoop(); backToMenu(); }
            }

            for (Chef chef : controller.getAllChefs()) {
                chef.updateAnimationFrame();
            }

            renderChefs();
            renderItemsOnStationsAndFloor();
            updateProgressBars();
            updateUI();
        });
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) {
                    for (Chef chef : controller.getAllChefs()) {
                        chef.updateAnimationFrame();
                    }

                    renderChefs();
                    renderItemsOnStationsAndFloor();
                    updateProgressBars();
                    updateUI();
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

    private void updateUI() {
        scoreText.setText("Score: " + controller.getScore());

        int time = controller.getRemainingTime();
        timeText.setText(String.format("Time: %02d:%02d", time / 60, time % 60));

        updateOrdersBox();
        updateChefsBox();
    }

    private void updateOrdersBox() {
        ordersBox.getChildren().clear();

        for (Order order : controller.getOrderManager().getActiveOrders()) {
            VBox orderCard = createOrderCard(order);
            ordersBox.getChildren().add(orderCard);
        }
    }

    private VBox createOrderCard(Order order) {
        VBox card = new VBox(3);
        card.setPadding(new Insets(5));
        card.setAlignment(Pos.CENTER);

        int timeRemaining = order.getTimeRemaining();
        String borderColor = timeRemaining < 20 ? "#ff4444" : "#666666";

        card.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 5; " +
                "-fx-border-color: " + borderColor + "; -fx-border-width: 2; -fx-border-radius: 5;");

        Text num = new Text("[" + order.getPosition() + "]");
        num.setFill(Color.ORANGE);
        num.setFont(Font.font("Monospaced", 14));

        String recipeName = order.getRecipe().getName();
        Image orderImg = imageLoader.getOrderImage(recipeName);
        ImageView orderView = new ImageView(orderImg);
        orderView.setFitWidth(250);
        orderView.setFitHeight(200);
        orderView.setPreserveRatio(true);

        HBox timerBox = new HBox(3);
        timerBox.setAlignment(Pos.CENTER);

        Text time = new Text("⏱ " + timeRemaining + "s");
        time.setFill(timeRemaining < 20 ? Color.RED :
                timeRemaining < 40 ? Color.ORANGE : Color.LIGHTGREEN);
        time.setFont(Font.font("Monospaced", 12));

        timerBox.getChildren().add(time);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(150);
        progressBar.setPrefHeight(6);
        double progress = (double) timeRemaining / 60.0;
        progressBar.setProgress(progress);

        String barColor = progress < 0.33 ? "#ff4444" : progress < 0.66 ? "#ffaa00" : "#44ff44";
        progressBar.setStyle("-fx-accent: " + barColor + ";");

        card.getChildren().addAll(num, orderView, timerBox, progressBar);
        return card;
    }

    private void updateChefsBox() {
        chefsBox.getChildren().clear();

        for (Chef chef : controller.getAllChefs()) {
            HBox row = new HBox(5);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(3));
            row.setStyle("-fx-background-color: #1a1a2e; -fx-background-radius: 3;");

            Text status = new Text(chef.isActive() ? "►" : "○");
            status.setFill(chef.isActive() ? Color.YELLOW : Color.GRAY);
            status.setFont(Font.font("Monospaced", 12));

            Text name = new Text(chef.getName());
            name.setFill(Color.WHITE);
            name.setFont(Font.font("Arial", 10));

            row.getChildren().addAll(status, name);
            chefsBox.getChildren().add(row);
        }
    }

    private void showGameOver() {
        if (controller.getGameStatus() == GameStatus.STAGE_CLEARED) {
            SuccessPop.show(root, stage);
        } else {
            FailedPop.show(root, stage);
        }
    }

    private void backToMenu() {
        LandingPage landingPage = new LandingPage(stage);
        Scene scene = landingPage.createScene(1280, 720);
        stage.setScene(scene);
    }
}
