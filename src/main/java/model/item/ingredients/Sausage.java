package model.item.ingredients;

import model.item.Ingredient;

public class Sausage extends Ingredient {
    public Sausage() {
        super("Sosis", true, false);
    }
    
    @Override
    public Sausage clone() {
        Sausage cloned = new Sausage();
        cloned.setState(this.state);
        return cloned;
    }
}
