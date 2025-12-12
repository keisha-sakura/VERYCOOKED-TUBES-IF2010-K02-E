
package model.station;

import model.core.Position;
import model.entity.Chef;
import model.enums.StationType;
import model.item.Item;
import model.item.utensils.KitchenUtensil;


public class TrashStation extends Station {
    
    public TrashStation() {
        super(StationType.TRASH);
    }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        if (heldItem == null) {
            return;
        }
        
        // buang isi kitchen utensil saja
        if (heldItem instanceof KitchenUtensil) {
            KitchenUtensil utensil = (KitchenUtensil) heldItem;
            utensil.clear();
            System.out.println("Cleared contents of " + utensil.getName());
        } else {
            // Buang item sepenuhnya
            chef.setInventory(null);
            System.out.println("Trashed item");
        }
    }
    
    @Override
    public boolean canInteract(Chef chef) {
        return chef.getInventory() != null;
    }
    
    @Override
    public String getInteractionPrompt() {
        return "Press V to trash item";
    }
}
