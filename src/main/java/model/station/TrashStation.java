package model.station;

import model.item.*;
import model.map.*;
import model.chef.*;
import model.recipe.*;
import model.order.*;

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