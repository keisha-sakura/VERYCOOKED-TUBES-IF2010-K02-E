package model.enums;

public enum StationType {
    CUTTING('C'),
    COOKING('R'),
    ASSEMBLY('A'),
    SERVING('S'),
    WASHING('W'),
    INGREDIENT_STORAGE('I'),
    PLATE_STORAGE('P'),
    TRASH('T'),
    WALL('X'),
    WALKABLE('.'),
    CHEF_SPAWN('V');

    private final char symbol;

    StationType(char symbol) {
        this.symbol = symbol;
    }

    public char getSymbol() { return symbol; }

    public static StationType fromChar(char c) {
        for (StationType type : values()) {
            if (type.symbol == c) return type;
        }
        return WALKABLE;
    }
}
