package model.station;

import model.item.*;
import model.map.*;
import model.chef.*;
import model.recipe.*;
import model.order.*;

import java.util.*;

// ==================== WASHING STATION ====================

// WashingStation.java
public class WashingStation extends Station {
    private static final int WASHING_DURATION = 3000; // 3 seconds per plate
    private Stack<Plate> dirtyPlates;
    private Stack<Plate> cleanPlates;
    private volatile boolean isWashing;
    private volatile int washingProgress;
    private Thread washingThread;
    private final Object lock = new Object();

    public WashingStation(Position pos) {
        super(pos);
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
        this.isWashing = false;
        this.washingProgress = 0;
    }

    @Override
    public boolean isBusy() { return isWashing; }
    @Override
    public int getProgress() { return getWashingProgress(); }

    @Override
    protected boolean canInteract(Chef chef) {
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            Item chefItem = chef.getInventory();

            // Case 1: Put dirty plates on station
            if (chefItem instanceof Plate && !((Plate) chefItem).isClean()) {
                // Handle multiple dirty plates
                if (chefItem instanceof Plate) {
                    Plate plate = (Plate) chefItem;
                    while (plate != null) {
                        dirtyPlates.push(plate);
                    }
                } else {
                    dirtyPlates.push((Plate) chefItem);
                }
                chef.setInventory(null);
                return;
            }

            // Case 2: Take clean plate
            if (chefItem == null && !cleanPlates.isEmpty()) {
                chef.setInventory(cleanPlates.pop());
                return;
            }

            // Case 3: Start washing
            if (!dirtyPlates.isEmpty() && !isWashing) {
                startWashing(chef);
            }
        }
    }

    private void startWashing(Chef chef) {
        isWashing = true;
        washingProgress = 0;
        chef.setIsBusy(true);

        washingThread = new Thread(() -> {
            try {
                while (!dirtyPlates.isEmpty() && isWashing) {
                    // Wash one plate
                    washingProgress = 0;

                    while (washingProgress < 100 && isWashing) {
                        Thread.sleep(100);
                        synchronized (lock) {
                            if (isWashing) {
                                washingProgress += (100.0 / (WASHING_DURATION / 100.0));
                            }
                        }
                    }

                    synchronized (lock) {
                        if (isWashing && !dirtyPlates.isEmpty()) {
                            Plate plate = dirtyPlates.pop();
                            plate.setClean(true);
                            cleanPlates.push(plate);
                            System.out.println("✓ Plate washed! Clean plates: " + cleanPlates.size());
                        }
                    }
                }

                synchronized (lock) {
                    isWashing = false;
                    washingProgress = 0;
                    chef.setIsBusy(false);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "WashingStation-" + position + "-Chef-" + chef.getId());

        washingThread.start();
    }

    public void stopWashing(Chef chef) {
        synchronized (lock) {
            isWashing = false;
            if (chef.isBusy()) {
                chef.setIsBusy(false);
            }
        }
    }

    public int getDirtyPlateCount() {
        return dirtyPlates.size();
    }

    public int getCleanPlateCount() {
        return cleanPlates.size();
    }

    public int getWashingProgress() {
        return washingProgress;
    }

    public boolean isWashing() {
        return isWashing;
    }

    // Called by serving station when returning dirty plates
    public void receiveDirtyPlate(Plate plate) {
        synchronized (lock) {
            plate.setClean(false);
            dirtyPlates.push(plate);
        }
    }
}