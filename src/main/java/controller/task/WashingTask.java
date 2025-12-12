package controller.task;

import model.chef.*;
import model.enums.ChefAction;
import model.item.utensils.Plate;
import model.station.WashingStation;

public class WashingTask extends Thread {
    private WashingStation station;
    private Plate plate;
    private int duration;
    private Chef chef;
    private volatile boolean interrupted = false;

    public WashingTask(WashingStation station, Plate plate, int duration) {
        this.station = station;
        this.plate = plate;
        this.duration = duration;
    }

    public void setChef(Chef chef) {
        this.chef = chef;
    }

    @Override
    public void run() {
        if (chef != null) {
            chef.setBusy(true);
            chef.setCurrentAction(ChefAction.WASHING);
        }

        System.out.println("Started washing plate...");

        try {
            int steps = 10;
            int stepDuration = duration / steps;

            for (int i = 0; i < steps; i++) {
                if (interrupted) {
                    System.out.println("Washing interrupted");
                    if (chef != null) {
                        chef.setBusy(false);
                        chef.setCurrentAction(ChefAction.IDLE);
                    }
                    station.setWashing(false);
                    return;
                }

                Thread.sleep(stepDuration);
            }

            station.finishWashing(plate);
            System.out.println("Plate washed clean!");

        } catch (InterruptedException e) {
            System.out.println("Washing interrupted");
        } finally {
            if (chef != null) {
                chef.setBusy(false);
                chef.setCurrentAction(ChefAction.IDLE);
            }
            station.setWashing(false);
        }
    }

    public void cancel() {
        interrupted = true;
    }
}
