package controller.task;

import model.chef.*;
import model.enums.ChefAction;
import model.item.Ingredient;

public class ChoppingTask extends Thread {
    private Chef chef;
    private Ingredient ingredient;
    private int duration;
    private volatile boolean interrupted = false;

    public ChoppingTask(Chef chef, Ingredient ingredient, int duration) {
        this.chef = chef;
        this.ingredient = ingredient;
        this.duration = duration;
    }

    @Override
    public void run() {
        chef.setBusy(true);
        chef.setCurrentAction(ChefAction.CHOPPING);

        System.out.println(chef.getName() + " started chopping " + ingredient.getName());

        try {
            int steps = 10;
            int stepDuration = duration / steps;

            for (int i = 0; i < steps; i++) {
                if (interrupted) {
                    System.out.println(chef.getName() + " stopped chopping (interrupted)");
                    chef.setBusy(false);
                    chef.setCurrentAction(ChefAction.IDLE);
                    return;
                }

                Thread.sleep(stepDuration);
                // Progress: [(i+1)/steps * 100]%
            }

            ingredient.chop();
            System.out.println(chef.getName() + " finished chopping " + ingredient.getName());

        } catch (InterruptedException e) {
            System.out.println(chef.getName() + " chopping interrupted");
        } finally {
            chef.setBusy(false);
            chef.setCurrentAction(ChefAction.IDLE);
        }
    }

    public void cancel() {
        interrupted = true;
    }
}
