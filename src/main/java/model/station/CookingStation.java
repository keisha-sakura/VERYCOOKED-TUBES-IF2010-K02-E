package main.java.model.station;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.recipe.*;
import main.java.model.order.*;

import java.util.*;


// ==================== COOKING STATION ====================

// CookingStation.java
public class CookingStation extends Station {
    private static final int COOKING_DURATION = 12000; // 12 seconds
    private static final int BURNING_DURATION = 24000; // 24 seconds total
    private KitchenUtensils kitchenUtensil;
    private volatile boolean isCooking;
    private volatile long cookingStartTime;
    private Thread cookingThread;
    private final Object lock = new Object();

    public CookingStation(Position pos, KitchenUtensils kitchenUtensil) {
        super(pos);
        this.kitchenUtensil = kitchenUtensil;
        this.isCooking = false;
    }

    @Override
    public boolean isBusy() { return isCooking; }
    @Override
    public int getProgress() { return getCookingProgress(); }

    @Override
    protected boolean canInteract(Chef chef) {
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            Item chefItem = chef.getInventory();

            // Case 1: Add ingredient to oven
            if (chefItem instanceof Ingredient && kitchenUtensil instanceof CookingDevice) {
                CookingDevice device = (CookingDevice) kitchenUtensil;
                Ingredient ingredient = (Ingredient) chefItem;

                // Wrap single ingredient in a Set for CookingDevice interface
                Set<Preparable> ingredientSet = new HashSet<>();
                ingredientSet.add(ingredient);

                if (device.canAccept(ingredientSet)) {
                    device.addIngredient(ingredientSet);
                    chef.setInventory(null);
                    startCooking();
                }
            }
            
            // Case 2: Take cooked dish from oven
            else if (chefItem == null && kitchenUtensil instanceof CookingDevice && isCooking) {
                Oven device = (Oven) kitchenUtensil;
                
                // Check if cooking is done
                if (!device.getContents().isEmpty()) {
                    long elapsedTime = System.currentTimeMillis() - cookingStartTime;
                    if (elapsedTime >= COOKING_DURATION) {
                        // Cooking complete, retrieve the dish
                        Dish dish = device.startCooking();
                        chef.setInventory(dish);
                        stopCooking();
                        System.out.println("📥 Took cooked dish: " + dish.getName());
                    }
                }
            }
        }
    }

    private void startCooking() {
        if (isCooking) return;

        isCooking = true;
        cookingStartTime = System.currentTimeMillis();

        cookingThread = new Thread(() -> {
            try {
                // Wait for COOKED state
                Thread.sleep(COOKING_DURATION);

                synchronized (lock) {
                    if (isCooking && kitchenUtensil instanceof CookingDevice) {
                        Oven device = (Oven) kitchenUtensil;
                        for (Preparable prep : device.getContents()) {
                            if (prep instanceof Ingredient) {
                                ((Ingredient) prep).cook();
                            }
                        }
                        System.out.println("✓ Cooking complete! Food is COOKED");
                    }
                }

                // Wait for BURNED state
                Thread.sleep(BURNING_DURATION - COOKING_DURATION);

                synchronized (lock) {
                    if (isCooking && kitchenUtensil instanceof CookingDevice) {
                        Oven device = (Oven) kitchenUtensil;
                        for (Preparable prep : device.getContents()) {
                            if (prep instanceof Ingredient) {
                                Ingredient ingredient = (Ingredient) prep;
                                ingredient.setState(IngredientState.BURNED);
                            }
                        }
                        System.out.println("✗ Food is BURNED!");
                    }
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "CookingStation-" + position + "-Utensil-" + kitchenUtensil.getClass().getSimpleName());

        cookingThread.start();
    }

    private void stopCooking() {
        synchronized (lock) {
            isCooking = false;
            if (cookingThread != null) {
                cookingThread.interrupt();
            }
        }
    }

    public int getCookingProgress() {
        if (!isCooking) return 0;
        long elapsed = System.currentTimeMillis() - cookingStartTime;
        return (int) Math.min(100, (elapsed * 100) / COOKING_DURATION);
    }

    public KitchenUtensils getKitchenUtensil() {
        return kitchenUtensil;
    }

    public boolean isCooking() {
        return isCooking;
    }
}