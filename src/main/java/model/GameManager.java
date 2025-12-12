package model;

import model.order.*;
import model.recipe.*;
import model.map.*;
import model.chef.*;

import java.util.ArrayList;
import java.util.List;

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
    private List<Chef> chefs;
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
        this.activeChef = c1;
        this.totalScore = 0;
        this.pizzaServed = 0;
        this.pizzaBurned = 0;
        this.isGameRunning = false;

        this.chefs = new ArrayList<>();
        this.chefs.add(c1);
        this.chefs.add(c2);

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

    // Getters
    public int getTotalScore() { synchronized (scoreLock) { return totalScore; } }
    public int getPizzaServed() { synchronized (scoreLock) { return pizzaServed; } }
    public int getPizzaBurned() { synchronized (scoreLock) { return pizzaBurned; } }
    public boolean isGameRunning() { return isGameRunning; }
    public long getElapsedSeconds() { return (System.currentTimeMillis() - gameStartTime) / 1000; }
    public long getRemainingSeconds() { return Math.max(0, GAME_DURATION - getElapsedSeconds()); }
    public Map getGameMap() { return gameMap; }
    public List<Chef> getChefs() { return new ArrayList<>(chefs); }
    public OrderManager getOrderManager() { return orderManager; }
}
