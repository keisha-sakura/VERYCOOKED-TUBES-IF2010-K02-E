// File: model/map/Tile.java
package model.map;

import model.item.*;
import model.station.*;
import model.position.Position;

public class Tile {
    private final int x;
    private final int y;
    private String symbol;
    private TileState state; // Changed to TileState enum
    private final Station stationContained;
    private Item itemContained;

    public Tile(int x, int y, String symbol) {
        this.x = x;
        this.y = y;
        this.symbol = symbol;
        this.state = TileState.getState(symbol); // Use enum directly
        this.itemContained = null;

        Position pos = new Position(this.x, this.y);

        // Create station jika bukan wall atau walkable
        if(state.isWall() || state.isWalkable()) {
            this.stationContained = null;
        } else {
            this.stationContained = StationFactory.createStationObject(pos, symbol);
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
        this.state = TileState.getState(symbol);
    }

    public TileState getState() {
        return state;
    }

    public String getStateName() {
        return state.getName();
    }

    public boolean isWall(int x, int y) {
        return state.isWall();
    }

    public boolean isWalkable(int x, int y) {
        return state.isWalkable();
    }

    public boolean isStation(int x, int y) {
        return stationContained != null;
    }

    public boolean isChefSpawn() {
        return state == TileState.SPAWN;
    }

    public Station getStation() {
        return stationContained;
    }

    public Item getItem() {
        return itemContained;
    }

    public void setItem(Item item) {
        itemContained = item;
    //     this.itemContained = item;
    //     if (item != null) {
    //         item.setPosition(new Position(this.x, this.y));
    //     }
    // }
    }

    public Item removeItem() {
        Item removedItem = this.itemContained;
        this.itemContained = null;
        // if (removedItem != null) {
        //     removedItem.setPosition(null);
        // }
        return removedItem;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "Tile(" + x + "," + y + ") [" + state.getName() + "]";
    }
}
