package main.java.model.item;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class Dish extends Item implements Preparable  {
    private String name;
    private IngredientState state;
    private Set<Preparable> contents;

    public Dish(Set<Preparable> contents) {
        this.contents = contents;
        this.name = this.getName();
        this.state = IngredientState.RAW;
    }

    public String getName() {
        boolean hasAdonan = false;
        boolean hasTomat = false;
        boolean hasKeju = false;
        boolean hasSosis = false;
        boolean hasAyam = false;

        Iterator<Preparable> it = contents.iterator();
        while (it.hasNext()) {
            String ing = it.next().getName();

            if (ing.equals("adonan")) hasAdonan = true;
            if (ing.equals("tomat"))  hasTomat = true;
            if (ing.equals("keju"))   hasKeju = true;
            if (ing.equals("sosis"))  hasSosis = true;
            if (ing.equals("ayam"))   hasAyam = true;
        }

        if (hasAdonan && hasTomat && hasKeju && hasSosis) {
            return "Pizza Sosis";
        }

        if (hasAdonan && hasTomat && hasKeju && hasAyam) {
            return "Pizza Ayam";
        }

        if (hasAdonan && hasTomat && hasKeju) {
            return "Pizza Margherita";
        }

        return "Unknown Dish";
    }

    public boolean canBeCooked() {
        return state == IngredientState.RAW;
    }

    public void cook() {
        if(canBeCooked()) {
            state = IngredientState.COOKING;
            System.out.println(name + " is being COOKED ... ");

            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            state = IngredientState.COOKED;
            System.out.println(name + " [" + state + "] is ready!");

            for (Preparable item : contents) {
                if (item instanceof Ingredient) {
                    ((Ingredient) item).setState(IngredientState.COOKED);
                }
            }
        } else {
            System.out.println("Already cooked");
        }
    }

    public boolean canBePlacedOnPlate() {
        return true;
    }

    public boolean canBeChopped() {
        return false;
    }

    public void chop() {

    }
}