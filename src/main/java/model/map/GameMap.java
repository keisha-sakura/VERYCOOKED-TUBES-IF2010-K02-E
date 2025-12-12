package model.map;

import model.position.*;
import model.chef.*;
import model.enums.StationType;
import model.item.ingredients.*;
import model.station.*;
import java.util.*;

public class GameMap {
    private static final int WIDTH = 14;
    private static final int HEIGHT = 10;

    private char[][] grid;
    private Map<Position, Station> stations;
    private List<Position> walkableTiles;
    private List<Position> chefSpawnPoints;

    public GameMap() {
        this.grid = new char[HEIGHT][WIDTH];
        this.stations = new HashMap<>();
        this.walkableTiles = new ArrayList<>();
        this.chefSpawnPoints = new ArrayList<>();
        initializePizzaMap();
    }

    private void initializePizzaMap() {
        String[] mapLayout = {
                "XXATAACAACAAXX",
                "X............X",
                "XAVII....SXXXX",
                "X............X",
                "XWWAIAIAIAIPX",
                "X............X",
                "XXXXXX....XXXX",
                "XR....V....RX",
                "XXXXXX......XX",
                "XXXXAAIAAAAXXX"
        };

        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                char c = mapLayout[y].charAt(x);
                grid[y][x] = c;
                Position pos = new Position(x, y);

                StationType type = StationType.fromChar(c);

                switch (type) {
                    case CUTTING:
                        stations.put(pos, new CuttingStation(pos));
                        break;
                    case COOKING:
                        stations.put(pos, new CookingStation(pos));
                        break;
                    case ASSEMBLY:
                        stations.put(pos, new AssemblyStation(pos));
                        break;
                    case SERVING:
                        stations.put(pos, new ServingCounter(pos));
                        break;
                    case WASHING:
                        stations.put(pos, new WashingStation(pos));
                        break;
                    case INGREDIENT_STORAGE:
                        stations.put(pos, createIngredientStorage(pos, x, y));
                        break;
                    case PLATE_STORAGE:
                        stations.put(pos, new PlateStorage(pos, 3)); // 3 plate awal
                        break;
                    case TRASH:
                        stations.put(pos, new TrashStation(pos));
                        break;
                    case WALKABLE:
                        walkableTiles.add(pos);
                        break;
                    case CHEF_SPAWN:
                        chefSpawnPoints.add(pos);
                        walkableTiles.add(pos);
                        grid[y][x] = '.';
                        break;
                }
            }
        }
    }

    private IngredientStorage createIngredientStorage(Position pos, int x, int y) {

        if (y == 2 && x >= 3 && x <= 4) {
            return new IngredientStorage(pos, Dough.class);
        } else if (y == 4 && x == 4) {
            return new IngredientStorage(pos, Tomato.class);
        } else if (y == 4 && x == 6) {
            return new IngredientStorage(pos, Cheese.class);
        } else if (y == 4 && x == 8) {
            return new IngredientStorage(pos, Sausage.class);
        } else if (y == 4 && x == 10) {
            return new IngredientStorage(pos, Chicken.class);
        } else if (y == 9 && x >= 5 && x <= 6) {
            return new IngredientStorage(pos, Dough.class);
        }
        return new IngredientStorage(pos, Dough.class);
    }

    public int getWidth() { return WIDTH; }
    public int getHeight() { return HEIGHT; }
    public char[][] getGrid() { return grid; }

    public Station getStationAt(Position pos) {
        return stations.get(pos);
    }

    public Map<Position, Station> getAllStations() {
        return new HashMap<>(stations);
    }

    public List<Position> getChefSpawnPoints() {
        return new ArrayList<>(chefSpawnPoints);
    }

    public boolean isWalkable(Position pos) {
        if (!pos.isInBounds(WIDTH, HEIGHT)) return false;
        char tile = grid[pos.getY()][pos.getX()];
        return tile == '.' || tile == 'V';
    }

    public boolean hasChefAt(Position pos, List<Chef> chefs) {
        for (Chef chef : chefs) {
            if (chef.getPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    public void printMap() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                System.out.print(grid[y][x]);
            }
            System.out.println();
        }
    }
}
