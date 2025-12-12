package model.station;

import model.chef.Chef;
import model.interfaces.Preparable;
import model.item.Item;
import model.item.CombinedIngredient;
import model.item.Ingredient;
import model.item.utensils.KitchenUtensil;
import model.item.utensils.Plate;
import java.util.ArrayList;
import java.util.List;

// Centralizes plating rules so multiple stations share consistent behavior.
final class PlatingHelper {
    private PlatingHelper() {
    }

    static boolean handlePlateInHand(Chef chef, Station station) {
        Item heldItem = chef.getInventory();
        if (!(heldItem instanceof Plate)) {
            return false;
        }

        Plate plate = (Plate) heldItem;
        if (plate.isDirty()) {
            return false;
        }

        Item stationItem = station.getItemOnStation();
        if (stationItem instanceof Preparable) {
            Preparable prep = (Preparable) stationItem;
            if (!prep.canBePlacedOnPlate()) {
                return false;
            }

            int requiredSlots = requiredSlots(prep);
            if (requiredSlots > plate.getRemainingSlots()) {
                return false;
            }

            if (!plate.addIngredient(prep)) {
                return false;
            }
            station.removeItemFromStation();
            station.setItemOnStation(plate);
            chef.setInventory(null);
            return true;
        }

        if (stationItem instanceof KitchenUtensil && !(stationItem instanceof Plate)) {
            KitchenUtensil utensil = (KitchenUtensil) stationItem;
            List<Preparable> transferable = collectTransferable(utensil);
            if (transferable.isEmpty()) {
                return false;
            }

            if (transferable.size() > plate.getRemainingSlots()) {
                return false;
            }

            for (Preparable prep : transferable) {
                if (!plate.addIngredient(prep)) {
                    return false;
                }
            }

            utensil.clear();
            return true;
        }

        return false;
    }

    static boolean handleUtensilInHand(Chef chef, Station station) {
        Item heldItem = chef.getInventory();
        if (!(heldItem instanceof KitchenUtensil) || heldItem instanceof Plate) {
            return false;
        }

        KitchenUtensil utensil = (KitchenUtensil) heldItem;
        if (utensil.isEmpty()) {
            return false;
        }

        Item stationItem = station.getItemOnStation();
        if (!(stationItem instanceof Plate)) {
            return false;
        }

        Plate plate = (Plate) stationItem;
        if (plate.isDirty()) {
            return false;
        }

        List<Preparable> transferable = collectTransferable(utensil);
        if (transferable.isEmpty()) {
            return false;
        }

        if (transferable.size() > plate.getRemainingSlots()) {
            return false;
        }

        for (Preparable prep : transferable) {
            if (!plate.addIngredient(prep)) {
                return false;
            }
        }

        utensil.clear();
        station.setItemOnStation(plate);
        return true;
    }

    private static List<Preparable> collectTransferable(KitchenUtensil utensil) {
        List<Preparable> result = new ArrayList<>();
        for (Preparable prep : utensil.getContents()) {
            if (!prep.canBePlacedOnPlate()) {
                return new ArrayList<>();
            }
            if (prep instanceof CombinedIngredient) {
                CombinedIngredient combined = (CombinedIngredient) prep;
                for (Ingredient component : combined.getComponents()) {
                    if (!component.canBePlacedOnPlate()) {
                        return new ArrayList<>();
                    }
                    result.add(component);
                }
            } else {
                result.add(prep);
            }
        }
        return result;
    }

    // Quick helper so callers know how many plate slots a bundle consumes.
    private static int requiredSlots(Preparable prep) {
        if (prep instanceof CombinedIngredient) {
            return ((CombinedIngredient) prep).componentCount();
        }
        return 1;
    }
}
