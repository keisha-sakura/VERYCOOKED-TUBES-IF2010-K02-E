package main.java.model.station;

import main.java.model.item.*;
import main.java.model.chef.*;
import main.java.model.map.*;
import main.java.model.recipe.*;

import java.util.*;

public class PlateStorage extends Station {
    private Stack<Plate> cleanPlates;
    private Stack<Plate> dirtyPlates;
    private final Object lock = new Object();

    public PlateStorage(Position pos, int initialPlateCount) {
        super(pos);
        this.cleanPlates = new Stack<>();
        this.dirtyPlates = new Stack<>();

        // Initialize with clean plates
        for (int i = 0; i < initialPlateCount; i++) {
            cleanPlates.push(new Plate());
        }
    }

    /*temp*/
    public boolean isBusy() { return false; }
    public int getProgress() { return 1; }

    @Override
    protected boolean canInteract(Chef chef) {
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            Item chefItem = chef.getInventory();

            // Case 1: Take clean plate (only if no dirty plates on top)
            if (chefItem == null && dirtyPlates.isEmpty() && !cleanPlates.isEmpty()) {
                chef.setInventory(cleanPlates.pop());
                System.out.println("✓ Took clean plate");
                return;
            }

            // Case 2: Take all dirty plates
            if (chefItem == null && !dirtyPlates.isEmpty()) {
                // Create stack of dirty plates
                Stack<Plate> platesToTake = new Stack<>();
                while (!dirtyPlates.isEmpty()) {
                    platesToTake.push(dirtyPlates.pop());
                }
                chef.setInventory(platesToTake); // May need special handling
                System.out.println("✓ Took " + platesToTake.size() + " dirty plates");
            }
        }
    }

    public void receiveDirtyPlate(Plate plate) {
        synchronized (lock) {
            plate.setClean(false);
            dirtyPlates.push(plate);
        }
    }

    public int getCleanPlateCount() {
        return cleanPlates.size();
    }

    public int getDirtyPlateCount() {
        return dirtyPlates.size();
    }
}