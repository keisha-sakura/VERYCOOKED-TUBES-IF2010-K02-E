package model.item.ingredients;

import model.item.Ingredient;

public class Dough extends Ingredient {
    public Dough() {
        super("Adonan", true, false);
    }
    
    @Override
    public Dough clone() {
        Dough cloned = new Dough();
        cloned.setState(this.state);
        return cloned;
    }
}
