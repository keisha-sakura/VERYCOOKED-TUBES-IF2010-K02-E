package model.item.ingredients;

import model.item.Ingredient;

public class Chicken extends Ingredient {
    public Chicken() {
        super("Ayam", true, false);
    }
    
    @Override
    public Chicken clone() {
        Chicken cloned = new Chicken();
        cloned.setState(this.state);
        return cloned;
    }
}
