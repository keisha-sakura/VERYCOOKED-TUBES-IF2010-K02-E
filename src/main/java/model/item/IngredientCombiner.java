package model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import model.enums.IngredientState;

// Utility class for merging ingredients into CombinedIngredient instances.
public final class IngredientCombiner {
    private IngredientCombiner() {
    }

    public static boolean canCombine(Ingredient first, Ingredient second) {
        if (first == null || second == null) {
            return false;
        }
        return isFinal(first) && isFinal(second);
    }

    public static Ingredient combine(Ingredient first, Ingredient second) {
        if (!canCombine(first, second)) {
            return null;
        }

        List<Ingredient> components = new ArrayList<>();
        collect(first, components);
        collect(second, components);
        return new CombinedIngredient(components);
    }

    public static Ingredient combine(List<Ingredient> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            return null;
        }

        List<Ingredient> finals = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            if (!isFinal(ingredient)) {
                return null;
            }
            collect(ingredient, finals);
        }
        return new CombinedIngredient(finals);
    }

    public static boolean isFinal(Ingredient ingredient) {
        if (ingredient == null) {
            return false;
        }
        if (ingredient instanceof CombinedIngredient) {
            CombinedIngredient combined = (CombinedIngredient) ingredient;
            for (Ingredient component : combined.getComponents()) {
                if (!isFinal(component)) {
                    return false;
                }
            }
            return true;
        }
        IngredientState state = ingredient.getState();
        return state == IngredientState.CHOPPED || state == IngredientState.COOKED;
    }

    public static List<Ingredient> flatten(Ingredient ingredient) {
        if (ingredient == null) {
            return Collections.emptyList();
        }
        List<Ingredient> result = new ArrayList<>();
        collect(ingredient, result);
        return result;
    }

    private static void collect(Ingredient ingredient, List<Ingredient> target) {
        if (ingredient instanceof CombinedIngredient) {
            CombinedIngredient combined = (CombinedIngredient) ingredient;
            for (Ingredient component : combined.getComponents()) {
                collect(component, target);
            }
        } else {
            target.add(ingredient);
        }
    }
}
