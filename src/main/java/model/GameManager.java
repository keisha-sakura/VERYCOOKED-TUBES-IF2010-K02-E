package main.java.model;

import main.java.model.order.*;
import main.java.model.recipe.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.item.*;
import main.java.model.station.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

public class GameManager {
    private static GameManager instance;

    private int totalScore;
    private int pizzaServed;
    private int pizzaBurned;
    private volatile boolean isGameRunning;
    private long gameStartTime;
    private final int GAME_DURATION = 300; // 5 minutes

    private OrderManager orderManager;
    private RecipePool recipePool;
    private Chef primaryChef;
    private Chef secondaryChef;
    private Map gameMap;

    private Chef activeChef;

    private final Object scoreLock = new Object();
    private final Object orderLock = new Object();

    private GameManager() {
        this.gameMap = MapBuilder.buildMap();

        Chef c1 = new Chef("01", "Pikachu", new Position(2, 8), Direction.DOWN, null);
        gameMap.getTile(2, 8).setChef(c1);

        Chef c2 = new Chef("02", "Jigglypuff", new Position(7, 5), Direction.DOWN, null);
        gameMap.getTile(7, 5).setChef(c2);
        this.primaryChef = c1;
        this.secondaryChef = c2;
        this.activeChef = c1;
        this.totalScore = 0;
        this.pizzaServed = 0;
        this.pizzaBurned = 0;
        this.isGameRunning = false;

        this.orderManager = OrderManager.getInstance();
        this.recipePool = new RecipePool();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    // START GAME
    public void startGame() {
        this.isGameRunning = true;
        this.gameStartTime = System.currentTimeMillis();

        Thread gameLoopThread = new Thread(this::gameLoop, "GameLoop");
        gameLoopThread.setDaemon(false);
        gameLoopThread.start();

        System.out.println("═══════════════════════════════════");
        System.out.println("🍕 PIZZA COOKING SIMULATOR STARTED 🍕");
        System.out.println("═══════════════════════════════════");
    }

    private void gameLoop() {
        int orderSpawnInterval = 15;
        int lastOrderSpawnTime = 0;

        while (isGameRunning) {
            long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;

            if (elapsedSeconds >= GAME_DURATION) {
                endGame();
                break;
            }

            if (elapsedSeconds - lastOrderSpawnTime >= orderSpawnInterval) {
                spawnNewOrder();
                lastOrderSpawnTime = (int) elapsedSeconds;
            }

            try { Thread.sleep(1000); }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
    }

    public void spawnNewOrder() {
        synchronized (orderLock) {
            Recipe recipe = recipePool.getRandomRecipeStatic();
            int reward = 100 + (int)(Math.random() * 50);
            int penalty = 50;

            Order newOrder = new Order(orderManager.getNextOrderId(), recipe, reward, penalty);
            orderManager.addOrder(newOrder);

            System.out.println("📋 New order spawned: " + recipe.getName() + " (+$" + reward + ")");
        }
    }

    public void increaseTotalScore(int points) {
        synchronized (scoreLock) {
            this.totalScore += points;
            System.out.println("✓ Score +$" + points + " | Total: $" + totalScore);
        }
    }

    public void decreaseTotalScore(int points) {
        synchronized (scoreLock) {
            this.totalScore -= points;
            if (totalScore < 0) totalScore = 0;
            System.out.println("✗ Score -$" + points + " | Total: $" + totalScore);
        }
    }

    public void incrementPizzaServed() { synchronized (scoreLock) { pizzaServed++; } }
    public void incrementPizzaBurned() { synchronized (scoreLock) { pizzaBurned++; } }

    private void endGame() {
        this.isGameRunning = false;

        System.out.println("\n═══════════════════════════════════");
        System.out.println("🏁 GAME OVER! 🏁");
        System.out.println("═══════════════════════════════════");
        System.out.println("Final Score: $" + totalScore);
        System.out.println("Pizzas Served: " + pizzaServed);
        System.out.println("Pizzas Burned: " + pizzaBurned);
        System.out.println("═══════════════════════════════════\n");
    }

    public void toggleGameState() {
        this.isGameRunning = !this.isGameRunning;
        System.out.println(isGameRunning ? "▶ Game resumed" : "⏸ Game paused");
    }

    public void forceEndGame() { endGame(); }

    public boolean moveUp() {
        Position oldPos = activeChef.getPosition();
        if (!activeChef.moveUp(getGameMap())) return false;

        gameMap.getTile(oldPos.getRow(), oldPos.getCol()).removeChef();
        gameMap.getTile(activeChef.getPosition().getRow(), activeChef.getPosition().getCol()).setChef(activeChef);
        return true;
    }

    public boolean moveDown() {
        Position oldPos = activeChef.getPosition();
        if (!activeChef.moveDown(getGameMap())) return false;

        gameMap.getTile(oldPos.getRow(), oldPos.getCol()).removeChef();
        gameMap.getTile(activeChef.getPosition().getRow(), activeChef.getPosition().getCol()).setChef(activeChef);
        return true;
    }

    public boolean moveLeft() {
        Position oldPos = activeChef.getPosition();
        if (!activeChef.moveLeft(getGameMap())) return false;

        gameMap.getTile(oldPos.getRow(), oldPos.getCol()).removeChef();
        gameMap.getTile(activeChef.getPosition().getRow(), activeChef.getPosition().getCol()).setChef(activeChef);
        return true;
    }

    public boolean moveRight() {
        Position oldPos = activeChef.getPosition();
        if (!activeChef.moveRight(getGameMap())) return false;

        gameMap.getTile(oldPos.getRow(), oldPos.getCol()).removeChef();
        gameMap.getTile(activeChef.getPosition().getRow(), activeChef.getPosition().getCol()).setChef(activeChef);
        return true;
    }

    public void switchActiveChef() {
        Chef previousChef = activeChef;

        if (previousChef == primaryChef) {
            activeChef = secondaryChef;
        } else {
            activeChef = primaryChef;
        }

        System.out.println("Switched control to " + describeChef(activeChef) + ".");
    }

    public void handlePickupOrDrop() {
        if (activeChef == null || gameMap == null) {
            System.out.println("No active chef to control.");
            return;
        }

        Tile frontTile = getFrontTile();
        if (frontTile == null) {
            System.out.println("Nothing in front of the chef.");
            return;
        }

        try {
            if (frontTile instanceof StationTile stationTile) {
                Station station = stationTile.getStation();
                // Serving Action: Trigger on Drop key at ServingStation
                if (station instanceof ServingStation servingStation) {
                    try {
                        servingStation.interact(activeChef);
                        Plate servedPlate = servingStation.consumeLastServedPlate();
                        if (servedPlate != null) {
                            handleDirtyPlate(servedPlate);
                        }
                    } catch (RuntimeException ex) {
                        System.out.println(ex.getMessage());
                    }
                    return;
                }
                // Other stations use interact() via 'V'
                System.out.println("Use 'V' to interact with this station.");
                return;
            }

            handleFloorTilePickupDrop(frontTile);
        } catch (ChefException ex) {
            System.out.println(ex.getMessage());
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Separate interaction handler for stations
    public void interact() {
        if (activeChef == null || gameMap == null) {
            System.out.println("No active chef to control.");
            return;
        }

        Tile frontTile = getFrontTile();
        if (!(frontTile instanceof StationTile stationTile)) {
            System.out.println("Nothing to interact with.");
            return;
        }

        Station station = stationTile.getStation();
        try {
            station.interact(activeChef);
            if (station instanceof ServingStation servingStation) {
                Plate servedPlate = servingStation.consumeLastServedPlate();
                if (servedPlate != null) {
                    handleDirtyPlate(servedPlate);
                }
            }
        } catch (RuntimeException ex) {
            System.out.println(ex.getMessage());
        }
    }

    // Route dirty plate to the plate storage
    public void handleDirtyPlate(Plate plate) {
        if (plate == null) return;
        PlateStorage storage = locatePlateStorage();
        if (storage == null) {
            System.out.println("No plate storage available.");
            return;
        }
        storage.receiveDirtyPlate(plate);
    }

    private PlateStorage locatePlateStorage() {
        for (int r = 0; r < gameMap.getHeight(); r++) {
            for (int c = 0; c < gameMap.getWidth(); c++) {
                Tile t = gameMap.getTile(r, c);
                if (t instanceof StationTile st && st.getStation() instanceof PlateStorage ps) {
                    return ps;
                }
            }
        }
        return null;
    }

    private void handleFloorTilePickupDrop(Tile frontTile) throws ChefException {
        Item heldItem = activeChef.getInventory();

        if (heldItem == null) {
            if (!frontTile.hasItem()) {
                System.out.println("No item to pick up.");
                return;
            }

            Item pickedItem = frontTile.pickUpItem();
            if (pickedItem == null) {
                System.out.println("No item to pick up.");
                return;
            }

            activeChef.setInventory(pickedItem);
            System.out.println("Picked up " + describeItem(pickedItem) + ".");
            return;
        }

        if (heldItem instanceof Plate plate && plate.isClean() && frontTile.hasItem()) {
            Item tileItem = frontTile.pickUpItem();
            if (tileItem == null) {
                System.out.println("Nothing to plate.");
                return;
            }

            boolean plated = tryPlateItem(plate, tileItem, frontTile);
            if (!plated) {
                frontTile.placeItem(tileItem);
                System.out.println("Item is not ready to plate.");
            }
            return;
        }

        if (!frontTile.hasItem() && frontTile.canHoldItem()) {
            frontTile.placeItem(heldItem);
            activeChef.setInventory(null);
            System.out.println("Placed " + describeItem(heldItem) + " on the floor.");
            return;
        }

        throw new InventoryFullException("Cannot place item on occupied tile.");
    }

    private boolean tryPlateItem(Plate plate, Item tileItem, Tile originTile) {
        if (tileItem instanceof Ingredient ingredient) {
            if (!ingredient.canBePlacedOnPlate()) {
                return false;
            }
            plate.addIngredient(ingredient);
            System.out.println("Plated " + ingredient.getName() + ".");
            return true;
        }

        if (tileItem instanceof Dish dish) {
            plate.addDish(dish);
            System.out.println("Transferred dish to plate.");
            return true;
        }

        if (tileItem instanceof KitchenUtensils utensils) {
            boolean plated = false;
            Iterator<Preparable> iterator = new ArrayList<>(utensils.getContents()).iterator();
            while (iterator.hasNext()) {
                Preparable prep = iterator.next();
                if (!prep.canBePlacedOnPlate()) {
                    continue;
                }

                if (prep instanceof Ingredient ingredientPrep) {
                    plate.addIngredient(ingredientPrep);
                } else {
                    plate.addDish(prep);
                }
                utensils.getContents().remove(prep);
                plated = true;
            }

            if (plated) {
                originTile.placeItem(utensils);
                System.out.println("Plated items from utensil.");
                return true;
            }
        }

        return false;
    }

    private boolean canPlaceItemOnStation(Station station) {
        if (station == null) {
            return true;
        }

        Item heldItem = activeChef.getInventory();
        if (heldItem == null) {
            return true;
        }

        if (station instanceof WashingStation && heldItem instanceof Plate plate && plate.isClean()) {
            return false;
        }

        if (station instanceof CookingStation && heldItem instanceof Plate) {
            return false;
        }

        return true;
    }

    private Tile getFrontTile() {
        Position frontPosition = getFrontPosition();
        if (frontPosition == null) {
            return null;
        }

        try {
            return gameMap.getTile(frontPosition.getRow(), frontPosition.getCol());
        } catch (ArrayIndexOutOfBoundsException ex) {
            return null;
        }
    }

    private Position getFrontPosition() {
        Position current = activeChef.getPosition();
        Direction direction = activeChef.getDirection();

        return switch (direction) {
            case UP -> current.up(1);
            case DOWN -> current.down(1);
            case LEFT -> current.left(1);
            case RIGHT -> current.right(1);
        };
    }

    private String describeItem(Item item) {
        if (item instanceof Ingredient ingredient) {
            return ingredient.getName();
        }
        if (item instanceof Dish dish) {
            return dish.getName();
        }
        return item.getClass().getSimpleName();
    }

    private String describeChef(Chef chef) {
        if (chef == primaryChef) {
            return "Chef 1";
        }
        if (chef == secondaryChef) {
            return "Chef 2";
        }
        return "Chef";
    }

    // Getters
    public int getTotalScore() { synchronized (scoreLock) { return totalScore; } }
    public int getPizzaServed() { synchronized (scoreLock) { return pizzaServed; } }
    public int getPizzaBurned() { synchronized (scoreLock) { return pizzaBurned; } }
    public boolean isGameRunning() { return isGameRunning; }
    public long getElapsedSeconds() { return (System.currentTimeMillis() - gameStartTime) / 1000; }
    public long getRemainingSeconds() { return Math.max(0, GAME_DURATION - getElapsedSeconds()); }
    public Map getGameMap() { return gameMap; }
    public List<Chef> getChefs() {
        List<Chef> list = new ArrayList<>();
        if (primaryChef != null) list.add(primaryChef);
        if (secondaryChef != null) list.add(secondaryChef);
        return list;
    }
    public OrderManager getOrderManager() { return orderManager; }
}
