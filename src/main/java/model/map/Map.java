package model.map;

import model.station.*;

public class Map {
    private Tile[][] tiles;
    private int width;
    private int height;

    public Map(int height, int width){
        this.tiles = new Tile[height][width];
        this.width = width;
        this.height = height;

        for(int row = 0; row < height; row++){
            for(int col = 0; col < width; col++){
                Position pos = new Position(row, col);
                tiles[row][col] = new FloorTile(pos);
            }
        }
    }

    public void setTiles(int row, int col, Tile tile){
        this.tiles[row][col] = tile;
    }

    public Tile getTile(int row, int col){
        if (row < 0 || row >= height || col < 0 || col >= width) {
            return null;
        }
        return tiles[row][col];
    }

    public Tile getTile(Position pos){
        return tiles[pos.getRow()][pos.getCol()];
    }

    public void displayMap(){
        for(int i = 0; i < this.height; i++){
            System.out.println("+--+--+--+--+--+--+--+--+--+--+--+--+--+--+");
            System.out.printf("|");
            for(int j = 0; j < this.width; j++){
                if(tiles[i][j] instanceof WallTile){
                    System.out.printf(" X|");
                }

                if(tiles[i][j] instanceof FloorTile && !tiles[i][j].isOccupied()){
                    System.out.printf(" .|");
                }
                if(tiles[i][j] instanceof FloorTile && tiles[i][j].isOccupied()){
                    System.out.printf(" H|");
                }

                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof CuttingStation){
                    System.out.printf(" C|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof CookingStation){
                    System.out.printf(" R|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof AssemblyStation){
                    System.out.printf(" A|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof IngredientStation){
                    System.out.printf(" I|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof PlateStorage){
                    System.out.printf(" P|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof ServingStation){
                    System.out.printf(" S|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof TrashStation){
                    System.out.printf(" T|");
                }
                if(tiles[i][j] instanceof StationTile && ((StationTile) tiles[i][j]).getStation() instanceof WashingStation) {
                    System.out.printf(" W|");
                }
            }
            System.out.println();
        }
        System.out.println("+--+--+--+--+--+--+--+--+--+--+--+--+--+--+");
    }
}