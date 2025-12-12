package view;

import controller.GameController;
import model.chef.*;
import model.item.Item;
import model.map.*;
import model.order.*;
import model.position.*;
import model.station.*;
import java.util.List;

public class GameRenderer {
    private GameController controller;

    public GameRenderer(GameController controller) {
        this.controller = controller;
    }

    public void render() {
        clearScreen();
        renderHeader();
        renderMap();
        renderOrders();
        renderChefInfo();
        renderControls();
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void renderHeader() {
        int timeRemaining = controller.getRemainingTime();
        int score = controller.getScore();

        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.printf("║  VERYCOOKED - PIZZA MAP          Score: %4d    ║%n", score);
        System.out.printf("║  Time: %02d:%02d                                      ║%n",
                timeRemaining / 60, timeRemaining % 60);
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private void renderMap() {
        Map map = controller.getGameMap();
        char[][] grid = map.getGrid();
        List<Chef> chefs = controller.getAllChefs();

        System.out.println("MAP:");
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Position pos = new Position(x, y);
                Tile tile = map.getTile(x, y);

                Chef chefHere = null;
                for (Chef chef : chefs) {
                    if (chef.getPosition().equals(pos)) {
                        chefHere = chef;
                        break;
                    }
                }

                if (chefHere != null) {
                    if (chefHere.isActive()) {
                        System.out.print('@');
                    } else {
                        System.out.print('*');
                    }
                } else {
                    Item item = tile != null ? tile.getItem() : null;
                    if (item != null && (tile.getStation() == null)) {
                        System.out.print(itemSymbol(item));
                    } else {
                        System.out.print(grid[y][x]);
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    private void renderOrders() {
        System.out.println("ACTIVE ORDERS:");
        for (Order order : controller.getOrderManager().getActiveOrders()) {
            System.out.printf("  [%d] %s - %ds remaining%n",
                    order.getPosition(),
                    order.getRecipe().getName(),
                    order.getTimeRemaining());
        }
        if (controller.getOrderManager().getActiveOrders().isEmpty()) {
            System.out.println("  No active orders");
        }
        System.out.println();
    }

    private void renderChefInfo() {
        System.out.println("CHEFS:");
        for (Chef chef : controller.getAllChefs()) {
            System.out.println("  " + chef.toString());
        }
        System.out.println();
    }

    private void renderControls() {
        Chef activeChef = controller.getActiveChef();
        if (activeChef != null) {
            Position frontPos = activeChef.getFrontPosition();
            Station station = controller.getGameMap().getStationAt(frontPos);

            if (station != null) {
                System.out.println("► " + station.getInteractionPrompt());
            } else {
                Tile tile = controller.getGameMap().getTile(frontPos);
                if (tile != null && tile.getState().isWalkable()) {
                    if (tile.getItem() != null && !activeChef.hasInventory()) {
                        System.out.println("► Press C to pick item from floor");
                    } else if (tile.getItem() == null && activeChef.hasInventory()) {
                        System.out.println("► Press C to drop item on floor");
                    }
                }
            }
        }
        System.out.println();
    }


    private char itemSymbol(Item item) {
        if (item == null || item.getName() == null || item.getName().isEmpty()) {
            return '?';
        }
        return Character.toUpperCase(item.getName().charAt(0));
    }

    public void renderMenu() {
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

    public void renderHowToPlay() {
        clearScreen();
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║              HOW TO PLAY                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("OBJECTIVE:");
        System.out.println("  Cook and serve pizzas to fulfill orders!");
        System.out.println();
        System.out.println("CONTROLS:");
        System.out.println("  W/A/S/D - Move chef");
        System.out.println("  C - Pickup/Drop items");
        System.out.println("  V - Interact with station");
        System.out.println("  B - Switch between chefs");
        System.out.println("  Q - Quit game");
        System.out.println();
        System.out.println("STATIONS:");
        System.out.println("  C - Cutting Station (chop ingredients)");
        System.out.println("  R - Oven (cook pizza)");
        System.out.println("  A - Assembly Station");
        System.out.println("  I - Ingredient Storage");
        System.out.println("  P - Plate Storage");
        System.out.println("  W - Washing Station");
        System.out.println("  S - Serving Counter");
        System.out.println("  T - Trash");
        System.out.println();
        System.out.println("Press Enter to return...");
    }
}
