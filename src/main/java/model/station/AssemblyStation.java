
package model.station;

import model.position.*;
import model.chef.*;
import model.interfaces.Preparable;package model.station;

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

        if (heldItem != null && !hasItem()) {
            setItemOnStation(heldItem);
            chef.setInventory(null);
            return;
        }

        // case 2: Ambil item dari station (kalau tangan kosong)
        if (heldItem == null && hasItem()) {
            chef.setInventory(itemOnStation);
            removeItemFromStation();
            return;
        }

        if (heldItem instanceof Plate && itemOnStation instanceof Preparable) {
            Plate plate = (Plate) heldItem;
            Preparable prep = (Preparable) itemOnStation;

            if (!plate.isDirty() && prep.canBePlacedOnPlate() && plate.canAddIngredient()) {
                plate.addIngredient(prep);
                removeItemFromStation();
            }
            return;
        }

        if (itemOnStation instanceof Plate && heldItem instanceof Preparable) {
            Plate plateOnStation = (Plate) itemOnStation;
            Preparable prepInHand = (Preparable) heldItem;

            if (!plateOnStation.isDirty()
                    && prepInHand.canBePlacedOnPlate()
                    && plateOnStation.canAddIngredient()) {
                plateOnStation.addIngredient(prepInHand);
                chef.setInventory(null);
            }
        }
    }

    @Override
    public boolean canInteract(Chef chef) {
        return true;
    }
}

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