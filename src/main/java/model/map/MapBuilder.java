package main.java.model.map;

import main.java.model.station.*;
import main.java.model.*;
import main.java.item.*


public class MapBuilder {

    public static Map mapImporter(char[][] matrixmap) {
        int height = matrixmap.length;
        int width = matrixmap[0].length;

        Map buildedMap = new Map(height, width);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {

                char symbol = matrixmap[r][c];
                Position pos = new Position(r, c);

                Tile tile = createTile(symbol, pos);
                buildedMap.setTiles(r, c, tile);
            }
        }

        return buildedMap;
    }

    public static Tile createTile(char symbol, Position pos){
        switch(symbol){
            case '.' : return new FloorTile(pos);

            case 'X' : return new WallTile(pos);

            case 'A' : return new StationTile(pos, new AssemblyStation(pos));
            case 'T' : return new StationTile(pos, new TrashStation(pos));
            case 'C' : return new StationTile(pos, new CuttingStation(pos));
            case 'R' : return new StationTile(pos, new CookingStation(pos, new Oven()));
            case 'S' : return new StationTile(pos, new ServingStation(pos));
            case 'W' : return new StationTile(pos, new WashingStation(pos));
            case 'I' : return new StationTile(pos, new IngredientStation(pos, getDefaultIngredientType()));
            case 'P' : return new StationTile(pos, new PlateStorage(pos, 50));

            default : throw new IllegalArgumentException();
        }
    }

    public static Map buildMap(){
        char[][] mapmatrix = {
                {'X', 'A', 'T', 'A', 'C', 'A', 'A', 'A', 'C', 'A', 'A', 'A', 'X', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'X', 'X'},
                {'X', '.', '.', '.', '.', '.', 'A', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', 'W', 'W', 'A', 'I', 'A', 'I', 'A', 'I', 'A', 'I', 'A', 'P', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', 'X', 'X', 'X', '.', '.', 'A', '.', '.', '.', 'X', 'X', 'X', 'X'},
                {'X', 'R', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'R', 'X'},
                {'X', 'X', 'X', 'X', '.', '.', '.', '.', '.', '.', 'X', 'X', 'X', 'X'},
                {'X', 'X', 'X', 'X', 'A', 'A', 'I', 'A', 'A', 'A', 'X', 'X', 'X', 'X'}};

        return mapImporter(mapmatrix);
    }
}