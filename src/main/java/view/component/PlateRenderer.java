package view.component;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.enums.IngredientState;
import model.interfaces.Preparable;
import model.item.Ingredient;
import model.item.utensils.Plate;
import model.recipe.RecipeBook;
import model.recipe.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PlateRenderer {
    private ImageLoader imageLoader;
    private static final double ITEM_SIZE = 40;

    public PlateRenderer(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    public StackPane renderPlateWithContents(Plate plate, double size) {
        StackPane plateStack = new StackPane();

        // 1. Base plate image
        Image plateImg = plate.isDirty()
                ? imageLoader.getItemImage("Dirty Plate")
                : imageLoader.getItemImage("Plate");

        ImageView plateView = new ImageView(plateImg);
        plateView.setFitWidth(size);
        plateView.setFitHeight(size);
        plateView.setPreserveRatio(true);

        plateStack.getChildren().add(plateView);

        // 2. Check if contents form a complete pizza
        if (!plate.isEmpty()) {
            String pizzaName = checkForCompletePizza(plate);

            if (pizzaName != null) {
                // Render as complete pizza
                renderCompletePizza(plateStack, pizzaName, plate, size);
            } else {
                // Render individual ingredients
                renderIndividualIngredients(plateStack, plate, size);
            }
        }

        return plateStack;
    }

    private String checkForCompletePizza(Plate plate) {
        Set<Preparable> contents = plate.getContents();

        List<String> names = new ArrayList<>();
        List<IngredientState> states = new ArrayList<>();

        for (Preparable prep : contents) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;
                names.add(ing.getName());
                states.add(ing.getState());
            }
        }

        Recipe matchedRecipe = RecipeBook.getInstance().findMatchingRecipe(names, states);
        return matchedRecipe != null ? matchedRecipe.getName() : null;
    }

    private void renderCompletePizza(StackPane plateStack, String pizzaName, Plate plate, double size) {
        // Check if all ingredients are COOKED
        boolean allCooked = true;
        boolean anyBurned = false;

        for (Preparable prep : plate.getContents()) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;
                if (ing.getState() == IngredientState.BURNED) {
                    anyBurned = true;
                    break;
                }
                if (ing.getState() != IngredientState.COOKED) {
                    allCooked = false;
                }
            }
        }

        String imageName;
        if (anyBurned) {
            imageName = pizzaName + " (Burned)";
        } else if (allCooked) {
            imageName = pizzaName;
        } else {
            imageName = pizzaName + " (Raw)";
        }

        Image pizzaImg = imageLoader.getPizzaImage(imageName);
        ImageView pizzaView = new ImageView(pizzaImg);
        pizzaView.setFitWidth(size * 0.7);
        pizzaView.setFitHeight(size * 0.7);
        pizzaView.setPreserveRatio(true);

        plateStack.getChildren().add(pizzaView);
    }

    private void renderIndividualIngredients(StackPane plateStack, Plate plate, double size) {
        int index = 0;
        double offset = 5;

        for (Preparable prep : plate.getContents()) {
            if (prep instanceof Ingredient) {
                Ingredient ing = (Ingredient) prep;

                String itemKey = getIngredientImageKey(ing);
                Image ingImg = imageLoader.getItemImage(itemKey);

                ImageView ingView = new ImageView(ingImg);
                ingView.setFitWidth(size * 0.4);
                ingView.setFitHeight(size * 0.4);
                ingView.setPreserveRatio(true);

                // Stack dengan slight offset
                double xOffset = (index % 2 == 0) ? -offset : offset;
                double yOffset = (index / 2) * offset;

                ingView.setTranslateX(xOffset);
                ingView.setTranslateY(yOffset);

                plateStack.getChildren().add(ingView);
                index++;
            }
        }
    }

    private String getIngredientImageKey(Ingredient ing) {
        String baseName = ing.getName();
        IngredientState state = ing.getState();

        switch (state) {
            case RAW:
                return baseName;
            case CHOPPED:
                return baseName + " (Chopped)";
            case COOKED:
                return baseName + " (Cooked)";
            case BURNED:
                return baseName + " (Burned)";
            default:
                return baseName;
        }
    }
}
