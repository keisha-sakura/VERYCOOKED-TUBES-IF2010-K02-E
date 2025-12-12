package model.item;

public class Plate extends KitchenUtensils {
    private boolean isClean;

    public Plate() {
        this.isClean = true;
    }

    public boolean isClean() {
        return isClean;
    }

    public void cleanPlate(){
        this.isClean = true;
    }

    public void setClean(boolean isClean) {
        this.isClean = isClean;
    }

    public void addIngredient(Preparable ingredient) {
        if (!isClean) {
            throw new IllegalStateException("Plate is not clean");
        }
        if (!ingredient.canBePlacedOnPlate()) {
            throw new IllegalArgumentException("Ingredient cannot be placed on plate: " + ingredient);
        }
        getContents().add(ingredient);
        this.isClean = false;
        System.out.println("Added to plate: " + ingredient);
    }

    public void addDish(Preparable dish) {
        getContents().add(dish);
        this.isClean = false;
        System.out.println("Added to plate: " + dish.getName());
    }
}
