// File: model/map/Map.java
package model.map;

import model.item.*;
import model.position.Position;
import model.chef.Chef;
import model.station.Station;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Map {
    private static final int height = 10;
    private static final int width = 14;
    private final Tile[][] tiles;
    private final MapType mapConfig;

    public Map(MapType mapConfig) {
        this.mapConfig = mapConfig;
        this.tiles = mapConfig.getTiles();
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            return tiles[y][x];
        }
        return null;
    }

    /**
     * Get tile dengan Position
     */
    public Tile getTile(Position pos) {
        return getTile(pos.getX(), pos.getY());
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }

        Tile targetTile = tiles[y][x];

        if(targetTile.isWall(x, y) || targetTile.isStation(x, y)) {
            return false;
        }
        return true;
    }

    /**
     * isWalkable dengan Position
     */
    public boolean isWalkable(Position pos) {
        return isWalkable(pos.getX(), pos.getY());
    }

    public void placeItemOnMap(int x, int y, Item item) {
        getTile(x, y).setItem(item);
    }

    public Item removeItemOnMap(int x, int y) {
        return getTile(x, y).removeItem();
    }

    // ========== Method untuk GameController ==========

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Get grid sebagai char[][] untuk rendering
     */
    public char[][] getGrid() {
        char[][] grid = new char[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = tiles[y][x].getSymbol().charAt(0);
            }
        }
        return grid;
    }

    /**
     * Get station di posisi tertentu
     */
    public Station getStationAt(Position pos) {
        Tile tile = getTile(pos.getX(), pos.getY());
        if (tile != null) {
            return tile.getStation();
        }
        return null;
    }

    /**
     * Get semua stations
     */
    public HashMap<Position, Station> getAllStations() {
        HashMap<Position, Station> stations = new HashMap<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];
                Station station = tile.getStation();
                if (station != null) {
                    Position pos = new Position(x, y);
                    stations.put(pos, station);
                }
            }
        }

        return stations;
    }

    /**
     * Get chef spawn points
     */
    public List<Position> getChefSpawnPoints() {
        List<Position> spawnPoints = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Tile tile = tiles[y][x];
                if (tile.isChefSpawn()) {
                    spawnPoints.add(new Position(x, y));
                }
            }
        }

        return spawnPoints;
    }

    /**
     * Check apakah ada chef di posisi
     */
    public boolean hasChefAt(Position pos, List<Chef> chefs) {
        for (Chef chef : chefs) {
            if (chef.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Print map untuk debugging
     */
    public void printMap() {
        System.out.println("\n=== MAP LAYOUT ===");
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                System.out.print(tiles[y][x].getSymbol());
            }
            System.out.println();
        }
        System.out.println("==================\n");
    }
}
