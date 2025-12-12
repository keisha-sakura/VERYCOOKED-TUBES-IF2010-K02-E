package model.chef;

import model.position;
import model.enums.ChefAction;
import model.enums.Direction;
import model.item.Item;

public class Chef {
    private String id;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private ChefAction currentAction;
    private boolean isActive;
    private volatile boolean isBusy;

    public Chef(String id, String name, Position startPosition) {
        this.id = id;
        this.name = name;
        this.position = new Position(startPosition);
        this.direction = Direction.DOWN;
        this.inventory = null;
        this.currentAction = ChefAction.IDLE;
        this.isActive = false;
        this.isBusy = false;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public Direction getDirection() { return direction; }
    public Item getInventory() { return inventory; }
    public ChefAction getCurrentAction() { return currentAction; }
    public boolean isActive() { return isActive; }
    public boolean isBusy() { return isBusy; }

    public void setPosition(Position position) { this.position = position; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public void setInventory(Item inventory) { this.inventory = inventory; }
    public void setCurrentAction(ChefAction action) { this.currentAction = action; }
    public void setActive(boolean active) { this.isActive = active; }
    public void setBusy(boolean busy) { this.isBusy = busy; }

    public Position getFrontPosition() {
        return position.move(direction);
    }

    public void move(Direction dir) {
        this.direction = dir;
        this.position = position.move(dir);
        this.currentAction = ChefAction.MOVING;
    }

    public boolean hasInventory() {
        return inventory != null;
    }

    public void clearInventory() {
        this.inventory = null;
    }

    @Override
    public String toString() {
        String status = isActive ? "[ACTIVE]" : "[IDLE]";
        String action = isBusy ? " (BUSY: " + currentAction + ")" : "";
        String inv = hasInventory() ? " | Holding: " + inventory.getName() : " | Empty hands";
        return name + " " + status + action + " at " + position + inv;
    }
}
