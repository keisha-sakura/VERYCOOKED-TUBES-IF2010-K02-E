package model.interfaces;

import java.util.Set;

public interface CookingDevice {
    boolean isPortable();
    int capacity();
    boolean canAccept(Preparable ingredient);
    void addIngredient(Preparable ingredient);
    void removeIngredient(Preparable ingredient);
    Set<Preparable> getContents();
    void clear();
    boolean isEmpty();
    boolean isFull();
}
