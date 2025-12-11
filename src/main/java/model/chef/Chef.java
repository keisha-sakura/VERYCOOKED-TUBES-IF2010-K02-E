package main.java.model.chef;

import java.util.List;
import main.java.model.item.*;
import main.java.model.map.*;
import main.java.model.chef.Direction;
import main.java.model.station.*;
import main.java.model.chef.ChefBusyException;
import main.java.model.chef.ChefException;
import main.java.model.chef.InventoryEmptyException;
import main.java.model.chef.InventoryFullException;

class Chef {
	private String id;
	private String name;
	private Position position;
	private Direction direction;
	private Item inventory;
    private boolean isActive;
    private boolean isBusy;
    private static final int THROW_DISTANCE = 2;
    private long lastDashTime = 0;
    private static final long DASH_COOLDOWN = 3000; // 3 detik
    private static final int DASH_DISTANCE = 2; // Jarak dash

	Chef(String id, String name, Position position, Direction direction, Item inventory) { 
        this.id = id;
        this.name = name;
        this.position = position;
        this.direction = Direction.RIGHT;
        this.inventory = null;
        this.isActive = false;
        this.isBusy = false;

    }

    public <T extends Item> T getInventoryAs(Class<T> type){
        if (this.inventory != null && type.isInstance(this.inventory)){
            return type.cast(this.inventory);
        }
        return null;
    }

    private void move(Map map, int rowChange, int colChange) {
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

	void moveUp(Map map) {
        this.direction = Direction.UP;
        move(map, -1, 0);
    }

	void moveDown(Map map) { 
        this.direction = Direction.DOWN;
        move(map, 1, 0);
    }

	void moveLeft(Map map) { 
        this.direction = Direction.LEFT;
        move(map, 0, -1);
    }

	void moveRight(Map map) { 
        this.direction = Direction.RIGHT;
        move(map, 0, 1);
    }

    private void performDash(Map map) throws ChefBusyException {
        if (isBusy) {
            throw new ChefBusyException("Chef sedang sibuk!");
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDashTime < DASH_COOLDOWN) {
            return;
        }

        int rowChange = 0;
        int colChange = 0;

        switch (this.direction){
            case UP:
                rowChange = -1;
                break;
            case DOWN:
                rowChange = 1;
                break;
            case LEFT:
                colChange = -1;
                break;
            case RIGHT:
                colChange = 1;
                break;
        }

        boolean hitWall = false;

        for (int i = 0; i < DASH_DISTANCE; i++){
            int targetRow = this.position.getRow() + rowChange;
            int targetCol = this.position.getCol() + colChange;

            Tile targetTile = map.getTile(targetRow, targetCol);

            if (targetTile != null && targetTile.isWalkable()){
                this.position.setRow(targetRow);
                this.position.setCol(targetCol);
            }
            else{
                hitWall = true;
                break;
            }
        }
        
        this.lastDashTime = System.currentTimeMillis();

        if (hitWall){
            System.out.println("Dash terhenti karena menabrak!");
        }
        else {
            System.out.println("Dash berhasil!");
        }
    }

	void dashUp(Map map) throws ChefBusyException { 
        this.direction = Direction.UP;
        performDash(map);
    }

	void dashDown(Map map) throws ChefBusyException { 
        this.direction = Direction.DOWN;
        performDash(map);
    }

	void dashleft(Map map) throws ChefBusyException { 
        this.direction = Direction.LEFT;
        performDash(map);
    }

	void dashRight(Map map) throws ChefBusyException { 
        this.direction = Direction.RIGHT;
        performDash(map);
    }

    //pickUp/drop
	void act(Map map) throws InventoryFullException, ChefBusyException { 
        if (isBusy) {
            throw new ChefBusyException("Chef sedang sibuk!");
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
            else if (this.inventory != null && frontTile.hasItem()){
                throw new InventoryFullException("Inventory chef sudah penuh!");
            }
        }
    } 
    
    void throwItem(Map map, List<Chef> otherChefs) throws InventoryEmptyException, ChefBusyException { 
        if (isBusy){
            throw new ChefBusyException("Chef sedang sibuk!");
        }
        if (inventory == null){
            throw new InventoryEmptyException("Tidak ada item untuk dilempar!");
        }

        Item itemToThrow = this.inventory;
        this.inventory = null;

        Position currentCheckPosition = new Position(this.position.getRow(), this.position.getCol());
        Position lastValidPosition = new Position(this.position.getRow(), this.position.getCol());

        for (int i = 1; i <= THROW_DISTANCE; i++){
            updatePositionInDirection(currentCheckPosition, this.direction);

            Tile tile = map.getTile(currentCheckPosition.getRow(), currentCheckPosition.getCol());
            if (tile == null || !tile.isWalkable()){
                break; // berhenti di posisi terakhir
            }

            Chef receiver = getChefAt(otherChefs, currentCheckPosition);
            if (receiver != null){
                if (!receiver.isHoldingItem()){
                    receiver.setInventory(itemToThrow);
                    System.out.println("Item berhasil dilempar ke chef " + receiver.getName());
                    return;
                }
                else{
                    lastValidPosition = new Position(currentCheckPosition.getRow(), currentCheckPosition.getCol());
                    break;
                }
            }

            lastValidPosition.setRow(lastValidPosition.getRow());
            lastValidPosition.setCol(lastValidPosition.getCol()); 
        }
        
        Tile landingTile = map.getTile(lastValidPosition.getRow(), lastValidPosition.getCol());
        if (landingTile != null && landingTile.canHoldItem() && !landingTile.hasItem()){
            landingTile.placeItem(itemToThrow);
        }
        else{
            this.inventory = itemToThrow;
        }
    }

    private Chef getChefAt(List<Chef> chefs, Position position){
        for (Chef chef : chefs){
            if (!chef.getId().equals(this.id) && chef.getPosition().getRow() == position.getRow() && chef.getPosition().getCol() == position.getCol()){
                return chef;
            }
        }
        return null;
    }
    
	void interact(Map map) throws ChefBusyException { 
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

    private Position getFrontPosition() {
        Position front = new Position(this.position.getRow(), this.position.getCol());
        updatePositionInDirection(front, this.direction);
        return front;
    }

    private void updatePositionInDirection(Position position, Direction direction) {
        switch (direction) {
            case UP:
                position.setRow(position.getRow() - 1);
                break;
            case DOWN:
                position.setRow(position.getRow() + 1);
                break;
            case LEFT:
                position.setCol(position.getCol() - 1);
                break;
            case RIGHT:
                position.setCol(position.getCol() + 1);
                break;
        }
    }


    // Getter and Setter
    Position getPosition() { 
        return this.position;
    } 

	void setPosition(Position position) { 
        this.position = position;
    }

	String getId() { 
        return this.id;
    }

	void setId(String id) { 
        this.id = id;
    }

	String getName() { 
        return this.name;
    }

	void setName(String name) { 
        this.name = name;
    }

	Direction getDirection() { 
        return this.direction;
    }

    void setDirection(Direction direction) { 
        this.direction = direction;
    }

    Item getInventory() { 
        return this.inventory;
    }

    void setInventory(Item item) { 
        this.inventory = item;
    }

    boolean isHoldingItem() { 
        return this.inventory != null;
    }

    boolean isActive(){
        return this.isActive;
    }

    void setActive(boolean isActive){
        this.isActive = isActive;
    }

    boolean isBusy(){
        return this.isBusy;
    }

    void setIsBusy(boolean isBusy){
        this.isBusy = isBusy;
    }
}
