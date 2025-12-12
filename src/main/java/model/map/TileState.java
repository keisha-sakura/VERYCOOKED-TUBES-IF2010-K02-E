// File: model/map/TileState.java
package model.map;

public enum TileState {
    WALL("X"),
    CUTTING_STATION("C"),
    COOKING_STATION("R"),
    ASSEMBLY_STATION("A"),
    SERVING_COUNTER("S"),
    WASHING_STATION("W"),
    INGREDIENT_STORAGE("I"),
    PLATE_STORAGE("P"),
    TRASH_STATION("T"),
    WALKABLE("."),
    SPAWN("V");

    private final String symbol;

    TileState(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getName() {
        return this.name();
    }

    /**
     * Get TileState dari symbol
     */
    public static String fromSymbol(String symbol) {
        for(TileState state : TileState.values()) {
            if(state.symbol.equals(symbol)) {
                return state.getName();
            }
        }
        return WALKABLE.getName(); // Default ke WALKABLE
    }

    /**
     * Check apakah symbol adalah station
     */
    public static boolean isStation(String symbol) {
        TileState state = getState(symbol);
        return state != WALL && state != WALKABLE && state != SPAWN;
    }

    /**
     * Get TileState enum dari symbol
     */
    public static TileState getState(String symbol) {
        for(TileState state : TileState.values()) {
            if(state.symbol.equals(symbol)) {
                return state;
            }
        }
        return WALKABLE;
    }

    /**
     * Check apakah walkable
     */
    public boolean isWalkable() {
        return this == WALKABLE || this == SPAWN;
    }

    /**
     * Check apakah wall
     */
    public boolean isWall() {
        return this == WALL;
    }
}
