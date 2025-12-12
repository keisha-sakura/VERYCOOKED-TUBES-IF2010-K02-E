
package model.station;

import model.position.*;
import model.chef.*;
import model.interfaces.Preparable;
import model.item.Item;
import model.item.utensils.Plate;
import model.enums.*;

public class AssemblyStation extends Station {

    public AssemblyStation(Position position) {
        super(position, StationType.ASSEMBLY);
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();

        // case : Taruh item di station
        if (heldItem != null && !hasItem()) {
            setItemOnStation(heldItem);
            chef.setInventory(null);
            return;
        }

        // case :Ambil item dari station
        if (heldItem == null && hasItem()) {
            chef.setInventory(itemOnStation);
            removeItemFromStation();
            return;
        }

        // case : Plating: Chef pegang plate, station ada ingredient
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
}