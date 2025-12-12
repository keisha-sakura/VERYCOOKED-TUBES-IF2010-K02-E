package view;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class MapPage {

    private final Image[] tiles = new Image[14];

    private final int[][] map = {
            {1,2,12,2,3,2,2,2,3,2,2,2,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,11,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,11,1},
            {1,13,13,2,6,2,9,2,5,2,8,2,10,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,0,0,2,0,0,0,1,1,1,1},
            {1,4,0,0,0,0,0,0,0,0,0,0,4,1},
            {1,1,1,1,0,0,0,0,0,0,1,1,1,1},
            {1,1,1,1,2,2,7,2,2,2,1,1,1,1}
    };

    public MapPage() {
        tiles[0]  = new Image(getClass().getResource("/map/tile-floor 1.png").toExternalForm());
        tiles[1]  = new Image(getClass().getResource("/map/tile-wall 1.png").toExternalForm());
        tiles[2]  = new Image(getClass().getResource("/map/station-assembly 1.png").toExternalForm());
        tiles[3]  = new Image(getClass().getResource("/map/station-cutting 1.png").toExternalForm());
        tiles[4]  = new Image(getClass().getResource("/map/station-cooking 1.png").toExternalForm());
        tiles[5]  = new Image(getClass().getResource("/map/station-ingredient-cheese 1.png").toExternalForm());
        tiles[6]  = new Image(getClass().getResource("/map/station-ingredient-chicken 1.png").toExternalForm());
        tiles[7]  = new Image(getClass().getResource("/map/station-ingredient-dough 1.png").toExternalForm());
        tiles[8]  = new Image(getClass().getResource("/map/station-ingredient-sausage 1.png").toExternalForm());
        tiles[9]  = new Image(getClass().getResource("/map/station-ingredient-tomato 1.png").toExternalForm());
        tiles[10] = new Image(getClass().getResource("/map/station-plate 1.png").toExternalForm());
        tiles[11] = new Image(getClass().getResource("/map/station-serve 1.png").toExternalForm());
        tiles[12] = new Image(getClass().getResource("/map/station-trash 1.png").toExternalForm());
        tiles[13] = new Image(getClass().getResource("/map/station-washing 1.png").toExternalForm());
    }

    public GridPane createGrid(double tileSize) {
        GridPane grid = new GridPane();
        grid.setHgap(0);
        grid.setVgap(0);

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[row].length; col++) {
                int id = map[row][col];
                Image img = tiles[id];

                ImageView iv = new ImageView(img);
                iv.setFitWidth(tileSize);
                iv.setFitHeight(tileSize);

                grid.add(iv, col, row);
            }
        }
        return grid;
    }
}
