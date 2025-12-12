package main.java.interfaces;

import model.enums.IngredientState;

public interface Preparable {
    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();
    void chop();
    void cook();
    void burn();
    IngredientState getState();
    String getName();
    Preparable clone();
}