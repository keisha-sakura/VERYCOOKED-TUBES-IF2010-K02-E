package main.java.view;

import java.util.Locale;
import java.util.Scanner;
import main.java.model.GameManager;
import main.java.model.map.Map;

public final class Driver {
    private Driver() {
        // Utility class
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        GameManager gameManager = GameManager.getInstance();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Very Cooked CLI Driver ===");
        System.out.println("Commands: start | pause | resume | stop | move <w/a/s/d> | interact | act | map | stats | exit");

        boolean running = true;
        while (running) {
            System.out.print("verycooked> ");
            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                continue;
            }

            String[] tokens = input.split("\\s+");
            String command = tokens[0].toLowerCase(Locale.ROOT);

                switch (command) {
                case "start":
                    if (gameManager.isGameRunning()) {
                        System.out.println("Game already running.");
                    } else {
                        gameManager.startGame();
                    }
                    break;
                case "pause":
                    if (!gameManager.isGameRunning()) {
                        System.out.println("Game already paused.");
                    } else {
                        gameManager.toggleGameState();
                    }
                    break;
                case "resume":
                    if (gameManager.isGameRunning()) {
                        System.out.println("Game already running.");
                    } else {
                        gameManager.toggleGameState();
                    }
                    break;
                case "stop":
                    gameManager.forceEndGame();
                    break;
                case "move":
                    if (tokens.length < 2) {
                        System.out.println("Usage: move <w/a/s/d>");
                        break;
                    }
                    handleMove(gameManager, tokens[1]);
                    break;
                    case "interact":
                        gameManager.interact();
                        break;
                    case "act":
                        gameManager.handlePickupOrDrop();
                        break;
                case "map":
                    Map map = gameManager.getGameMap();
                    if (map == null) {
                        System.out.println("Map not initialised.");
                    } else {
                        map.displayMap();
                    }
                    break;
                case "stats":
                    System.out.printf(Locale.US, "Score: %d | Served: %d | Burned: %d | Time Left: %d s%n",
                            gameManager.getTotalScore(),
                            gameManager.getPizzaServed(),
                            gameManager.getPizzaBurned(),
                            gameManager.getRemainingSeconds());
                    break;
                case "exit":
                    running = false;
                    break;
                default:
                    System.out.println("Unknown command.");
                    break;
            }
        }

        scanner.close();
        System.out.println("Goodbye.");
    }

    private static void handleMove(GameManager manager, String direction) {
        boolean moved;
        switch (direction.toLowerCase(Locale.ROOT)) {
            case "w":
                moved = manager.moveUp();
                break;
            case "a":
                moved = manager.moveLeft();
                break;
            case "s":
                moved = manager.moveDown();
                break;
            case "d":
                moved = manager.moveRight();
                break;
            default:
                System.out.println("Invalid direction. Use w, a, s, or d.");
                return;
        }

        if (moved) {
            System.out.println("Chef moved " + direction.toUpperCase(Locale.ROOT) + ".");
        } else {
            System.out.println("Movement blocked.");
        }
    }
}
