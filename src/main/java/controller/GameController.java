package main.java.controller;

import javafx.scene.input.KeyEvent;
import main.java.model.GameManager;

public class GameController {

    private final GameManager gameManager;

    public GameController(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case W:
                gameManager.moveUp();
                break;
            case A:
                gameManager.moveLeft();
                break;
            case S:
                gameManager.moveDown();
                break;
            case D:
                gameManager.moveRight();
                break;
            case C:
                gameManager.handlePickupOrDrop();
                break;
            case V:
                gameManager.interact();
                break;
            case B:
                gameManager.switchActiveChef();
                break;
            default:
                break;
        }
    }
}
