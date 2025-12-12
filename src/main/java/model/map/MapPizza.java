// File: model/map/MapPizza.java
package model.map;

import model.position.Position;
import model.station.StationFactory;

public class MapPizza extends MapType {
    // Layout Pizza Map - VERIFIED 14 characters per row
    private static final String[] PIZZA_LAYOUT = {
            "XXATAACAACAAXX",  // Row 0: 14 chars ✓
            "X............X",  // Row 1: 14 chars ✓
            "XAVI.....SXXXX",  // Row 2: 14 chars ✓ (V spawn di 2,2)
            "X............X",  // Row 3: 14 chars ✓
            "XWWAIAIAIAIPXX",  // Row 4: 14 chars ✓
            "X............X",  // Row 5: 14 chars ✓
            "XXXXXX....XXXX",  // Row 6: 14 chars ✓
            "XR....V....RXX",  // Row 7: 14 chars ✓ (V spawn di 6,7)
            "XXXXXX......XX",  // Row 8: 14 chars ✓
            "XXXXAAIAAAAXXX"   // Row 9: 14 chars ✓
    };

    public MapPizza() {
        super();
        initialLayout();
    }

    @Override
    protected String[] getLayoutData() {
        return PIZZA_LAYOUT;
    }

    @Override
    protected String getMapName() {
        return "Pizza Map";
    }

    @Override
    protected void initialLayout() {
        System.out.println("Initializing Pizza Map tiles...");

        for(int y = 0; y < getHeight(); y++) {
            String rowLayout = PIZZA_LAYOUT[y];

            // Validasi panjang row
            if (rowLayout.length() != getWidth()) {
                System.err.println("WARNING: Row " + y + " length mismatch! Expected " +
                        getWidth() + ", got " + rowLayout.length());
            }

            for(int x = 0; x < getWidth(); x++) {
                String symbol = String.valueOf(rowLayout.charAt(x));
                tiles[y][x] = new Tile(x, y, symbol);
            }
        }

        System.out.println("Pizza Map tiles initialized: " + getWidth() + "x" + getHeight());
        printIngredientDistribution();
    }

    /**
     * Debug: Print ingredient distribution
     */
    private void printIngredientDistribution() {
        System.out.println("\n=== Ingredient Distribution ===");
        for(int y = 0; y < getHeight(); y++) {
            for(int x = 0; x < getWidth(); x++) {
                Tile tile = tiles[y][x];
                if (tile.getState() == TileState.INGREDIENT_STORAGE) {
                    Position pos = new Position(x, y);
                    String name = StationFactory.getStationName("I", pos);
                    System.out.println("  Position (" + x + "," + y + "): " + name);
                }
            }
        }
        System.out.println("===============================\n");
    }
}
