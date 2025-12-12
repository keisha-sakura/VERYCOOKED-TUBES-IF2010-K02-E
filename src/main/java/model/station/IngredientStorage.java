
package model.station;

import model.chef.Chef;
import model.enums.StationType;
import model.item.Ingredient;
import model.item.Item;
import model.position.Position;

//unlimited ingredient
public class IngredientStorage extends Station {
    private Class<? extends Ingredient> ingredientType;
    
    public IngredientStorage(Position position, Class<? extends Ingredient> type) {
        super(position, StationType.INGREDIENT_STORAGE);
        this.ingredientType = type;
    }
    
    @Override
    public void interact(Chef chef) {
        // Allow storage tiles to act as plating surfaces too.
        if (PlatingHelper.handlePlateInHand(chef, this)) {
            return;
        }

        if (PlatingHelper.handleUtensilInHand(chef, this)) {
            return;
        }

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