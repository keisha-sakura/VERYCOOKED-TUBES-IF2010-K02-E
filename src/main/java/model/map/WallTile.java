package main.java.model.map;

public class WallTile extends Tile{

    public WallTile(Position pos){
        super(pos);
        this.setWalkable(false);
    }
}
