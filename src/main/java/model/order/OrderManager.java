package model.order;

import model.recipe.Recipe;
import model.recipe.RecipeBook;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

//mengelola queue order
//Thread-safe menggunakan ConcurrentLinkedQueue
public class OrderManager {
    private static final int MAX_ACTIVE_ORDERS = 5;
    private static final int ORDER_TIME_LIMIT = 60; // 60 detik per order
    
    private Queue<Order> activeOrders;
    private RecipeBook recipeBook;
    private int consecutiveFailures;
    
    public OrderManager() {
        this.activeOrders = new ConcurrentLinkedQueue<>();
        this.recipeBook = RecipeBook.getInstance();
        this.consecutiveFailures = 0;
        generateInitialOrders();
    }
    
    private void generateInitialOrders() {
        for (int i = 0; i < 3; i++) {
            addNewOrder();
        }
    }
  
    public synchronized void addNewOrder() {
        if (activeOrders.size() >= MAX_ACTIVE_ORDERS) {
            return;
        }
        
        Recipe recipe = recipeBook.getRandomRecipe();
        if (recipe != null) {
            Order order = new Order(recipe, activeOrders.size() + 1, ORDER_TIME_LIMIT);
            activeOrders.offer(order);
            updateOrderPositions();
        }
    }
    
    
    private void updateOrderPositions() {
        int pos = 1;
        for (Order order : activeOrders) {
            order.setPosition(pos++);
        }
    }
    
    
    public synchronized boolean completeOrder(Recipe matchedRecipe) {
        for (Order order : activeOrders) {
            if (order.getRecipe().getName().equals(matchedRecipe.getName())) {
                activeOrders.remove(order);
                consecutiveFailures = 0;
                updateOrderPositions();
                addNewOrder();
                return true;
            }
        }
        return false;
    }
    
    
    public synchronized int removeExpiredOrders() {
        int expiredCount = 0;
        Iterator<Order> iterator = activeOrders.iterator();
        while (iterator.hasNext()) {
            Order order = iterator.next();
            if (order.checkExpired()) {
                iterator.remove();
                expiredCount++;
                consecutiveFailures++;
            }
        }
        
        if (expiredCount > 0) {
            updateOrderPositions();
            // Generate order baru untuk yang expired
            for (int i = 0; i < expiredCount; i++) {
                addNewOrder();
            }
        }
        
        return expiredCount;
    }
    
   //dish salah
    public synchronized void failOrder() {
        consecutiveFailures++;
    }
    
    public Queue<Order> getActiveOrders() {
        return new LinkedList<>(activeOrders);
    }
    
    public int getConsecutiveFailures() {
        return consecutiveFailures;
    }
    
    public boolean hasReachedFailureLimit() {
        return consecutiveFailures >= 5; // Batas 5 kegagalan berturut-turut
    }
}
