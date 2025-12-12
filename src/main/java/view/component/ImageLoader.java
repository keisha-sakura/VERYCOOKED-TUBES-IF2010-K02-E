package view.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.map.TileState;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private final Image[] tileImages = new Image[14];
    private final Image[] chefSprites = new Image[2];
    private final Map<String, Image> itemImages = new HashMap<>();

    public ImageLoader() {
        loadAllImages();
    }

    private void loadAllImages() {
        loadTileImages();
        loadChefSprites();
        loadItemImages();
    }

    private void loadTileImages() {
        try {
            tileImages[0] = loadImage("/map/tile-floor.png");
            tileImages[1] = loadImage("/map/tile-wall.png");
            tileImages[2] = loadImage("/map/station-assembly.png");
            tileImages[3] = loadImage("/map/station-cutting.png");
            tileImages[4] = loadImage("/map/station-cooking.png");
            tileImages[5] = loadImage("/map/station-ingredient-cheese.png");
            tileImages[6] = loadImage("/map/station-ingredient-chicken.png");
            tileImages[7] = loadImage("/map/station-ingredient-dough.png");
            tileImages[8] = loadImage("/map/station-ingredient-sausage.png");
            tileImages[9] = loadImage("/map/station-ingredient-tomato.png");
            tileImages[10] = loadImage("/map/station-plate.png");
            tileImages[11] = loadImage("/map/station-serve.png");
            tileImages[12] = loadImage("/map/station-trash 1.png");
            tileImages[13] = loadImage("/map/station-washing.png");

            System.out.println("✓ Tile images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading tiles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadChefSprites() {
        try {
            chefSprites[0] = loadImage("/chefPikachu/pikachu-front-1.png");
            chefSprites[1] = loadImage("/chefJig/jigglypuff-front-1.png");

            System.out.println("✓ Chef sprites loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading chefs: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadItemImages() {
        try {
            itemImages.put("Plate", loadImage("/item/plate.png"));
            itemImages.put("Dirty Plate", loadImage("/item/plate-dirty.png"));

            itemImages.put("Adonan", loadImage("/item/dough-raw-export.png"));
            itemImages.put("Adonan (Chopped)", loadImage("/item/dough-chopped.png"));

            itemImages.put("Tomat", loadImage("/item/tomato-raw.png"));
            itemImages.put("Tomat (Chopped)", loadImage("/item/tomato-chopped.png"));

            itemImages.put("Keju", loadImage("/item/cheese-raw.png"));
            itemImages.put("Keju (Chopped)", loadImage("/item/cheese-chopped.png"));

            itemImages.put("Sosis", loadImage("/item/sausage-raw.png"));
            itemImages.put("Sosis (Chopped)", loadImage("/item/sausage-chopped.png"));
            itemImages.put("Sosis (Cooked)", loadImage("/item/sausage-cooked.png"));

            itemImages.put("Ayam", loadImage("/item/chicken-raw.png"));
            itemImages.put("Ayam (Chopped)", loadImage("/item/chicken-chopped.png"));
            itemImages.put("Ayam (Cooked)", loadImage("/item/chicken-cooked.png"));

            System.out.println("✓ Item images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading items: " + e.getMessage());
            e.printStackTrace();
            createItemPlaceholders();
        }
    }

    private Image loadImage(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    public Image getTileImage(TileState state, int x, int y) {
        int id = getTileId(state, x, y);
        return tileImages[id];
    }

    public Image getChefSprite(int index) {
        return chefSprites[index];
    }

    public Image getItemImage(String name) {
        Image img = itemImages.get(name);
        if (img == null) {
            img = createPlaceholder(name);
            itemImages.put(name, img);
        }
        return img;
    }

    private int getTileId(TileState state, int x, int y) {
        if (state == TileState.INGREDIENT_STORAGE) {
            if (y == 2 && x == 3) return 7;
            if (y == 4) {
                if (x == 4) return 9;
                if (x == 6) return 5;
                if (x == 8) return 8;
                if (x == 10) return 6;
            }
            if (y == 9 && x == 6) return 7;
            return 7;
        }

        return switch (state) {
            case WALL -> 1;
            case ASSEMBLY_STATION -> 2;
            case CUTTING_STATION -> 3;
            case COOKING_STATION -> 4;
            case PLATE_STORAGE -> 10;
            case SERVING_COUNTER -> 11;
            case TRASH_STATION -> 12;
            case WASHING_STATION -> 13;
            default -> 0;
        };
    }

    private void createItemPlaceholders() {
        String[] itemNames = {
                "Plate", "Dirty Plate",
                "Adonan", "Adonan (Chopped)",
                "Tomat", "Tomat (Chopped)",
                "Keju", "Keju (Chopped)",
                "Sosis", "Sosis (Chopped)", "Sosis (Cooked)",
                "Ayam", "Ayam (Chopped)", "Ayam (Cooked)"
        };

        for (String name : itemNames) {
            if (!itemImages.containsKey(name)) {
                itemImages.put(name, createPlaceholder(name));
            }
        }
    }

    private Image createPlaceholder(String name) {
        Canvas canvas = new Canvas(32, 32);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Color color = getPlaceholderColor(name);

        gc.setFill(color);
        gc.fillOval(4, 4, 24, 24);
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);
        gc.strokeOval(4, 4, 24, 24);

        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font("Arial", 10));
        String label = name.substring(0, Math.min(2, name.length()));
        gc.fillText(label, 10, 20);

        javafx.scene.SnapshotParameters params = new javafx.scene.SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return canvas.snapshot(params, null);
    }

    private Color getPlaceholderColor(String name) {
        if (name.contains("Plate")) return Color.WHITE;
        if (name.contains("Dirty")) return Color.GRAY;
        if (name.contains("Adonan")) return Color.WHEAT;
        if (name.contains("Tomat")) return Color.RED;
        if (name.contains("Keju")) return Color.YELLOW;
        if (name.contains("Sosis")) return Color.PINK;
        if (name.contains("Ayam")) return Color.LIGHTYELLOW;
        if (name.contains("Cooked")) return Color.BROWN;
        if (name.contains("Chopped")) return Color.ORANGE;
        return Color.LIGHTGRAY;
    }
}
