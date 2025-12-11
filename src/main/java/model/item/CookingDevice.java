package main.java.model.item;

import java.util.Set;

public interface CookingDevice {
    public boolean isPortable();

    public boolean canAccept(Set<Preparable> ingredient);

    public void addIngredient(Set<Preparable> ingredient);

    public Dish startCooking();
}