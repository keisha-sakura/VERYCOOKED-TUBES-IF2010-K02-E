package main.java.model.recipe;

import java.util.*;

public class RecipePool {
    private static final List<Recipe> recipes = List.of(
            new Recipe("Pizza Margherita"),
            new Recipe("Pizza Sosis"),
            new Recipe("Pizza Ayam")
    );

    private static final Random random = new Random();

    public static Recipe getRandomRecipeStatic() {
        return recipes.get(random.nextInt(recipes.size()));
    }

    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
}