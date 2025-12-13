package model.item.utensils;

import model.enums.IngredientState;
import model.interfaces.CookingDevice;
import model.interfaces.Preparable;
import java.util.HashSet;
import java.util.Set;

public class Oven extends KitchenUtensil implements CookingDevice {
    private static final int MAX_CAPACITY = 5; // Semua ingredient pizza
    private boolean isCooking;
    
    public Oven() {
        super("Oven");
        this.isCooking = false;
    }
    
    @Override
    public boolean isPortable() { 
        return false; // Oven tidak bisa dibawa
    }
    
    @Override
    public int capacity() { 
        return MAX_CAPACITY; 
    }
    
    @Override
    public boolean canAccept(Preparable ingredient) {
        if (ingredient == null) {
            return false;
        }
        if (contents.size() >= MAX_CAPACITY) {
            return false;
        }
        return ingredient.getState() == IngredientState.CHOPPED;
    }
    
    @Override
    public void addIngredient(Preparable ingredient) {
        if (canAccept(ingredient)) {
            contents.add(ingredient);
        }
    }
    
    @Override
    public void removeIngredient(Preparable ingredient) {
        contents.remove(ingredient);
    }
    
    @Override
    public boolean isFull() {
        return contents.size() >= MAX_CAPACITY;
    }
    
    public boolean isCooking() { return isCooking; }
    public void setIsCooking(boolean cooking) { this.isCooking = cooking; }
    
    @Override
    public Oven clone() {
        Oven cloned = new Oven();
        cloned.isCooking = this.isCooking;
        for (Preparable p : this.contents) {
            cloned.contents.add(p.clone());
        }
        return cloned;
    }
}
