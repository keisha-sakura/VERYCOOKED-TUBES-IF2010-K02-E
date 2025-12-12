
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
    
    public PlateStorage(int initialPlates) {
        super(StationType.PLATE_STORAGE);
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
        
        // Tidak dapat melakukan drop item apapun pada PlateStorage
        if (heldItem != null) {
            return;
        }
        
        // Ambil satu piring dari atas tumpukan (dirty atau clean)
        if (!plateStack.isEmpty()) {
            chef.setInventory(plateStack.pop());
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
