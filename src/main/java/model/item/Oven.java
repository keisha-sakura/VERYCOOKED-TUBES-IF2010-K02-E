package main.java.model.item;

import java.util.Set;
import java.util.HashSet;

public class Oven extends KitchenUtensils implements CookingDevice {
    private int ovenCapacity = 1;

    public Oven() {

    }

    public int getCapacity() {
        return ovenCapacity;
    }

    public boolean isPortable() {
        return false;
    }

    public boolean canAccept(Set<Preparable> contents) {
        return ovenCapacity > 0;
    }

    public void addIngredient(Set<Preparable> contents) {
        if (!canAccept(contents)) {
            throw new IllegalStateException("Oven cannot accept ingredient: " + contents + ". (Reason: Oven full)");
        }

        for (Preparable item : contents) {
            if (item instanceof Ingredient && ((Ingredient) item).getState() == IngredientState.RAW) {
                throw new IllegalStateException("Oven cannot accept ingredient: " + item.getName() + ". (Reason: Not prepared/chopped)");
            }
        }

        getContents().addAll(contents);

        System.out.println("Added to oven: " + contents);
        ovenCapacity--;
    }

    public Dish startCooking() {
        // Ambil isi Oven dan buat Dish baru
        Set<Preparable> contentsToCook = new HashSet<>(getContents());

        Dish dish = new Dish(contentsToCook);

        System.out.println("Starting to cook: " + dish.getName());
        dish.cook();

        getContents().clear();
        ovenCapacity++;

        return dish;
    }
}
