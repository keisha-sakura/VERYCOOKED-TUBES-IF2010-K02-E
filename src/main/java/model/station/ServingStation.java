package main.java.model.station;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.recipe.*;
import main.java.model.order.*;

import java.util.*;


// ==================== SERVING STATION ====================

// ServingStation.java
public class ServingStation extends Station {
    private GameManager gameManager;
    private List<Order> activeOrders;
    private final Object lock = new Object();

    public ServingStation(Position pos, GameManager controller) {
        super(pos);
        this.gameManager = controller;
        this.activeOrders = new ArrayList<>();
    }


    @Override
    protected boolean canInteract(Chef chef) {
        Item item = chef.getInventory();
        return item instanceof Plate && ((Plate) item).getContents().size() > 0;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            Item item = chef.getInventory();

            if (!(item instanceof Plate)) {
                throw new InvalidInteractionException("Can only serve dishes on plates");
            }

            Plate plate = (Plate) item;
            Dish dish = createDishFromPlate(plate);

            // Validate against active orders
            Order matchedOrder = findMatchingOrder(dish);

            if (matchedOrder != null) {
                // Success!
                gameManager.increaseTotalScore(matchedOrder.getReward());
                gameManager.incrementPizzaServed();
                activeOrders.remove(matchedOrder);
                System.out.println("✓ Order completed! +" + matchedOrder.getReward() + " points");

                // Spawn new order
                spawnNewOrder();
            } else {
                // Wrong dish
                gameManager.decreaseTotalScore(50); // Penalty
                System.out.println("✗ Wrong dish served! -50 points");
            }

            // Return dirty plate after 10 seconds
            chef.setInventory(null);
            returnDirtyPlateAsync(plate);
        }
    }

    private Dish createDishFromPlate(Plate plate) {
        Dish dish = new Dish();
        dish.setComponents(new ArrayList<>(plate.getContents()));
        return dish;
    }

    private Order findMatchingOrder(Dish dish) {
        for (Order order : activeOrders) {
            if (validateDish(dish, order.getRecipe())) {
                return order;
            }
        }
        return null;
    }

    private boolean validateDish(Dish dish, Recipe recipe) {
        List<Preparable> dishComponents = dish.getComponents();
        List<Preparable> recipeIngredients = recipe.getIngredients();

        if (dishComponents.size() != recipeIngredients.size()) {
            return false;
        }

        // Check if all ingredients match (including state)
        Map<String, Integer> dishMap = new HashMap<>();
        Map<String, Integer> recipeMap = new HashMap<>();

        for (Preparable prep : dishComponents) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;
                String key = ing.getName() + "-" + ing.getState();
                dishMap.put(key, dishMap.getOrDefault(key, 0) + 1);
            }
        }

        for (Preparable prep : recipeIngredients) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;
                String key = ing.getName() + "-" + ing.getState();
                recipeMap.put(key, recipeMap.getOrDefault(key, 0) + 1);
            }
        }

        return dishMap.equals(recipeMap);
    }

    private void returnDirtyPlateAsync(Plate plate) {
        Thread returnThread = new Thread(() -> {
            try {
                Thread.sleep(10000); // 10 seconds
                WashingStation washingStation = findWashingStation();
                if (washingStation != null) {
                    washingStation.receiveDirtyPlate(plate);
                    System.out.println("Dirty plate returned to washing station");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "PlateReturn-" + position + "-" + System.currentTimeMillis());

        returnThread.start();
    }

    private WashingStation findWashingStation() {
        // Find washing station from map
        // Implementation depends on your Map structure
        return null; // Placeholder
    }

    private void spawnNewOrder() {
        // Spawn new order logic
        // Should be handled by GameManager
        gameManager.spawnNewOrder();
    }

    public void setActiveOrders(List<Order> orders) {
        this.activeOrders = orders;
    }
}