
package model.station;

import model.core.Position;
import model.entity.Chef;
import model.enums.StationType;
import model.interfaces.Interactable;
import model.item.Item;

//superclass (abstact)
public abstract class Station implements Interactable {
    // protected Position position;
    protected StationType type;
    protected Item itemOnStation; // Item yang ada di atas station, defaulny null
    
    public Station(StationType type) {
        // this.position = position;
        this.type = type;
        this.itemOnStation = null;
    }
    
    // public Position getPosition() { return position; }
    public StationType getType() { return type; }
    public Item getItemOnStation() { return itemOnStation; }
    
    public void setItemOnStation(Item item) { 
        this.itemOnStation = item;
        if (item != null) {
            item.setPosition(position);
        }
    }
    
    public void removeItemFromStation() {
        this.itemOnStation = null;
    }
    
    public boolean hasItem() {
        return itemOnStation != null;
    }
    
    @Override
    public abstract void interact(Chef chef);
    
    @Override
    public abstract boolean canInteract(Chef chef);
    
    @Override
    public String getInteractionPrompt() {
        return "Press V to interact with " + type;
    }
    
    @Override
    public String toString() {
        return type;
    }
}







