package controller.task;

import model.enums.IngredientState;
import model.interfaces.Preparable;
import model.item.utensils.Oven;

public class CookingTask extends Thread {
    private Oven oven;
    private int cookingDuration;
    private int burningDuration;

    public CookingTask(Oven oven, int cookingDuration, int burningDuration) {
        this.oven = oven;
        this.cookingDuration = cookingDuration;
        this.burningDuration = burningDuration;
    }

    @Override
    public void run() {
        try {
            System.out.println("Oven started cooking...");

            Thread.sleep(cookingDuration);

            for (Preparable prep : oven.getContents()) {
                if (prep.getState() == IngredientState.CHOPPED ||
                        prep.getState() == IngredientState.RAW) {
                    prep.cook();
                    if (prep instanceof model.item.Ingredient) {
                        ((model.item.Ingredient) prep).setCooked();
                    }
                }
            }

            oven.setIsCooking(false);
            System.out.println("Oven finished cooking! Take items before they burn!");

            int additionalTime = burningDuration - cookingDuration;
            Thread.sleep(additionalTime);

            if (!oven.isEmpty()) {
                for (Preparable prep : oven.getContents()) {
                    prep.burn();
                }
                System.out.println("WARNING: Items in oven BURNED!");
            }

        } catch (InterruptedException e) {
            System.out.println("Cooking interrupted");
            oven.setIsCooking(false);
        }
    }
}
