package controller;

import controller.task.OrderTimerTask;
import java.util.ArrayList;
import java.util.List;
import model.chef.Chef;
import model.enums.Direction;
import model.enums.GameStatus;
import model.item.utensils.Plate;
import model.map.Map;
import model.map.MapPizza;
import model.order.OrderManager;
import model.position.Position;
import model.station.PlateStorage;
import model.station.ServingCounter;
import model.station.Station;

public class GameController {
    private Map gameMap;
    private List<Chef> chefs;
    private int activeChefIndex;
    private OrderManager orderManager;
    private GameStatus gameStatus;
    private int score;
    private long gameStartTime;
    private int gameDuration;
    private OrderTimerTask orderTimerTask;
    private List<PlateStorage> plateStorages;

    public GameController(int gameDurationSeconds) {
        this.gameMap = new Map(new MapPizza());
        this.chefs = new ArrayList<>();
        this.activeChefIndex = 0;
        this.orderManager = new OrderManager();
        this.gameStatus = GameStatus.MENU;
        this.score = 0;
        this.gameDuration = gameDurationSeconds;
        this.plateStorages = new ArrayList<>();

        initializeChefs();
        linkStationsToController();
    }

    private void initializeChefs() {
        List<Position> spawnPoints = gameMap.getChefSpawnPoints();

        if (spawnPoints.size() >= 2) {
            Chef chef1 = new Chef("CHEF_1", "Pikachu", spawnPoints.get(0));
            Chef chef2 = new Chef("CHEF_2", "Jigglypuff", spawnPoints.get(1));

            chef1.setActive(true);

            chefs.add(chef1);
            chefs.add(chef2);
        }
    }

    private void linkStationsToController() {
        for (Station station : gameMap.getAllStations().values()) {
            if (station instanceof ServingCounter) {
                ServingCounter counter = (ServingCounter) station;
                counter.setOrderManager(orderManager);
                counter.setGameController(this);
            }
            if (station instanceof PlateStorage) {
                plateStorages.add((PlateStorage) station);
            }
        }
    }

    public void returnPlateToStorage(Plate plate) {
        if (plate == null) {
            return;
        }
        plate.setDirty(true);
        if (!plateStorages.isEmpty()) {
            plateStorages.get(0).returnDirtyPlate(plate);
        }
    }

    public void startGame() {
        this.gameStatus = GameStatus.PLAYING;
        this.gameStartTime = System.currentTimeMillis();
        this.score = 0;

        orderTimerTask = new OrderTimerTask(orderManager);
        orderTimerTask.start();

        System.out.println("=== GAME STARTED ===");
        System.out.println("Duration: " + gameDuration + " seconds");
        System.out.println("Controls:");
        System.out.println("  WASD - Move");
        System.out.println("  C - Pickup/Drop");
        System.out.println("  V - Interact");
        System.out.println("  B - Switch Chef");
        System.out.println("  Q - Quit");
        System.out.println();
    }

    public void moveChef(Direction direction) {
        Chef activeChef = getActiveChef();
        if (activeChef == null || gameStatus != GameStatus.PLAYING) return;

        activeChef.setDirection(direction);

        Position currentPos = activeChef.getPosition();
        Position newPos = calculateNewPosition(currentPos, direction);

        if (!canMoveTo(newPos)) {
            activeChef.resetAnimationFrame();
            return;
        }

        activeChef.setPosition(newPos);
    }

    private Position calculateNewPosition(Position current, Direction direction) {
        return switch (direction) {
            case UP -> new Position(current.getX(), current.getY() - 1);
            case DOWN -> new Position(current.getX(), current.getY() + 1);
            case LEFT -> new Position(current.getX() - 1, current.getY());
            case RIGHT -> new Position(current.getX() + 1, current.getY());
        };
    }

    private boolean canMoveTo(Position pos) {
        if (pos.getX() < 0 || pos.getY() < 0 ||
                pos.getX() >= gameMap.getWidth() || pos.getY() >= gameMap.getHeight()) {
            return false;
        }

        return gameMap.isWalkable(pos);
    }

    public void pickupDrop() {
        Chef activeChef = getActiveChef();
        if (activeChef == null || activeChef.isBusy()) return;

        Position frontPos = activeChef.getFrontPosition();
        Station station = gameMap.getStationAt(frontPos);

        if (station != null && station.canInteract(activeChef)) {
            station.interact(activeChef);
        }
    }

    public void interact() {
        Chef activeChef = getActiveChef();
        if (activeChef == null || activeChef.isBusy()) return;

        Position frontPos = activeChef.getFrontPosition();
        Station station = gameMap.getStationAt(frontPos);

        if (station != null && station.canInteract(activeChef)) {
            station.interact(activeChef);
        }
    }

    public void switchChef() {
        chefs.get(activeChefIndex).setActive(false);
        activeChefIndex = (activeChefIndex + 1) % chefs.size();
        chefs.get(activeChefIndex).setActive(true);

        System.out.println("Switched to " + getActiveChef().getName());
    }

    public Chef getActiveChef() {
        if (activeChefIndex < chefs.size()) {
            return chefs.get(activeChefIndex);
        }
        return null;
    }

    public List<Chef> getAllChefs() {
        return new ArrayList<>(chefs);
    }

    public Map getGameMap() {
        return gameMap;
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getRemainingTime() {
        if (gameStatus != GameStatus.PLAYING) return 0;

        long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
        int remaining = gameDuration - (int) elapsed;
        return Math.max(0, remaining);
    }

    public void checkGameOver() {
        if (getRemainingTime() <= 0) {
            endGame(true);
            return;
        }

        if (orderManager.hasReachedFailureLimit()) {
            endGame(false);
        }
    }

    private void endGame(boolean timeUp) {
        if (timeUp) {
            gameStatus = score >= 200 ? GameStatus.STAGE_CLEARED : GameStatus.STAGE_FAILED;
            System.out.println("\n=== TIME'S UP! ===");
        } else {
            gameStatus = GameStatus.STAGE_FAILED;
            System.out.println("\n=== TOO MANY FAILED ORDERS! ===");
        }

        if (orderTimerTask != null) {
            orderTimerTask.stopTimer();
        }

        displayResults();
    }

    private void displayResults() {
        System.out.println("\n========== GAME FINISHED ==========");
        System.out.println("Final Score: " + score);
        System.out.println("Status: " + (gameStatus == GameStatus.STAGE_CLEARED ? "PASSED" : "FAILED"));
        System.out.println("Consecutive Failures: " + orderManager.getConsecutiveFailures());
        System.out.println("=====================================\n");
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(GameStatus status) {
        this.gameStatus = status;
    }
}
