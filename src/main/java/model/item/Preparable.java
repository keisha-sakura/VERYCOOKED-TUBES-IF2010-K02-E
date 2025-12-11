package main.java.model.item;

public interface Preparable {
    String getName();

    boolean canBeChopped();
    boolean canBeCooked();
    boolean canBePlacedOnPlate();

    void chop();
    void cook();
}