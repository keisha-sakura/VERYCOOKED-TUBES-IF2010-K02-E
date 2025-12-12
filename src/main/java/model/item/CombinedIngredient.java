package model.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import model.enums.IngredientState;

// Bundles several ingredients into one inventory item while keeping state/name info.
public class CombinedIngredient extends Ingredient {
    private final List<Ingredient> components;

    public CombinedIngredient(List<Ingredient> ingredients) {
        List<Ingredient> flattened = flattenInput(ingredients);
        if (flattened.isEmpty()) {
            throw new IllegalArgumentException("CombinedIngredient requires at least one component");
        }

        super(buildName(flattened), false, false);
        this.components = new ArrayList<>();
        for (Ingredient ingredient : flattened) {
            if (ingredient instanceof CombinedIngredient) {
                // Avoid nesting by reusing the existing component list.
                this.components.addAll(((CombinedIngredient) ingredient).getComponents());
            } else {
                this.components.add(ingredient);
            }
        }
        this.state = determineState(this.components);
        this.name = buildName(this.components);
    }

    private static List<Ingredient> flattenInput(List<Ingredient> ingredients) {
        List<Ingredient> result = new ArrayList<>();
        if (ingredients == null) {
            return result;
        }
        for (Ingredient ingredient : ingredients) {
            if (ingredient instanceof CombinedIngredient) {
                result.addAll(((CombinedIngredient) ingredient).getComponents());
            } else if (ingredient != null) {
                result.add(ingredient);
            }
        }
        return result;
    }

    private static String buildName(List<Ingredient> ingredients) {
        // Build display name like "Keju+Ayam" so UI shows merged items.
        StringJoiner joiner = new StringJoiner("+");
        for (Ingredient ingredient : ingredients) {
            joiner.add(ingredient.getName());
        }
        return joiner.toString();
    }

    private static IngredientState determineState(List<Ingredient> ingredients) {
        boolean hasBurned = false;
        boolean hasCooking = false;
        boolean hasCooked = false;
        boolean hasRaw = false;

        for (Ingredient ingredient : ingredients) {
            IngredientState state = ingredient.getState();
            switch (state) {
                case BURNED:
                    hasBurned = true;
                    break;
                case COOKING:
                    hasCooking = true;
                    break;
                case COOKED:
                    hasCooked = true;
                    break;
                case RAW:
                    hasRaw = true;
                    break;
                case CHOPPED:
                default:
                    break;
            }
        }

        if (hasBurned) {
            return IngredientState.BURNED;
        }
        if (hasCooking) {
            return IngredientState.COOKING;
        }
        if (hasCooked) {
            return IngredientState.COOKED;
        }
        if (!hasRaw) {
            return IngredientState.CHOPPED;
        }
        return IngredientState.RAW;
    }

    @Override
    public boolean canBeChopped() {
        return false;
    }

    @Override
    public boolean canBeCooked() {
        return false;
    }

    @Override
    public boolean canBePlacedOnPlate() {
        if (!super.canBePlacedOnPlate()) {
            return false;
        }
        for (Ingredient ingredient : components) {
            if (!ingredient.canBePlacedOnPlate()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void chop() {
        // Combined ingredients are already in their final form.
    }

    @Override
    public void cook() {
        // Combined ingredients are already in their final form.
    }

    @Override
    public void burn() {
        super.burn();
        for (Ingredient ingredient : components) {
            ingredient.burn();
        }
    }

    @Override
    public void setState(IngredientState state) {
        super.setState(state);
        for (Ingredient ingredient : components) {
            ingredient.setState(state);
        }
    }

    public List<Ingredient> getComponents() {
        return Collections.unmodifiableList(components);
    }

    public int componentCount() {
        return components.size();
    }

    @Override
    public CombinedIngredient clone() {
        List<Ingredient> clonedComponents = new ArrayList<>();
        for (Ingredient ingredient : components) {
            clonedComponents.add(ingredient.clone());
        }
        CombinedIngredient clone = new CombinedIngredient(clonedComponents);
        clone.setState(this.state);
        return clone;
    }

    @Override
    public String toString() {
        return buildName(components) + " (" + getState() + ")";
    }
}
