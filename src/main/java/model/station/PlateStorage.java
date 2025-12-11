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

    
    

    @Override
    protected boolean canInteract(Chef chef) {
        // Only allow taking plates (no dropping allowed on this station)
        return chef.getInventory() == null;
    }

    @Override
    protected void performInteraction(Chef chef) {
        synchronized (lock) {
            // Case 1: Take dirty plate first if any exist (dirty plates on top have priority)
            if (!dirtyPlates.isEmpty()) {
                Plate dirtyPlate = dirtyPlates.pop();
                chef.setInventory(dirtyPlate);
                System.out.println("✓ Took dirty plate (" + dirtyPlates.size() + " remaining)");
                return;
            }

            // Case 2: Take clean plate only if NO dirty plates remain on top
            if (!cleanPlates.isEmpty()) {
                Plate cleanPlate = cleanPlates.pop();
                chef.setInventory(cleanPlate);
                System.out.println("✓ Took clean plate (" + cleanPlates.size() + " remaining)");
                return;
            }

            System.out.println("✗ No plates available");
        }
    }

    // Receive dirty plate from serving station (push to top of stack for priority)
    public void receiveDirtyPlate(Plate plate) {
        synchronized (lock) {
            plate.setClean(false);
            dirtyPlates.push(plate);
            System.out.println("📥 Received dirty plate (" + dirtyPlates.size() + " dirty total)");
        }
    }

    public int getCleanPlateCount() {
        return cleanPlates.size();
    }

    public int getDirtyPlateCount() {
        return dirtyPlates.size();
    }
}