
package model.station;

import model.position.*;
import model.chef.*;
import model.enums.IngredientState;
import model.interfaces.Preparable;
import model.item.Item;
import model.item.utensils.Plate;
import model.order.OrderManager;
import model.recipe.Recipe;
import model.recipe.RecipeBook;
import controller.GameController;
import java.util.ArrayList;
import java.util.List;
import model.enums.*;

public class ServingCounter extends Station {
    private OrderManager orderManager;
    private GameController gameController;

    public ServingCounter(Position position) {
        super(position, StationType.SERVING);
    }

    public void setOrderManager(OrderManager om) {
        this.orderManager = om;
    }

    public void setGameController(GameController gc) {
        this.gameController = gc;
    }

    @Override
    public void interact(Chef chef) {
        Item heldItem = chef.getInventory();

        if (!(heldItem instanceof Plate)) {
            return;
        }

        Plate plate = (Plate) heldItem;
        if (plate.isDirty() || plate.isEmpty()) {
            return;
        }

        // Validasi dish = recipe
        List<String> ingredientNames = new ArrayList<>();
        List<IngredientState> states = new ArrayList<>();

        for (Preparable prep : plate.getContents()) {
            ingredientNames.add(prep.getName());
            states.add(prep.getState());
        }

        RecipeBook recipeBook = RecipeBook.getInstance();
        Recipe matchedRecipe = recipeBook.findMatchingRecipe(ingredientNames, states);

        if (matchedRecipe != null) {
            // Order berhasil
            boolean completed = orderManager.completeOrder(matchedRecipe);
            if (completed && gameController != null) {
                gameController.addScore(matchedRecipe.getReward());
                System.out.println("✓ Order completed: " + matchedRecipe.getName() +
                        " (+" + matchedRecipe.getReward() + " pts)");
            }
        } else {
            // Order gagal
            orderManager.failOrder();
            if (gameController != null) {
                gameController.addScore(-50); // Penalty
                System.out.println("✗ Wrong dish served! (-50 pts)");
            }
        }

        chef.setInventory(null);
        if (gameController != null) {
            gameController.returnPlateToStorage(plate);
        } else {
            plate.setDirty(true);
        }
    }

    @Override
    public boolean canInteract(Chef chef) {
        return chef.getInventory() instanceof Plate;
    }

    @Override
    public String getInteractionPrompt() {
        return "Press V to serve dish";
    }
}