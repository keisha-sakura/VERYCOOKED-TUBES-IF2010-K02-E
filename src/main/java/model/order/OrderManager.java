package main.java.model.order;

import main.java.model.recipe.*;
import java.util.*;
import java.util.concurrent.*;

public class OrderManager {

    private static OrderManager instance;
    public static OrderManager getInstance() {
        if (instance == null) instance = new OrderManager();
        return instance;
    }

    private final List<Order> activeOrders = new ArrayList<>();
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    private final int MAX_ORDER = 4;

    private OrderManager() {}

    public void start() {
        scheduler.schedule(() -> {
            for (int i = 1; i <= MAX_ORDER; i++) {
                spawnOrder(i);
            }
        }, 3, TimeUnit.SECONDS);

        scheduler.scheduleAtFixedRate(() -> {
            if (activeOrders.size() < MAX_ORDER) {
                spawnOrder(activeOrders.size() + 1);
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    public synchronized void spawnOrder(int posisi) {
        Order o = new Order(posisi,
                RecipePool.getRandomRecipe(),
                100,
                -50);

        activeOrders.add(o);
    }

    public synchronized void expireOrder(Order o) {
        activeOrders.remove(o);
        System.out.println("ORDER REMOVED → EXPIRED");
        spawnOrder(activeOrders.size() + 1);
    }

    public synchronized void completeOrder(Order o) {
        activeOrders.remove(o);
        System.out.println("ORDER REMOVED → COMPLETED");
        spawnOrder(activeOrders.size() + 1);
    }
}