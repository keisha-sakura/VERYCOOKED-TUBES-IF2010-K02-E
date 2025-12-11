package main.java.model.recipe;

import java.util.*;

public class Recipe {
    public String name;
    private List<String> ingredients;
    private int cookingTime; // in seconds

    public Recipe(String name) {
        this.name = name;
        this.ingredients = new ArrayList<>();
        initializeRecipe(name);
    }

    /**
     * Initialize recipe with ingredients and cooking time
     */
    private void initializeRecipe(String recipeName) {
        switch(recipeName) {
            case "Pizza Margherita":
                ingredients.addAll(List.of("adonan", "tomat", "keju"));
                cookingTime = 12;
                break;
            case "Pizza Sosis":
                ingredients.addAll(List.of("adonan", "tomat", "keju", "sosis"));
                cookingTime = 15;
                break;
            case "Pizza Ayam":
                ingredients.addAll(List.of("adonan", "tomat", "keju", "ayam"));
                cookingTime = 15;
                break;
        }
    }

    public void showRecipe() {
        System.out.println("=== " + name + " ===");
        switch(name) {
            case "Pizza Margherita" ->
                    System.out.println("Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped)");
            case "Pizza Sosis" ->
                    System.out.println("Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped) + Sosis (Chopped)");
            case "Pizza Ayam" ->
                    System.out.println("Adonan (Chopped) + Tomat (Chopped) + Keju (Chopped) + Ayam (Chopped)");
        }
    }

    //getter
    public List<String> getIngredients() {
        return new ArrayList<>(ingredients);
    }


    public int getCookingTime() {
        return cookingTime;
    }

    /**
     * Check if given ingredients match this recipe
     */
    public boolean matchesIngredients(List<String> ingredientNames) {
        if (ingredientNames.size() != ingredients.size()) {
            return false;
        }

        Set<String> recipeSet = new HashSet<>(ingredients);
        Set<String> inputSet = new HashSet<>(ingredientNames);

        return recipeSet.equals(inputSet);
    }
}