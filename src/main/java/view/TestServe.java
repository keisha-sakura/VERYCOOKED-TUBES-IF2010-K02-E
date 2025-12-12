package main.java.view;

import main.java.model.GameManager;
import main.java.model.item.Ingredient;
import main.java.model.item.Plate;
import main.java.model.map.Map;
import main.java.model.map.Position;
import main.java.model.map.Tile;
import main.java.model.map.StationTile;
import main.java.model.station.ServingStation;
import main.java.model.station.PlateStorage;
import main.java.model.chef.Chef;

public class TestServe {
    public static void main(String[] args) {
        GameManager gm = GameManager.getInstance();
        Map map = gm.getGameMap();

        // Find ServingStation and PlateStorage
        ServingStation serving = null;
        PlateStorage storage = null;
        for (int r = 0; r < map.getHeight(); r++) {
            for (int c = 0; c < map.getWidth(); c++) {
                Tile t = map.getTile(r, c);
                if (t instanceof StationTile st) {
                    if (st.getStation() instanceof ServingStation) {
                        serving = (ServingStation) st.getStation();
                    } else if (st.getStation() instanceof PlateStorage) {
                        storage = (PlateStorage) st.getStation();
                    }
                }
            }
        }

        if (serving == null || storage == null) {
            System.out.println("Stations not found.");
            return;
        }

        // Prepare a plate with a simple pizza (adonan, tomat, keju)
        Plate plate = new Plate();
        Ingredient adonan = new Ingredient("adonan"); adonan.cook();
        Ingredient tomat = new Ingredient("tomat"); tomat.cook();
        Ingredient keju = new Ingredient("keju"); keju.cook();
        // Directly add to plate contents to bypass placement rules for the test
        plate.getContents().add(adonan);
        plate.getContents().add(tomat);
        plate.getContents().add(keju);

        // Give plate to active chef and position chef in front of serving station
        Chef chef = gm.getChefs().get(0);
        Position servePos = serving.getPosition();
        // Place chef just above serving station, facing down
        chef.setPosition(new Position(Math.max(0, servePos.getRow()-1), servePos.getCol()));
        chef.setDirection(main.java.model.chef.Direction.DOWN);
        map.getTile(chef.getPosition().getRow(), chef.getPosition().getCol()).setChef(chef);
        chef.setInventory(plate);

        System.out.println("Dirty before: " + storage.getDirtyPlateCount());
        gm.interact();
        System.out.println("Dirty after:  " + storage.getDirtyPlateCount());
    }
}