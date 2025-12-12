package main.java.interfaces;

import model.entity.Chef;

public interface Interactable {
    void interact(Chef chef);
    boolean canInteract(Chef chef);
    String getInteractionPrompt();
}
