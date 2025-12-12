package controller.task;

import model.order.OrderManager;

public class OrderTimerTask extends Thread {
    private OrderManager orderManager;
    private volatile boolean running = true;

    public OrderTimerTask(OrderManager orderManager) {
        this.orderManager = orderManager;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);

                int expiredCount = orderManager.removeExpiredOrders();
                if (expiredCount > 0) {
                    System.out.println("⚠ " +expiredCount + " orders expired!");
                }

            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public void stopTimer() {
        running = false;
        this.interrupt();
    }
}
