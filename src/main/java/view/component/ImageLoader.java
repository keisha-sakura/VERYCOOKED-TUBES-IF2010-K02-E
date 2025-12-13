package view.component;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import model.enums.Direction;
import model.map.TileState;

import java.util.HashMap;
import java.util.Map;

public class ImageLoader {
    private final Image[] tileImages = new Image[14];
    private final Map<String, Image> chefSprites = new HashMap<>();
    private final Map<String, Image> pizzaImages = new HashMap<>();
    private final Map<String, Image> itemImages = new HashMap<>();
    private final Map<String, Image> orderImages = new HashMap<>();

    public ImageLoader() {
        loadAllImages();
    }

    private void loadAllImages() {
        loadTileImages();
        loadChefSprites();
        loadItemImages();
        loadPizzaImages();
        loadOrderImages();
    }

    private void loadOrderImages() {
        try {
            orderImages.put("Pizza Ayam", loadImage("/order/ayam.png"));
            orderImages.put("Pizza Margherita", loadImage("/order/margherita.png"));
            orderImages.put("Pizza Sosis", loadImage("/order/sosis.png"));

            System.out.println("✓ Order images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading order images: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Image getOrderImage(String recipeName) {
        Image img = orderImages.get(recipeName);
        if (img == null) {
            System.err.println("Missing order image: " + recipeName);
            return createPlaceholder("Order");
        }
        return img;
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
            for (int chefIndex = 1; chefIndex <= 2; chefIndex++) {
                String[] directions = {"down", "left", "right", "up"};

                for (String dir : directions) {
                    for (int frame = 0; frame < 4; frame++) {
                        String key = String.format("chef%d-%s-%d", chefIndex, dir, frame);
                        String path = String.format("/chef/chef%d/%s-%d.png", chefIndex, dir, frame);

                        Image sprite = loadImage(path);
                        if (sprite != null) {
                            chefSprites.put(key, sprite);
                        }
                    }
                }
            }

            System.out.println("✓ Chef sprites loaded (" + chefSprites.size() + " frames)");
        } catch (Exception e) {
            System.err.println("ERROR loading chef sprites: " + e.getMessage());
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

    private void loadPizzaImages() {
        try {
            pizzaImages.put("Pizza Margherita", loadImage("/pizza/pizza-margherita-cooked.png"));
            pizzaImages.put("Pizza Sosis", loadImage("/pizza/pizza-sosis-cooked.png"));
            pizzaImages.put("Pizza Ayam", loadImage("/pizza/pizza-ayam-cooked.png"));

            pizzaImages.put("Pizza Margherita (Burned)", loadImage("/pizza/pizza-margherita-burned.png"));
            pizzaImages.put("Pizza Sosis (Burned)", loadImage("/pizza/pizza-sosis-burned.png"));
            pizzaImages.put("Pizza Ayam (Burned)", loadImage("/pizza/pizza-ayam-burned.png"));

            pizzaImages.put("Pizza Margherita (Raw)", loadImage("/pizza/pizza-margherita.png"));
            pizzaImages.put("Pizza Sosis (Raw)", loadImage("/pizza/pizza-sosis.png"));
            pizzaImages.put("Pizza Ayam (Raw)", loadImage("/pizza/pizza-ayam.png"));

            System.out.println("✓ Pizza images loaded");
        } catch (Exception e) {
            System.err.println("ERROR loading pizzas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Image loadImage(String path) {
        try {
            Image img = new Image(getClass().getResourceAsStream(path));
            if (img.isError()) {
                System.err.println("Failed to load: " + path);
                return null;
            }
            return img;
        } catch (Exception e) {
            System.err.println("Failed to load: " + path + " - " + e.getMessage());
            return null;
        }
    }

    public Image getTileImage(TileState state, int x, int y) {
        int id = getTileId(state, x, y);
        return tileImages[id];
    }

    public Image getChefSprite(int chefIndex, Direction direction, int frame) {
        String dirName = direction.name().toLowerCase();
        String key = String.format("chef%d-%s-%d", chefIndex + 1, dirName, frame);

        Image sprite = chefSprites.get(key);
        if (sprite == null) {
            System.err.println("Missing sprite: " + key);
            return createPlaceholder("Chef");
        }

        return sprite;
    }

    public Image getItemImage(String name) {
        Image img = itemImages.get(name);
        if (img == null) {
            img = createPlaceholder(name);
            itemImages.put(name, img);
        }
        return img;
    }

    public Image getPizzaImage(String pizzaName) {
        Image img = pizzaImages.get(pizzaName);
        if (img == null) {
            return createPlaceholder(pizzaName);
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
