package main.java.model.station;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.recipe.*;
import main.java.model.order.*;

import java.util.*;

// ==================== CUTTING STATION ====================

// CuttingStation.java
public class CuttingStation extends Station {
    private static final int CUTTING_DURATION = 3000; // 3 seconds in ms
    private volatile int progress; // 0-100
    private volatile boolean isCutting;
    private final Object lock = new Object();
    private Thread cuttingThread;

    public CuttingStation(Position pos) {
        super(pos);
        this.progress = 0;
        this.isCutting = false;
    }

    @Override
    public boolean isBusy() { return isCutting; }

    @Override
    protected boolean canInteract(Chef chef) {
        // Can interact if: placing item, taking item, or cutting
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            // Case 1: Take item from station
            if (chef.getInventory() == null && itemOnStation != null) {
                chef.setInventory(itemOnStation);
                itemOnStation = null;
                progress = 0;
                return;
            }

            // Case 2: Place item on station
            if (chef.getInventory() != null && itemOnStation == null) {
                itemOnStation = chef.getInventory();
                chef.setInventory(null);
                return;
            }

            // Case 3: Start/Resume cutting
            if (itemOnStation instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) itemOnStation;
                if (ingredient.canBeChopped() &&
                        ingredient.getState() == IngredientState.RAW) {
                    startCutting(chef);
                }
            }
        }
    }

    private void startCutting(Chef chef) {
        if (isCutting) return;

        isCutting = true;
        chef.setIsBusy(true);

        cuttingThread = new Thread(() -> {
            try {
                while (progress < 100 && isCutting) {
                    Thread.sleep(100); // Update every 100ms
                    synchronized (lock) {
                        if (isCutting) {
                            progress += (100.0 / (CUTTING_DURATION / 100.0));
                            if (progress >= 100) {
                                progress = 100;
                                completeCutting(chef);
                            }
                        }
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "CuttingStation-" + position + "-Chef-" + chef.getId());

        cuttingThread.start();
    }

    public void stopCutting(Chef chef) {
        synchronized (lock) {
            isCutting = false;
            if (chef.isBusy()) {
                chef.setIsBusy(false);
            }
        }
    }

    private void completeCutting(Chef chef) {
        synchronized (lock) {
            if (itemOnStation instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) itemOnStation;
                ingredient.chop();
                progress = 0;
                isCutting = false;
                chef.setIsBusy(false);
                System.out.println("✓ Cutting complete! " + ingredient.getName() + " is now CHOPPED");
            }
        }
    }

    @Override
    public int getProgress() { return progress; }

    public boolean isCutting() {
        return isCutting;
    }

    // Assembly functionality (dapat merakit hidangan)
    public void assemble(Chef chef) {
        // Implementation for assembly on cutting station
        if (chef.getInventory() instanceof Plate) {
            Plate plate = (Plate) chef.getInventory();
            if (plate.isClean() && itemOnStation instanceof Ingredient) {
                Ingredient ingredient = (Ingredient) itemOnStation;
                if (ingredient.canBePlacedOnPlate()) {
                    plate.addIngredient(ingredient);
                    itemOnStation = null;
                }
            }
        }
    }
}