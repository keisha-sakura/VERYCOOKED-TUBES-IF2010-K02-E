
package model.station;

import model.position.*;
import model.chef.*;
import model.enums.StationType;
import model.item.Ingredient;
import model.item.Item;
import model.item.utensils.Plate;
import model.interfaces.Preparable;

//unlimited ingredient
public class IngredientStorage extends Station {
    private Class<? extends Ingredient> ingredientType;
    
    public IngredientStorage(Position position, Class<? extends Ingredient> type) {
        super(position, StationType.INGREDIENT_STORAGE);
        this.ingredientType = type;
    }
    
    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();
        
        // case :Jika ada item di atas storage, ambil dulu
        if (heldItem == null && hasItem()) {
            chef.setInventory(itemOnStation);
            removeItemFromStation();
            return;
        }
        
        // case : Taruh item di storage
        if (heldItem != null && !hasItem()) {
            setItemOnStation(heldItem);
            chef.setInventory(null);
            return;
        }
        
        // case : Ambil ingredient baru dari storage
        if (heldItem == null && !hasItem()) {
            try {
                Ingredient newIngredient = ingredientType.getDeclaredConstructor().newInstance();
                chef.setInventory(newIngredient);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        
        // Plating: Chef pegang plate, station ada ingredient
        if (heldItem instanceof Plate) {
            Plate plate = (Plate) heldItem;
            if (!plate.isDirty() && itemOnStation instanceof Preparable) {
                Preparable prep = (Preparable) itemOnStation;
                if (prep.canBePlacedOnPlate() && plate.canAddIngredient()) {
                    plate.addIngredient(prep);
                    removeItemFromStation();
                }
            }
        }
    }
    
    @Override
    public boolean canInteract(Chef chef) {
        return true;
    }
    
    @Override
    public String getInteractionPrompt() {
        try {
            Ingredient sample = ingredientType.getDeclaredConstructor().newInstance();
            return "Press C to get " + sample.getName();
        } catch (Exception e) {
            return "Ingredient Storage";
        }
    }
}