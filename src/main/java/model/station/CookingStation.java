package model.station;

import model.position.*;
import model.chef.*;
import model.item.Item;
<<<<<<< HEAD
=======

>>>>>>> refactor
import model.item.utensils.Oven;
import model.item.utensils.Plate;
import model.interfaces.Preparable;
import controller.task.CookingTask;
import model.enums.*;

//oven gabisa lepas, hardcoded di cooking station
public class CookingStation extends Station {
    private Oven oven;
    private static final int COOKING_DURATION = 12000; // 12 detik
    private static final int BURNING_DURATION = 24000; // 24 detik total (12 + 12)
    
    public CookingStation(Position position) {
        super(position, StationType.COOKING);
        this.oven = new Oven();
    }
    
    public Oven getOven() { return oven; }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (!plate.isDirty() && !plate.isEmpty()) {
                // Transfer ingredient plate -> oven
                for (Preparable prep : plate.getContents()) {
                    if (oven.canAccept(prep)) {
                        oven.addIngredient(prep);
                        plate.removeIngredient(prep);
                    }
                }
                
                // Mulai cooking
                if (!oven.isEmpty() && !oven.isCooking()) {
                    CookingTask task = new CookingTask(oven, COOKING_DURATION, BURNING_DURATION);
                    oven.setIsCooking(true);
                    task.start();
                }
            }
        }
        
        
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (!oven.isEmpty() && !oven.isCooking()) {
                for (Preparable prep : oven.getContents()) {
                    if (plate.canAddIngredient()) {
                        plate.addIngredient(prep);
                    }
                }
                oven.clear();
            }
        }
    }
    
    @Override
    public boolean canInteract(Chef chef) {
        return chef.getInventory() instanceof Plate;
    }
    
    @Override
    public String getInteractionPrompt() {
        if (oven.isCooking()) {
            return "Oven is cooking...";
        }
        if (!oven.isEmpty()) {
            return "Press V to take cooked items";
        }
        return "Press V to put items in oven";
    }

    
}