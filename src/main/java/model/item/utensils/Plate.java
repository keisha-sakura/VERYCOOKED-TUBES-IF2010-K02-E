package model.item.utensils;

import model.interfaces.Preparable;

public class Plate extends KitchenUtensil {
    private boolean isDirty;
    private static final int MAX_CAPACITY = 10; // Cukup untuk semua ingredient pizza
    
    public Plate() {
        super("Plate");
        this.isDirty = false;
    }
    
    public boolean isDirty() { return isDirty; }
    
    public void setDirty(boolean dirty) { 
        this.isDirty = dirty;
        if (dirty) {
            contents.clear(); // Plate kotor dikosongkan
        }
    }
    
    public void clean() {
        this.isDirty = false;
    }
    
    public boolean canAddIngredient() {
        return !isDirty && contents.size() < MAX_CAPACITY;
    }
    
    public void addIngredient(Preparable ingredient) {
        if (canAddIngredient()) {
            contents.add(ingredient);
        }
    }
    
    public void removeIngredient(Preparable ingredient) {
        contents.remove(ingredient);
    }
    
    @Override
    public Plate clone() {
        Plate cloned = new Plate();
        cloned.isDirty = this.isDirty;
        for (Preparable p : this.contents) {
            cloned.contents.add(p.clone());
        }
        return cloned;
    }
    
    @Override
    public String toString() {
        String status = isDirty ? " (Kotor)" : " (Bersih)";
        return name + status + " [" + contents.size() + " items]";
    }
}
