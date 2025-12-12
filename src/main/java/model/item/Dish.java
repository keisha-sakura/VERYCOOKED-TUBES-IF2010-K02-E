package model.item;

import model.interfaces.Preparable;
import java.util.ArrayList;
import java.util.List;

public class Dish extends Item {
    private String dishName;
    private List<Preparable> components;
    
    public Dish(String dishName) {
        super(dishName);
        this.dishName = dishName;
        this.components = new ArrayList<>();
    }
    
    public String getDishName() { return dishName; }
    
    public List<Preparable> getComponents() { 
        return new ArrayList<>(components); 
    }
    
    public void addComponent(Preparable component) {
        components.add(component);
    }
    
    public void removeComponent(Preparable component) {
        components.remove(component);
    }
    
    public int getComponentCount() {
        return components.size();
    }
    
    @Override
    public Dish clone() {
        Dish cloned = new Dish(this.dishName);
        for (Preparable p : this.components) {
            cloned.addComponent(p.clone());
        }
        return cloned;
    }
    
    @Override
    public String toString() {
        return dishName + " [" + components.size() + " components]";
    }
}
