package model.item.utensils;

import java.util.StringJoiner;
import java.util.List;
import model.interfaces.Preparable;
import model.item.CombinedIngredient;
import model.item.Ingredient;

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
    
    // Accept either a single ingredient or a combined bundle so plating stays simple.
    public boolean addIngredient(Preparable ingredient) {
        if (ingredient instanceof CombinedIngredient) {
            CombinedIngredient combined = (CombinedIngredient) ingredient;
            List<Ingredient> components = combined.getComponents();
            if (components.size() > getRemainingSlots()) {
                return false;
            }
            for (Ingredient component : components) {
                if (!addIngredient(component)) {
                    return false;
                }
            }
            return true;
        }

        if (!canAddIngredient()) {
            return false;
        }

        contents.add(ingredient);
        return true;
    }

    public int getRemainingSlots() {
        return isDirty ? 0 : MAX_CAPACITY - contents.size();
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
        if (contents.isEmpty()) {
            return name + status + " [Kosong]";
        }

        StringJoiner joiner = new StringJoiner("+");
        for (Preparable prep : contents) {
            joiner.add(prep.getName());
        }
        return name + status + " [" + joiner.toString() + "]";
    }
}
