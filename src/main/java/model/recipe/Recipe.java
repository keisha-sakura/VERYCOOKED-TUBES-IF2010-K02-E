package model.recipe;

import model.enums.IngredientState;
import java.util.*;

public class Recipe {
    private String name;
    private Map<String, IngredientState> requiredIngredients;
    private int reward;
    private int penalty;

    public Recipe(String name, int reward, int penalty) {
        this.name = name;
        this.reward = reward;
        this.penalty = penalty;
        this.requiredIngredients = new HashMap<>();
    }

    public String getName() { return name; }
    public int getReward() { return reward; }
    public int getPenalty() { return penalty; }

    public void addIngredient(String ingredientName, IngredientState requiredState) {
        requiredIngredients.put(ingredientName, requiredState);
    }

    public Map<String, IngredientState> getRequiredIngredients() {
        return new HashMap<>(requiredIngredients);
    }

    //cek dish sesusai resep
    public boolean matches(List<String> ingredientNames, List<IngredientState> ingredientStates) {
        if (ingredientNames.size() != requiredIngredients.size()) {
            return false;
        }

        Map<String, IngredientState> dishMap = new HashMap<>();
        for (int i = 0; i < ingredientNames.size(); i++) {
            dishMap.put(ingredientNames.get(i), ingredientStates.get(i));
        }

        return dishMap.equals(requiredIngredients);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" (").append(reward).append(" pts):\n");
        for (Map.Entry<String, IngredientState> entry : requiredIngredients.entrySet()) {
            sb.append("  - ").append(entry.getKey()).append(" (").append(entry.getValue()).append(")\n");
        }
        return sb.toString();
    }
}