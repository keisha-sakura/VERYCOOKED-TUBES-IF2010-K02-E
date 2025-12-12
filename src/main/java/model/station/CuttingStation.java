
package model.station;

import controller.task.ChoppingTask;
import model.chef.Chef;
import model.enums.ChefAction;
import model.enums.StationType;
import model.item.Ingredient;
import model.item.Item;
import model.position.Position;

public class CuttingStation extends Station {
    private static final int CHOPPING_DURATION = 3000; // 3 detik
    
    public CuttingStation(Position position) {
        super(position, StationType.CUTTING);
    }
    
    @Override
    public void interact(Chef chef) {
        // Cutting tables also support plating transfers before other actions.
        if (PlatingHelper.handlePlateInHand(chef, this)) {
            return;
        }

        if (PlatingHelper.handleUtensilInHand(chef, this)) {
            return;
        }

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
                ChoppingTask task = new ChoppingTask(chef, ing, CHOPPING_DURATION);
                chef.setCurrentAction(ChefAction.CHOPPING);
                task.start();
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