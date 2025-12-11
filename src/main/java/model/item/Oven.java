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
            if (item instanceof Ingredient) {
                Ingredient ing = (Ingredient) item;
                
                // Check if ingredient is RAW (Oven only accepts CHOPPED for pizza)
                if (ing.getState() == IngredientState.RAW) {
                    throw new IllegalStateException("Oven cannot accept ingredient: " + ing.getName() + ". (Reason: Not prepared/chopped)");
                }
                
                // Oven is only for pizza ingredients, ini sebenernya bisa dihapus juga karna Ingredient storage yang ada harusnya cuman buat pizza
                String ingredientName = ing.getName().toLowerCase();
                if (!ingredientName.equals("adonan") && 
                    !ingredientName.equals("tomat") && 
                    !ingredientName.equals("keju") && 
                    !ingredientName.equals("sosis") && 
                    !ingredientName.equals("ayam")) {
                    throw new IllegalStateException("Oven can only cook pizza ingredients. Invalid ingredient: " + ing.getName());
                }
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
