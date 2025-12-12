// File: model/station/StationFactory.java
package model.station;

import model.position.Position;
import model.item.ingredients.*;

/**
 * Factory Pattern untuk create station objects
 */
public class StationFactory {

    /**
     * Create station berdasarkan symbol dan position
     */
    public static Station createStationObject(Position pos, String symbol) {
        return switch(symbol) {
            case "C" -> new CuttingStation(pos);
            case "R" -> new CookingStation(pos);
            case "A" -> new AssemblyStation(pos);
            case "S" -> new ServingCounter(pos);
            case "W" -> new WashingStation(pos);
            case "I" -> createIngredientStorage(pos);
            case "P" -> new PlateStorage(pos, 3);
            case "T" -> new TrashStation(pos);
            default -> null;
        };
    }

    /**
     * Create IngredientStorage dengan ingredient yang tepat
     * berdasarkan posisi di Pizza Map
     */
    private static IngredientStorage createIngredientStorage(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        // Pizza Map Ingredient Distribution
        // Berdasarkan layout:
        // Row 2: XAVI.....SXXXX -> I di (3,2)
        // Row 4: XWWAIAIAIAIPXX -> I di (4,4), (6,4), (8,4), (10,4)
        // Row 9: XXXXAAIAAAAXXX -> I di (6,9)

        // Row 2, X=3: Dough (Adonan Pizza)
        if (y == 2 && x == 3) {
            return new IngredientStorage(pos, Dough.class);
        }

        // Row 4: Various ingredients
        if (y == 4) {
            if (x == 4) {
                return new IngredientStorage(pos, Tomato.class);
            }
            if (x == 6) {
                return new IngredientStorage(pos, Cheese.class);
            }
            if (x == 8) {
                return new IngredientStorage(pos, Sausage.class);
            }
            if (x == 10) {
                return new IngredientStorage(pos, Chicken.class);
            }
        }

        // Row 9, X=6: Dough
        if (y == 9 && x == 6) {
            return new IngredientStorage(pos, Dough.class);
        }

        // Default: Dough (jika ada I lain yang tidak terdaftar)
        return new IngredientStorage(pos, Dough.class);
    }

    /**
     * Get descriptive name untuk station
     * Berguna untuk UI/debugging
     */
    public static String getStationName(String symbol, Position position) {
        return switch(symbol) {
            case "C" -> "Cutting Station";
            case "R" -> "Cooking Station (Oven)";
            case "A" -> "Assembly Station";
            case "S" -> "Serving Counter";
            case "W" -> "Washing Station";
            case "I" -> getIngredientStorageName(position);
            case "P" -> "Plate Storage";
            case "T" -> "Trash Station";
            default -> "Unknown Station";
        };
    }

    /**
     * Get ingredient storage name berdasarkan posisi
     */
    private static String getIngredientStorageName(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        if (y == 2 && x == 3) return "Ingredient Storage (Dough)";

        if (y == 4) {
            if (x == 4) return "Ingredient Storage (Tomato)";
            if (x == 6) return "Ingredient Storage (Cheese)";
            if (x == 8) return "Ingredient Storage (Sausage)";
            if (x == 10) return "Ingredient Storage (Chicken)";
        }

        if (y == 9 && x == 6) return "Ingredient Storage (Dough)";

        return "Ingredient Storage";
    }

    /**
     * Get ingredient type berdasarkan posisi
     * Berguna untuk debugging/display
     */
    public static Class<?> getIngredientType(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        if (y == 2 && x == 3) return Dough.class;

        if (y == 4) {
            if (x == 4) return Tomato.class;
            if (x == 6) return Cheese.class;
            if (x == 8) return Sausage.class;
            if (x == 10) return Chicken.class;
        }

        if (y == 9 && x == 6) return Dough.class;

        return Dough.class;
    }
}
