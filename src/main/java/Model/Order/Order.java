
package model.order;

import model.recipe.Recipe;
import java.util.concurrent.atomic.AtomicInteger;

public class Order {
    private static final AtomicInteger orderCounter = new AtomicInteger(0);
    
    private int orderId;
    private int position;
    private Recipe recipe;
    private int timeLimit; // (dalam detik)
    private long creationTime;
    private boolean isExpired;
    
    public Order(Recipe recipe, int position, int timeLimit) {
        this.orderId = orderCounter.incrementAndGet();
        this.recipe = recipe;
        this.position = position;
        this.timeLimit = timeLimit;
        this.creationTime = System.currentTimeMillis();
        this.isExpired = false;
    }
    
    public int getOrderId() { return orderId; }
    public int getPosition() { return position; }
    public Recipe getRecipe() { return recipe; }
    public int getTimeLimit() { return timeLimit; }
    public long getCreationTime() { return creationTime; }
    public boolean isExpired() { return isExpired; }
    
    public void setPosition(int position) { this.position = position; }
    public void setExpired(boolean expired) { this.isExpired = expired; }
    
    
    public int getTimeRemaining() {
        if (isExpired) return 0;
        long elapsed = (System.currentTimeMillis() - creationTime) / 1000;
        int remaining = timeLimit - (int) elapsed;
        return Math.max(0, remaining);
    }
    
    
     // Mengecek apakah order sudah expired

    public boolean checkExpired() {
        if (getTimeRemaining() <= 0) {
            isExpired = true;
        }
        return isExpired;
    }
    
    @Override
    public String toString() {
        return "Order #" + orderId + ": " + recipe.getName() + 
               " [" + getTimeRemaining() + "s remaining]";
    }
}
