package model.item.utensils;

import model.item.Item;
import model.interfaces.Preparable;
import java.util.HashSet;
import java.util.Set;

public abstract class KitchenUtensil extends Item {
    protected Set<Preparable> contents;
    
    public KitchenUtensil(String name) {
        super(name);
        this.contents = new HashSet<>();
    }
    
    public Set<Preparable> getContents() {
        return new HashSet<>(contents);
    }
    
    public void clear() {
        contents.clear();
    }
    
    public boolean isEmpty() {
        return contents.isEmpty();
    }
    
    public int getContentCount() {
        return contents.size();
    }
    
    @Override
    public String toString() {
        return name + " [" + contents.size() + " items]";
    }
}
