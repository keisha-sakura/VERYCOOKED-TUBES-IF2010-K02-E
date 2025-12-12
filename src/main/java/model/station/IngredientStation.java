package model.station;

import model.item.*;
import model.map.*;
import model.chef.*;
import model.recipe.*;
import model.order.*;

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


    @Override
    protected boolean canInteract(Chef chef) {
        return chef.getInventory() == null || itemOnStation != null;
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
        if (chef.getInventory() == null && itemOnStation == null) {
            Ingredient ingredient = createIngredient(ingredientType);
            chef.setInventory(ingredient);
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
