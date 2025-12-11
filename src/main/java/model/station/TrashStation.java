package main.java.model.station;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.recipe.*;
import main.java.model.order.*;

import java.util.*;

// TrashStation.java
public class TrashStation extends Station {

    public TrashStation(Position pos) {
        super(pos);
    }


    @Override
    protected boolean canInteract(Chef chef) {
        return chef.getInventory() != null;
    }

    @Override
    protected void performInteraction(Chef chef) {
        Item item = chef.getInventory();

        if (item instanceof KitchenUtensils) {
            // Only remove contents, keep the utensil
            KitchenUtensils utensil = (KitchenUtensils) item;
            utensil.getContents().clear();
            System.out.println("✓ Emptied " + utensil.getClass().getSimpleName());
        } else {
            // Remove entire item
            chef.setInventory(null);
            System.out.println("✓ Item trashed");
        }
    }
}