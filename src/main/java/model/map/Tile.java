package model.map;

import model.chef.*;
import model.item.*;
import model.map.*;

public class Tile {
    private final Position position;
    private boolean walkable;

    public Tile(Position pos){
        this.position = pos;
    }

    public void setChef(Chef ch){}
    public void removeChef(){}

    public boolean isOccupied(){
        return false;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    public boolean isWalkable(){
        return walkable;
    }

    public Position getPosition() {
        return position;
    }
    public boolean hasItem() {
        return false;
    }

    public Item pickUpItem() {
        return null;
    }

    public void placeItem(Item item) {
        // Default: do nothing
    }

    public boolean canHoldItem() {
        return false;
    }
}
