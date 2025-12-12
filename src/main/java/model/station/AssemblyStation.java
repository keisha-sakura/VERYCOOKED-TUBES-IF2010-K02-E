package model.station;

import model.item.*;
import model.map.*;
import model.chef.*;
import model.recipe.*;
import model.order.*;

import java.util.*;
// ==================== ASSEMBLY STATION ====================

// AssemblyStation.java
public class AssemblyStation extends Station {

    public AssemblyStation(Position pos) {
        super(pos);
    }


    @Override
    protected boolean canInteract(Chef chef) {
        return true;
    }

    @Override
    protected void performInteraction(Chef chef) {
        Item chefItem = chef.getInventory();

        // Case 1: Pick up item from station
        if (chefItem == null && itemOnStation != null) {
            chef.setInventory(itemOnStation);
            itemOnStation = null;
            return;
        }

        // Case 2: Place item on station
        if (chefItem != null && itemOnStation == null) {
            itemOnStation = chefItem;
            chef.setInventory(null);
            return;
        }

        // Case 3: Plating (chef has clean plate, station has ingredient)
        if (chefItem instanceof Plate && itemOnStation instanceof Ingredient) {
            Plate plate = (Plate) chefItem;
            Ingredient ingredient = (Ingredient) itemOnStation;

            if (plate.isClean() && ingredient.canBePlacedOnPlate()) {
                plate.addIngredient(ingredient);
                itemOnStation = plate;
                chef.setInventory(null);
            }
        }

        // Case 4: Add ingredient to plate on station
        if (chefItem instanceof Ingredient && itemOnStation instanceof Plate) {
            Plate plate = (Plate) itemOnStation;
            Ingredient ingredient = (Ingredient) chefItem;

            if (plate.isClean() && ingredient.canBePlacedOnPlate()) {
                plate.addIngredient(ingredient);
                chef.setInventory(null);
            }
        }
    }
}