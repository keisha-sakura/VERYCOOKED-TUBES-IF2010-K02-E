
package model.station;

import model.position.*;
import model.chef.*;
import model.enums.ChefAction;
import model.item.Item;
import model.item.utensils.Plate;
import controller.task.WashingTask;
import java.util.Stack;
import model.enums.*;

public class WashingStation extends Station {
    private Stack<Plate> dirtyPlates; // Stack plate kotor
    private Stack<Plate> cleanPlates; // Stack plate bersih
    private boolean isWashing;
    private static final int WASHING_DURATION = 3000; // cuci3 detik per plate
    
    public WashingStation(Position position) {
        super(position, StationType.WASHING);
        this.dirtyPlates = new Stack<>();
        this.cleanPlates = new Stack<>();
        this.isWashing = false;
    }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (plate.isDirty()) {
                dirtyPlates.push(plate);
                chef.setInventory(null);
                return;
            }
        }
        
       
        if (heldItem == null && !cleanPlates.isEmpty()) {
            chef.setInventory(cleanPlates.pop());
            return;
        }
        
        
        if (heldItem == null && !dirtyPlates.isEmpty() && !isWashing) {
            Plate dirtyPlate = dirtyPlates.pop();
            isWashing = true;
            WashingTask task = new WashingTask(this, dirtyPlate, WASHING_DURATION);
            task.setChef(chef);
            chef.setCurrentAction(ChefAction.WASHING);
            task.start();
            return;
        }
    }
    
    public void finishWashing(Plate plate) {
        plate.clean();
        cleanPlates.push(plate);
        isWashing = false;
    }
    
    public boolean isWashing() { return isWashing; }
    public void setWashing(boolean washing) { this.isWashing = washing; }
    
    @Override
    public boolean canInteract(Chef chef) {
        return true;
    }
    
    @Override
    public String getInteractionPrompt() {
        if (isWashing) return "Washing...";
        if (!dirtyPlates.isEmpty()) return "Press V to wash plate";
        if (!cleanPlates.isEmpty()) return "Press C to take clean plate";
        return "Washing Station";
    }
}