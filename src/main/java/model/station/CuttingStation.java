
package model.station;

import model.core.Position;
import model.entity.Chef;
import model.enums.ChefAction;
import model.enums.StationType;
import model.interfaces.Preparable;
import model.item.Item;
import model.item.Ingredient;
import model.item.utensils.Plate;
import controller.task.ChoppingTask;


public class CuttingStation extends Station {
    private static final int CHOPPING_DURATION = 3000; // 3 detik
    
    public CuttingStation(Position position) {
        super(position, StationType.CUTTING);
    }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        // Jika chef memegang item dan station kosong -> taruh item
        if (heldItem != null && !hasItem()) {
            setItemOnStation(heldItem);
            chef.setInventory(null);
            return;
        }
        
        // Jika chef tidak memegang apa-apa dan station ada item -> ambil item
        if (heldItem == null && hasItem()) {
            chef.setInventory(itemOnStation);
            removeItemFromStation();
            return;
        }
        
        // Jika station ada ingredient RAW -> mulai chopping
        if (heldItem == null && itemOnStation instanceof Ingredient) {
            Ingredient ing = (Ingredient) itemOnStation;
            if (ing.canBeChopped()) {
                // Start chopping task (concurrency)
                ChoppingTask task = new ChoppingTask(chef, ing, CHOPPING_DURATION);
                chef.setCurrentAction(ChefAction.CHOPPING);
                task.start();
            }
        }
        
        
        handlePlating(chef);
    }
    
    
    private void handlePlating(Chef chef) {
        Item heldItem = chef.getInventory();
        
        
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (!plate.isDirty() && itemOnStation instanceof Preparable) {
                Preparable prep = (Preparable) itemOnStation;
                if (prep.canBePlacedOnPlate()) {
                    plate.addIngredient(prep);
                    removeItemFromStation();
                }
            }
        }
    }
    
    @Override
    public boolean canInteract(Chef chef) {
        return true; // Selalu bisa interact
    }
    
    @Override
    public String getInteractionPrompt() {
        if (hasItem() && itemOnStation instanceof Ingredient) {
            Ingredient ing = (Ingredient) itemOnStation;
            if (ing.canBeChopped()) {
                return "Press V to chop " + ing.getName();
            }
        }
        return "Press C to pickup/drop item";
    }
}