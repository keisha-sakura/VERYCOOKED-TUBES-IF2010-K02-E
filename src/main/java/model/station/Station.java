package model.station;

import model.item.*;
import model.map.*;
import model.chef.*;
import model.recipe.*;
import model.order.*;

import java.util.*;

public abstract class Station {

    protected volatile boolean isProcessing;
    protected Thread processingThread;
    protected final Object stateLock = new Object();

    // Monitoring default (override in timed stations)
    public boolean isBusy() { return false; }
    public int getProgress() { return 0; }

    protected Position position;
    protected Item itemOnStation;

    public Station(Position pos) {
        this.position = pos;
        this.itemOnStation = null;
    }

    // Template Method Pattern
    public final void interact(Chef chef) {
        if (!canInteract(chef)) {
            throw new InvalidInteractionException("Cannot interact with this station");
        }
        performInteraction(chef);
    }

    protected abstract boolean canInteract(Chef chef);
    protected abstract void performInteraction(Chef chef);

    public Position getPosition() {
        return position;
    }

    public Item getItemOnStation() {
        return itemOnStation;
    }

    public void setItemOnStation(Item item) {
        this.itemOnStation = item;
    }

    public boolean hasItem() {
        return itemOnStation != null;
    }

    public void stopProcessing(Chef chef) {
        synchronized (stateLock) {
            isProcessing = false;
            if (processingThread != null && processingThread.isAlive()) {
                processingThread.interrupt();
            }
            chef.setIsBusy(false);
        }
    }

}