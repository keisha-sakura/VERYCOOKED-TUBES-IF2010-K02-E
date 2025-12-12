
package model.station;

import model.position.*;
import model.chef.*;
import model.interfaces.Preparable;package model.station;

import model.position.*;
import model.chef.*;
import model.interfaces.Preparable;
import model.chef.Chef;
import model.enums.StationType;
import model.item.Item;
import model.item.utensils.Plate;
import model.enums.*;
import model.position.Position;

public class AssemblyStation extends Station {

 public AssemblyStation(Position position) {
        super(position, StationType.ASSEMBLY);
    }

    @Override
    public void interact(Chef chef) {
        if (PlatingHelper.handlePlateInHand(chef, this)) {
            return;
            }

        if (PlatingHelper.handleUtensilInHand(chef, this)) {
            return;
        }

        Item heldItem = chef.getInventory();

        if (heldItem != null && !hasItem()) {
            setItemOnStation(heldItem);
            chef.setInventory(null);
            return;
        }
        if (heldItem == null && hasItem()) {
            chef.setInventory(itemOnStation);
            removeItemFromStation();
        }
    }

    @Override
    public boolean canInteract(Chef chef) {
        return true;
    }
}