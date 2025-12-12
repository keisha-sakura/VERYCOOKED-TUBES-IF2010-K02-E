package controller;

import javafx.scene.input.KeyEvent;
import model.GameManager;

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
            default:
                break;
        }
    }
}
