package model.item;

import model.enums.IngredientState;
import model.interfaces.Preparable;

public abstract class Ingredient extends Item implements Preparable {
    protected IngredientState state;
    protected boolean choppable;
    protected boolean cookable;
    
    public Ingredient(String name, boolean choppable, boolean cookable) {
        super(name);
        this.state = IngredientState.RAW;
        this.choppable = choppable;
        this.cookable = cookable;
    }
    
    @Override
    public boolean canBeChopped() {
        return choppable && state == IngredientState.RAW;
    }
    
    @Override
    public boolean canBeCooked() {
        return cookable && (state == IngredientState.RAW || state == IngredientState.CHOPPED);
    }
    
    @Override
    public boolean canBePlacedOnPlate() {
        return state != IngredientState.BURNED && state != IngredientState.COOKING;
    }
    
    @Override
    public void chop() {
        if (canBeChopped()) {
            state = IngredientState.CHOPPED;
        }
    }
    
    @Override
    public void cook() {
        if (canBeCooked()) {
            state = IngredientState.COOKING;
        }
    }
    
    @Override
    public void burn() {
        state = IngredientState.BURNED;
    }
    
    public void setCooked() {
        state = IngredientState.COOKED;
    }
    
    @Override
    public IngredientState getState() { return state; }
    
    public void setState(IngredientState state) { this.state = state; }
    
    public boolean isChoppable() { return choppable; }
    public boolean isCookable() { return cookable; }
    
    @Override
    public String toString() {
        return name + " (" + state + ")";
    }
    
    @Override
    public abstract Ingredient clone();
}
