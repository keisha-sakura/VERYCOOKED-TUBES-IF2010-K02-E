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
    private PlateStorage plateStorage;
    private OrderManager orderManager;
    private final Object lock = new Object();

    public ServingStation(Position pos) {
        super(pos);
        this.orderManager = OrderManager.getInstance();
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
                System.out.println("✓ Order validated: " + matchedOrder.getRecipe().getName());
                matchedOrder.complete();
                orderManager.completeOrder(matchedOrder);
            } else {
                System.out.println("✗ Dish does not match any order");
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
        List<Order> activeOrders = orderManager.getActiveOrders();
        for (Order order : activeOrders) {
            if (validateDish(dish, order.getRecipe())) {
                return order;
            }
        }
        return null;
    }

    private boolean validateDish(Dish dish, Recipe recipe) {
        List<Preparable> dishComponents = dish.getComponents();
        List<String> recipeIngredientNames = recipe.getIngredients();

        if (dishComponents.size() != recipeIngredientNames.size()) {
            return false;
        }

        // Check if all ingredients match (by name)
        Map<String, Integer> dishMap = new HashMap<>();
        Map<String, Integer> recipeMap = new HashMap<>();

        for (Preparable prep : dishComponents) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;
                String name = ing.getName();
                dishMap.put(name, dishMap.getOrDefault(name, 0) + 1);
            }
        }

        for (String ingredientName : recipeIngredientNames) {
            recipeMap.put(ingredientName, recipeMap.getOrDefault(ingredientName, 0) + 1);
        }

        return dishMap.equals(recipeMap);
    }

    private void returnDirtyPlateAsync(Plate plate) {
        Thread returnThread = new Thread(() -> {
            try {
                Thread.sleep(10000); // 10 seconds delay before returning to PlateStorage
                if (plateStorage != null) {
                    plateStorage.receiveDirtyPlate(plate);
                    System.out.println("📥 Dirty plate returned to plate storage");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "PlateReturn-" + position + "-" + System.currentTimeMillis());

        returnThread.start();
    }

    private PlateStorage findPlateStorage() {
        // Returns injected plateStorage reference
        return this.plateStorage;
    }
}
