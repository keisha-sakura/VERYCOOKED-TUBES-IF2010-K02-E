package model.item;

import model.position.*;

public abstract class Item {
    protected String name;
    protected Position position;
    
    public Item(String name) {
        this.name = name;
        this.position = null;
    }
    
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public void setPosition(Position position) { this.position = position; }
    
    @Override
    public abstract String toString();
    
    public abstract Item clone();
}
