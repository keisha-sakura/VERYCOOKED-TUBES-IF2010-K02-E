package model.item.ingredients;

import model.item.Ingredient;

public class Tomato extends Ingredient {
    public Tomato() {
        super("Tomat", true, false);
    }
    
    @Override
    public Tomato clone() {
        Tomato cloned = new Tomato();
        cloned.setState(this.state);
        return cloned;
    }
}
