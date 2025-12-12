
package model.station;

<<<<<<< HEAD
import model.chef.Chef;
import model.enums.StationType;
import model.item.Ingredient;
import model.item.IngredientCombiner;
import model.item.Item;
import model.item.utensils.Plate;
=======
import model.position.*;
import model.chef.*;
>>>>>>> parent of 7b9e37d (fix: Map layout dan assembling action)
import model.interfaces.Preparable;
import model.position.Position;

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

            // Merge station + inventory ingredients when both are ready components.
            private boolean tryCombine(Chef chef) {
                Item heldItem = chef.getInventory();
                Item stationItem = getItemOnStation();

                Ingredient heldIngredient = asIngredient(heldItem);
                Ingredient stationIngredient = asIngredient(stationItem);

                if (heldIngredient == null || stationIngredient == null) {
                    return false;
                }

                if (!IngredientCombiner.canCombine(heldIngredient, stationIngredient)) {
                    return false;
                }

                Ingredient combined = IngredientCombiner.combine(heldIngredient, stationIngredient);
                if (combined == null) {
                    return false;
                }

                chef.setInventory(combined);
                removeItemFromStation();
                return true;
            }

            private Ingredient asIngredient(Item item) {
                if (item instanceof Ingredient) {
                    return (Ingredient) item;
                }
                return null;
            }

            @Override
            public boolean canInteract(Chef chef) {
                return true;
            }

            @Override
            public String getInteractionPrompt() {
                Item stationItem = getItemOnStation();
                if (stationItem instanceof Plate) {
                    return "Press C to take plate";
                }
                if (stationItem instanceof Preparable) {
                    return "Press C to pick up ingredient";
                }
                return "Press C to place item";
            }
        }