package model.item.ingredients;

import model.item.Ingredient;

public class Cheese extends Ingredient {
    public Cheese() {
        super("Keju", true, false);
    }
    
    @Override
    public Cheese clone() {
        Cheese cloned = new Cheese();
        cloned.setState(this.state);
        return cloned;
    }
}
