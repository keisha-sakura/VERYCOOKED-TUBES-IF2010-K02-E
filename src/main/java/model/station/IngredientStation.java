package main.java.model.station;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.recipe.*;
import main.java.model.order.*;

import java.util.*;

// IngredientStation.java
public class IngredientStation extends Station {
    private String ingredientType;
    private int quantity; // Unlimited in spec, but track for display

    public IngredientStation(Position pos, String ingredientType) {
        super(pos);
        this.ingredientType = ingredientType;
        this.quantity = Integer.MAX_VALUE; // Unlimited
    }

    // Instant actions; rely on base defaults for monitoring

    @Override
    protected boolean canInteract(Chef chef) {
        // Can always interact: take ingredient, place/take item
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        // Case 1: Take item from top of station first
        if (itemOnStation != null && chef.getInventory() == null) {
            chef.setInventory(itemOnStation);
            itemOnStation = null;
            return;
        }

        // Case 2: Take ingredient from storage
        if (chef.getInventory() == null && itemOnStation == null && quantity > 0) {
            Ingredient ingredient = createIngredient(ingredientType);
            chef.setInventory(ingredient);
            // decrement if finite in future; kept unlimited for now
            System.out.println("✓ Took " + ingredientType + " from storage");
        }

        // Case 3: Place item on station
        if (chef.getInventory() != null && itemOnStation == null) {
            itemOnStation = chef.getInventory();
            chef.setInventory(null);
        }
    }

    private Ingredient createIngredient(String type) {
        return new Ingredient(type); // RAW by default
    }

    public String getIngredientType() {
        return ingredientType;
    }
}
