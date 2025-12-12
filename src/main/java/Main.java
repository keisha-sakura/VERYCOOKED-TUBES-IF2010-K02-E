import controller.GameController;
import model.enums.*;
import model.enums.*;
import view.GameRenderer;
import java.util.Scanner;

public class Main {
    private static final int GAME_DURATION = 180; // 3 menit
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Initializing VeryCooked... ");

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
        System.out.println("  Cook and serve pizzas to fulfill customer orders");
        System.out.println();
        System.out.println("CONTROLS:");
        System.out.println("  W/A/S/D - Move chef");
        System.out.println("  C - Pickup/Drop items");
        System.out.println("  V - Interact with station");
        System.out.println("  B - Switch between chefs");
        System.out.println("  Q - Quit game");
    }

    private static void playGame() {
        GameController controller = new GameController(GAME_DURATION);
        GameRenderer renderer = new GameRenderer(controller);

        controller.startGame();

        Thread gameLoopThread = new Thread(() -> {
            while (controller.getGameStatus() == GameStatus.PLAYING) {
                renderer.render();
                controller.checkGameOver();

                try {
                    Thread.sleep(100); // Refresh rate
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        gameLoopThread.start();

        while (controller.getGameStatus() == GameStatus.PLAYING) {
            if (System.in.available() > 0) {
                String input = scanner.nextLine().trim().toUpperCase();

                if (input.isEmpty()) continue;

                char command = input.charAt(0);
                handleInput(command, controller);
            }

            try {
                Thread.sleep(50);
            } catch (Exception e) {
                break;
            }
        }

        // Wait for game loop to finish
        try {
            gameLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Show final results
        System.out.println("\nPress Enter to return to menu...");
        scanner.nextLine();
    }

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
                System.out.println("Game quit by player.");
                break;
            default:
                System.out.println("Unknown command: " + command);
        }
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
