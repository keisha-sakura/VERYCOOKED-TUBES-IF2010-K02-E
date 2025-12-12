package model.order;

import model.recipe.*;

public class Order implements Runnable {
    private int posisiOrder;
    private Recipe recipe;
    private int reward;
    private int penalty;

    private volatile int timeLeft = 60;
    private volatile boolean active = true;

    public Order(int posisiOrder, Recipe recipe, int reward, int penalty) {
        this.posisiOrder = posisiOrder;
        this.recipe = recipe;
        this.reward = reward;
        this.penalty = penalty;

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (timeLeft > 0 && active) {
            this.show();

            try { Thread.sleep(1000); }
            catch (Exception ignored) {}

            timeLeft--;
        }

        if (!active) return;

        System.out.println("[ORDER " + posisiOrder + "] EXPIRED!");

        OrderManager.getInstance().expireOrder(this);
    }

    public void complete() {
        active = false;
        System.out.println("[ORDER " + posisiOrder + "] COMPLETED!");
    }

    public Recipe getRecipe() { return recipe; }

    public int getReward() { return reward; }

    public int getPenalty() { return penalty; }

    public void show() {
        System.out.println("[ORDER " + posisiOrder + "]");
        System.out.println("Dish: " + recipe.getName());
        System.out.println("Reward: " + reward);
        System.out.println("Penalty: " + penalty);
        System.out.println("Time left: " + timeLeft + "s");
        System.out.println("=====================");
    }

}