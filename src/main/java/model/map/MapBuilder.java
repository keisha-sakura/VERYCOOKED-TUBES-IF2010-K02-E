package model.map;

import model.station.*;
import model.item.*;
import model.*;
import model.item.*;

public class MapBuilder {
    private static int ingredientCounter = 0;
    private static final String[] INGREDIENT_TYPES = {"Adonan", "Tomat", "Keju", "Sosis", "Ayam"};

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
            case '1' : return new StationTile(pos, new IngredientStation(pos, "ayam"));
            case '2' : return new StationTile(pos, new IngredientStation(pos, "tomat"));
            case '3' : return new StationTile(pos, new IngredientStation(pos, "keju"));
            case '4' : return new StationTile(pos, new IngredientStation(pos, "sosis"));
            case '5' : return new StationTile(pos, new IngredientStation(pos, "adonan"));
            case 'P' : return new StationTile(pos, new PlateStorage(pos, 50));

            default : throw new IllegalArgumentException();
        }
    }

<<<<<<< HEAD
    public static Map buildMap(){
        char[][] mapmatrix = {
                {'X', 'A', 'T', 'A', 'C', 'A', 'A', 'A', 'C', 'A', 'A', 'A', 'X', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'X', 'X'},
                {'X', '.', '.', '.', '.', '.', 'A', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', 'W', 'W', 'A', '1', 'A', '2', 'A', '3', 'A', '4', 'A', 'P', 'X'},
                {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'S', 'X'},
                {'X', 'X', 'X', 'X', '.', '.', 'A', '.', '.', '.', 'X', 'X', 'X', 'X'},
                {'X', 'R', '.', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'R', 'X'},
                {'X', 'X', 'X', 'X', '.', '.', '.', '.', '.', '.', 'X', 'X', 'X', 'X'},
                {'X', 'X', 'X', 'X', 'A', 'A', '5', 'A', 'A', 'A', 'X', 'X', 'X', 'X'}};

        return mapImporter(mapmatrix);
=======
    private static String getDefaultIngredientType() {
        String type = INGREDIENT_TYPES[ingredientCounter % INGREDIENT_TYPES.length];
        ingredientCounter++;
        return type;
>>>>>>> 267a7b3ce40c0ba94c7b9bc561b01d5a6ece47c8
    }
}