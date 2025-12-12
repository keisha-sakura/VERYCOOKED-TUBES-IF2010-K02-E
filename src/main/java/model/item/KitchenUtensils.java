package model.item;

import java.util.Set;
import java.util.HashSet;

public abstract class KitchenUtensils extends Item {
    private Set<Preparable> contents;

    public KitchenUtensils() {
        this.contents = new HashSet<>();
    }

    public Set<Preparable> getContents() {
        return contents;
    }
}