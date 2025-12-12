package model.item;

public class Ingredient extends Item implements Preparable {
    IngredientState state;
    String name;

    public Ingredient(String name) {
        this.name = name;
        this.state = IngredientState.RAW;
    }

    public boolean canBeChopped() {
        return state == IngredientState.RAW;
    }

    public boolean canBePlacedOnPlate() {
        if (this.state == IngredientState.RAW) {
            return false;
        }
        return true;
    }

    public void chop() {
        if (canBeChopped()) {
            this.state = IngredientState.CHOPPED;
            System.out.println(name + " is now CHOPPED");
        } else {
            System.out.println(name + " is already CHOPPED");
        }
    }

    @Override
    public String toString() {
        return name + "(" + state + ")";
    }

    public String getName() {
        return name;
    }

    public IngredientState getState() {
        return state;
    }

    public void setState(IngredientState newState) {
        this.state = newState;
    }

    public boolean canBeCooked() {
        return state == IngredientState.RAW || state == IngredientState.CHOPPED;
    }

    public void cook() {
        if (canBeCooked()) {
            this.state = IngredientState.COOKED;
            System.out.println(name + " is now COOKED");
        } else {
            System.out.println(name + " cannot be cooked in current state: " + state);
        }
    }
}
