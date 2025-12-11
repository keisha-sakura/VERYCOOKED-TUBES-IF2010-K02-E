package main.java.model.map;

import main.java.model.station.*;


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

            case 'A' : return new StationTile(pos, new AssemblyStation());
            case 'T' : return new StationTile(pos, new TrashStation());
            case 'C' : return new StationTile(pos, new CuttingStation());
            case 'R' : return new StationTile(pos, new CookingStation());
            case 'S' : return new StationTile(pos, new ServingStation());
            case 'W' : return new StationTile(pos, new WashingStation());
            case 'I' : return new StationTile(pos, new IngredientStation());
            case 'P' : return new StationTile(pos, new PlateStorage(50));

            default : throw new IllegalArgumentException();
        }
    }
}