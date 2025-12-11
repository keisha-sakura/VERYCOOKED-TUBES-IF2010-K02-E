package main.java.model;

import main.java.model.order.*;
import main.java.model.recipe.*;
import main.java.model.map.*;
import main.java.model.chef.*;

import java.util.List;

/**
 * GameManager manages the overall game state, including:
 * - Score tracking and management
 * - Order spawning and lifecycle
 * - Chef management...? yang ini ga perlu
 * - Game loop timing
 */
public class GameManager {
    private static GameManager instance;

    private int totalScore;
    private int pizzaServed;
    private int pizzaBurned;
    private volatile boolean isGameRunning;
    private long gameStartTime;
    private final int GAME_DURATION = 300; // 5 minutes in seconds

    private OrderManager orderManager;
    private RecipePool recipePool;
    private List<Chef> chefs;
    private Map gameMap;

    private final Object scoreLock = new Object();
    private final Object orderLock = new Object();

    private GameManager() {
        this.gameMap = MapBuilder.buildMap();
        Chef c1 = new Chef("01", "Pikachu", new Position(2, 8), Direction.DOWN, null, true);
        map.getTile(2, 8).setChef(c1);
        Chef c2 = new Chef("02", "Jigglypuff", new Position(7, 5), Direction.DOWN, null, false);
        map.getTile(7, 5).setChef(c2);
        Chef activeChef = c1;
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
    /**
     * Get singleton instance of GameManager
     */
    public static synchronized GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    /**
     * Start the game and begin spawning orders
     */

    //yang ini klo udah di GUI hapus aja
    public void startGame() {
        this.isGameRunning = true;
        this.gameStartTime = System.currentTimeMillis();

        // Start game loop in separate thread
        Thread gameLoopThread = new Thread(this::gameLoop, "GameLoop");
        gameLoopThread.setDaemon(false);
        gameLoopThread.start();

        System.out.println("═══════════════════════════════════");
        System.out.println("🍕 PIZZA COOKING SIMULATOR STARTED 🍕");
        System.out.println("═══════════════════════════════════");
        System.out.println("Duration: " + GAME_DURATION + " seconds");
        System.out.println("Chefs: " + chefs.size());
    }

    /**
     * Main game loop - runs until game ends
     */
    private void gameLoop() {
        int orderSpawnInterval = 15; // Spawn new order every 15 seconds
        int lastOrderSpawnTime = 0;

        while (isGameRunning) {
            long elapsedSeconds = (System.currentTimeMillis() - gameStartTime) / 1000;

            // Check if game time is up
            if (elapsedSeconds >= GAME_DURATION) {
                endGame();
                break;
            }

            // Spawn new orders periodically
            if (elapsedSeconds - lastOrderSpawnTime >= orderSpawnInterval) {
                spawnNewOrder();
                lastOrderSpawnTime = (int) elapsedSeconds;
            }

            try {
                Thread.sleep(1000); // Update every second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    /**
     * Spawn a new order
     */
    public void spawnNewOrder() {
        synchronized (orderLock) {
            Recipe recipe = recipePool.getRandomRecipe();
            int reward = 100 + (int)(Math.random() * 50); // 100-150 points
            int penalty = 50;

            Order newOrder = new Order(orderManager.getNextOrderId(), recipe, reward, penalty);
            orderManager.addOrder(newOrder);

            System.out.println("📋 New order spawned: " + recipe.getName() + " (+$" + reward + ")");
        }
    }

    /**
     * Increase total score when order is completed successfully
     */
    public void increaseTotalScore(int points) {
        synchronized (scoreLock) {
            this.totalScore += points;
            System.out.println("✓ Score +$" + points + " | Total: $" + totalScore);
        }
    }

    /**
     * Decrease total score when pizza is burned or wrong order served
     */
    public void decreaseTotalScore(int points) {
        synchronized (scoreLock) {
            this.totalScore -= points;
            if (totalScore < 0) totalScore = 0;
            System.out.println("✗ Score -$" + points + " | Total: $" + totalScore);
        }
    }

    /**
     * Increment pizza served counter
     */
    public void incrementPizzaServed() {
        synchronized (scoreLock) {
            this.pizzaServed++;
        }
    }

    /**
     * Increment pizza burned counter
     */
    public void incrementPizzaBurned() {
        synchronized (scoreLock) {
            this.pizzaBurned++;
        }
    }

    /**
     * End the game and display final stats
     */
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

    /**
     * Pause/Resume game
     */
    public void toggleGameState() {
        this.isGameRunning = !this.isGameRunning;
        System.out.println(isGameRunning ? "▶ Game resumed" : "⏸ Game paused");
    }

    /**
     * Force end the game
     */
    public void forceEndGame() {
        endGame();
    }

    // Getters
    public int getTotalScore() {
        synchronized (scoreLock) {
            return totalScore;
        }
    }

    public int getPizzaServed() {
        synchronized (scoreLock) {
            return pizzaServed;
        }
    }

    public int getPizzaBurned() {
        synchronized (scoreLock) {
            return pizzaBurned;
        }
    }

    public boolean moveUp(){
        Position oldPos = new Position(activeChef.getPosition().getRow(), activeChef.getPosition().getCol());
        activeChef.moveUp();
    }

    public boolean isGameRunning() {
        return isGameRunning;
    }

    public long getElapsedSeconds() {
        return (System.currentTimeMillis() - gameStartTime) / 1000;
    }

    public long getRemainingSeconds() {
        return Math.max(0, GAME_DURATION - getElapsedSeconds());
    }

    public Map getGameMap() {
        return gameMap;
    }

    public List<Chef> getChefs() {
        return new ArrayList<>(chefs);
    }

    public OrderManager getOrderManager() {
        return orderManager;
    }
}