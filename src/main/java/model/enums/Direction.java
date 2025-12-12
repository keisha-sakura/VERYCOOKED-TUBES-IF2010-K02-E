package model.enums;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public int getDx() {
        return switch(this) {
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> 0;
        };
    }

    public int getDy() {
        return switch(this) {
            case DOWN -> 1;
            case UP -> -1;
            default -> 0;
        };
    }
}
