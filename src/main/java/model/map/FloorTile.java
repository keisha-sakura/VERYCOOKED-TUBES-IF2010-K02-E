package main.java.model.map;

import main.java.model.chef.*;
import main.java.model.item.*;
import main.java.model.map.*;

public class FloorTile extends Tile{
    private Item item;
    private Chef occupant;

    public FloorTile(Position pos){
        super(pos);
        this.setWalkable(true);
    }

    @Override
    public void setChef(Chef chef){
        this.occupant = chef;
        this.setWalkable(false);
    }

    public void removeChef (){
        this.occupant = null;
        this.setWalkable(true);
    }

    public void setItem(Item item){
        this.item = item;
    }

    public boolean hasItem() {
        return this.item != null;
    }

    public Item pickUpItem() {
        Item pickedItem = this.item;
        this.item = null;
        return pickedItem;
    }

    public void placeItem(Item item) {
        this.item = item;
    }

    public boolean canHoldItem() {
        return this.item == null;
    }

    @Override
    public boolean isOccupied(){
        return (occupant != null);
    }
}
