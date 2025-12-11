package main.java.model.chef;

import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.*;
import main.java.model.station.*;

public class Chef {
    private String id;
    private String name;
    private Position position;
    private Direction direction;
    private Item inventory;
    private boolean isActive;
    private boolean isBusy;


    public Chef(String id, String name, Position position, Direction direction, Item inventory) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = Direction.RIGHT;
        this.inventory = null;
        this.isActive = false;
        this.isBusy = false;

    }

    public void move(Map map, int rowChange, int colChange) {
        if (isBusy) {
            return;
        }

        int newRow = this.position.getRow() + rowChange;
        int newCol = this.position.getCol() + colChange;

        Tile targetTile = map.getTile(newRow, newCol);
        if (targetTile != null && targetTile.isWalkable()) {
            this.position = new Position(newRow, newCol);
        }

    }

    public void moveUp(Map map) {
        this.direction = Direction.UP;
        move(map, -1, 0);
    }
    public void moveDown(Map map) {
        this.direction = Direction.DOWN;
        move(map, 1, 0);
    }
    public void moveleft(Map map) {
        this.direction = Direction.LEFT;
        move(map, 0, -1);
    }
    public void moveRight(Map map) {
        this.direction = Direction.RIGHT;
        move(map, 0, 1);
    }
    void dashUp() {

    }
    void dashDown() {

    }
    void dashleft() {

    }
    void dashRight() {

    }

    //pickUp/drop
    public void act(Map map) {
        if (isBusy) {
            return;
        }

        Position frontPosition = getFrontPosition();
        Tile frontTile = map.getTile(frontPosition.getRow(), frontPosition.getCol());

        if (frontTile != null){
            if (this.inventory == null && frontTile.hasItem()) {
                this.inventory = frontTile.pickUpItem();
            }
            else if (this.inventory != null && !frontTile.hasItem() && frontTile.canHoldItem()){
                frontTile.placeItem(this.inventory);
                this.inventory = null;
            }

        }
    }

    public void throwItem() {

    }

    public void interact(Map map) throws ChefBusyException {
        if (isBusy) {
            throw new ChefBusyException("Chef sedang sibuk!");
        }

        Position frontPosition = getFrontPosition();
        Tile frontTile = map.getTile(frontPosition.getRow(), frontPosition.getCol());

        if (frontTile instanceof StationTile){
            Station station = ((StationTile) frontTile).getStation();
            station.interact(this);
        }

    }

    public Position getFrontPosition() {
        int row = this.position.getRow();
        int col = this.position.getCol();

        switch (this.direction) {
            case UP:
                return new Position(row - 1, col);
            case DOWN:
                return new Position(row + 1, col);
            case LEFT:
                return new Position(row, col - 1);
            case RIGHT:
                return new Position(row, col + 1);
            default:
                return this.position;
        }
    }

    public Position getPosition() {
        return this.position;

    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public String getId() {
        return this.id;

    }

    public void setId(String id) {
        this.id = id;

    }

    public String getName() {
        return this.name;

    }

    public void setName(String name) {
        this.name = name;

    }

    public String getDirection() {
        return this.direction;

    }

    public void setDirection(Direction direction) {
        this.direction = direction;

    }

    public Item getInventory() {
        return this.inventory;

    }

    public void setInventory(Item item) {
        this.inventory = item;

    }

    public boolean isHoldingItem() {
        return this.inventory != null;
    }

    public boolean isActive(){
        return this.isActive;
    }

    public void setActive(boolean isActive){
        this.isActive = isActive;
    }

    public boolean isBusy(){
        return this.isBusy;
    }

    public void setIsBusy(boolean isBusy){
        this.isBusy = isBusy;
    }
}