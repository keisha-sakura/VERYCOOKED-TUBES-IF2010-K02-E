// File: model/station/StationFactory.java
package model.station;

import model.position.Position;
import model.item.ingredients.*;

public class StationFactory {

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

    private static IngredientStorage createIngredientStorage(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

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

        if (y == 9 && x == 6) {
            return new IngredientStorage(pos, Dough.class);
        }

        return new IngredientStorage(pos, Dough.class);
    }

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

    private static String getIngredientStorageName(Position pos) {
        int x = pos.getX();
        int y = pos.getY();

        if (y == 4) {
            if (x == 4) return "Ingredient Storage (Chicken)";
            if (x == 6) return "Ingredient Storage (Tomato)";
            if (x == 8) return "Ingredient Storage (Cheese)";
            if (x == 10) return "Ingredient Storage (Sausage)";
        }

        if (y == 9 && x == 6) return "Ingredient Storage (Dough)";

        return "Ingredient Storage";
    }

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
