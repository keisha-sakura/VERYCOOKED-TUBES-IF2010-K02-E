
package model.recipe;

import model.enums.IngredientState;
import java.util.*;

//Recipe Book untuk menyimpan semua resep,
public class RecipeBook {
    private static RecipeBook instance;
    private List<Recipe> recipes;
    
    private RecipeBook() {
        recipes = new ArrayList<>();
        initializePizzaRecipes();
    }
    
    public static RecipeBook getInstance() {
        if (instance == null) {
            instance = new RecipeBook();
        }
        return instance;
    }
    
    
    //Inisialisasi resep Pizza(MapD)
    private void initializePizzaRecipes() {
        // Pizza Margherita: Adonan (CHOPPED) + Tomat (CHOPPED) + Keju (CHOPPED) -> COOKED
        Recipe margherita = new Recipe("Pizza Margherita", 120, -50);
        margherita.addIngredient("Adonan", IngredientState.COOKED);
        margherita.addIngredient("Tomat", IngredientState.COOKED);
        margherita.addIngredient("Keju", IngredientState.COOKED);
        recipes.add(margherita);
        
        // Pizza Sosis: Adonan (CHOPPED) + Tomat (CHOPPED) + Keju (CHOPPED) + Sosis (CHOPPED) -> COOKED
        Recipe sosis = new Recipe("Pizza Sosis", 150, -60);
        sosis.addIngredient("Adonan", IngredientState.COOKED);
        sosis.addIngredient("Tomat", IngredientState.COOKED);
        sosis.addIngredient("Keju", IngredientState.COOKED);
        sosis.addIngredient("Sosis", IngredientState.COOKED);
        recipes.add(sosis);
        
        // Pizza Ayam: Adonan (CHOPPED) + Tomat (CHOPPED) + Keju (CHOPPED) + Ayam (CHOPPED) -> COOKED
        Recipe ayam = new Recipe("Pizza Ayam", 150, -60);
        ayam.addIngredient("Adonan", IngredientState.COOKED);
        ayam.addIngredient("Tomat", IngredientState.COOKED);
        ayam.addIngredient("Keju", IngredientState.COOKED);
        ayam.addIngredient("Ayam", IngredientState.COOKED);
        recipes.add(ayam);
    }
    
    public List<Recipe> getAllRecipes() {
        return new ArrayList<>(recipes);
    }
    
    public Recipe getRandomRecipe() {
        if (recipes.isEmpty()) return null;
        Random random = new Random();
        return recipes.get(random.nextInt(recipes.size()));
    }
    
    //cari resep yang sesuai dengan ingredient yang ada di dish
    public Recipe findMatchingRecipe(List<String> ingredientNames, List<IngredientState> states) {
        for (Recipe recipe : recipes) {
            if (recipe.matches(ingredientNames, states)) {
                return recipe;
            }
        }
        return null;
    }
}
