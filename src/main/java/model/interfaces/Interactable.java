package model.interfaces;

import model.chef.*;

public interface Interactable {
    void interact(Chef chef);
    boolean canInteract(Chef chef);
    String getInteractionPrompt();
}
