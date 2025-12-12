// File: src/Main.java
import controller.GameController;
import model.enums.Direction;
import model.enums.GameStatus;
import view.GameRenderer;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final int GAME_DURATION = 1200;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Initializing VERYCOOKED...");

        boolean running = true;
        while (running) {
            showMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    playGame();
                    break;
                case "2":
                    showHowToPlay();
                    break;
                case "3":
                    System.out.println("Thanks for playing!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }

        scanner.close();
    }

    private static void showMenu() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║              VERYCOOKED                            ║");
        System.out.println("║              Pizza Edition                         ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("1. Start Game");
        System.out.println("2. How to Play");
        System.out.println("3. Exit");
        System.out.println();
        System.out.print("Choose option: ");
    }

    private static void showHowToPlay() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║              HOW TO PLAY                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("OBJECTIVE:");
        System.out.println("  Cook and serve pizzas to fulfill customer orders!");
        System.out.println();
        System.out.println("CONTROLS:");
        System.out.println("  W - Move Up");
        System.out.println("  A - Move Left");
        System.out.println("  S - Move Down");
        System.out.println("  D - Move Right");
        System.out.println("  C - Pickup/Drop items");
        System.out.println("  V - Interact with station");
        System.out.println("  B - Switch between chefs");
        System.out.println("  Q - Quit game");
        System.out.println();
        System.out.println("LEGEND:");
        System.out.println("  @ - Active Chef (you control this one)");
        System.out.println("  * - Inactive Chef");
        System.out.println("  X - Wall");
        System.out.println("  . - Walkable space");
        System.out.println("  C/R/A/S/W/I/P/T - Stations");
        System.out.println();
        System.out.print("Press Enter to return...");
        scanner.nextLine();
    }

    /**
     * Main game loop - UPDATED untuk render setiap input
     */
    private static void playGame() {
        GameController controller = new GameController(GAME_DURATION);
        GameRenderer renderer = new GameRenderer(controller);

        controller.startGame();

        // Render pertama kali
        renderer.render();

        // Thread untuk timer & order management
        Thread timerThread = new Thread(() -> {
            while (controller.getGameStatus() == GameStatus.PLAYING) {
                try {
                    Thread.sleep(1000); // Check setiap 1 detik
                    controller.checkGameOver();
                } catch (InterruptedException e) {
                    break;
                }
            }
        });

        timerThread.start();

        // Input handling loop
        System.out.println("\n>>> Type command (W/A/S/D/C/V/B/Q) then press Enter:");

        while (controller.getGameStatus() == GameStatus.PLAYING) {
            System.out.print("> ");
            String input = scanner.nextLine().trim().toUpperCase();

            if (input.isEmpty()) {
                continue;
            }

            // Handle input
            char command = input.charAt(0);
            handleInput(command, controller);

            // RENDER ULANG SETIAP KALI ADA INPUT!
            renderer.render();

            // Show command prompt
            System.out.println("\n>>> Type command (W/A/S/D/C/V/B/Q):");
        }

        // Stop timer thread
        try {
            timerThread.join(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final render
        renderer.render();

        System.out.println("\n=== GAME ENDED ===");
        System.out.println("Press Enter to return to menu...");
        scanner.nextLine();
    }

    /**
     * Handle user input
     */
    private static void handleInput(char command, GameController controller) {
        switch (command) {
            case 'W':
                controller.moveChef(Direction.UP);
                break;

            case 'A':
                controller.moveChef(Direction.LEFT);
                break;

            case 'S':
                controller.moveChef(Direction.DOWN);
                break;

            case 'D':
                controller.moveChef(Direction.RIGHT);
                break;

            case 'C':
                controller.pickupDrop();
                break;

            case 'V':
                controller.interact();
                break;

            case 'B':
                controller.switchChef();
                break;

            case 'Q':
                controller.setGameStatus(GameStatus.STAGE_FAILED);
                System.out.println("\n✖ Game quit by player.");
                break;

            default:
                System.out.println("⚠ Unknown command: " + command);
                System.out.println("Valid: W/A/S/D (move), C (pickup/drop), V (interact), B (switch), Q (quit)");
        }
    }

    /**
     * Clear screen
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Fallback
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
}
