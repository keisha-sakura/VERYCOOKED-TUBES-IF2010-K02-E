
package model.station;

import model.core.Position;
import model.entity.Chef;
import model.enums.StationType;
import model.item.Item;
import model.item.utensils.Plate;
import java.util.Stack;


public class PlateStorage extends Station {
    private Stack<Plate> plateStack;
    private int initialCleanPlates;
    
    public PlateStorage(Position position, int initialPlates) {
        super(position, StationType.PLATE_STORAGE);
        this.plateStack = new Stack<>();
        this.initialCleanPlates = initialPlates;
        
        // Inisialisasi dengan plate bersih
        for (int i = 0; i < initialPlates; i++) {
            plateStack.push(new Plate());
        }
    }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        // Chef taruh dirty plates (bisa lebih dari 1)
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (plate.isDirty()) {
                plateStack.push(plate);
                chef.setInventory(null);
            }
            return;
        }
        
        // Chef ambil plate
        if (heldItem == null && !plateStack.isEmpty()) {
            Plate topPlate = plateStack.peek();
            
            if (topPlate.isDirty()) {
                
                int dirtyCount = 0;
                for (int i = plateStack.size() - 1; i >= 0; i--) {
                    if (plateStack.get(i).isDirty()) {
                        dirtyCount++;
                    } else {
                        break;
                    }
                }
                
                
                chef.setInventory(plateStack.pop());
            } else {
                
                chef.setInventory(plateStack.pop());
            }
        }
    }
    
    
    public void returnDirtyPlate(Plate plate) {
        plate.setDirty(true);
        plateStack.push(plate);
    }
    
    public boolean hasCleanPlate() {
        for (Plate p : plateStack) {
            if (!p.isDirty()) return true;
        }
        return false;
    }
    
    @Override
    public boolean canInteract(Chef chef) {
        return true;
    }
    
    @Override
    public String getInteractionPrompt() {
        if (plateStack.isEmpty()) return "No plates available";
        Plate top = plateStack.peek();
        return "Press C to get " + (top.isDirty() ? "dirty" : "clean") + " plate";
    }
}
