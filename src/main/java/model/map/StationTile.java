package model.map;

import model.station.*;

public class StationTile extends Tile{
    private final Station station;

    public StationTile(Position pos, Station station){
        super(pos);
        this.setWalkable(false);
        this.station = station;
    }

    public Station getStation(){
        return station;
    }
}
