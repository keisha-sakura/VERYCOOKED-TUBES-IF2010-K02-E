package model.map;

public abstract class MapType {
    protected abstract String[] getLayoutData();
    protected abstract String getMapName();
    protected abstract void initialLayout();
    protected Tile[][] tiles = new Tile[getHeight()][getWidth()];

    public final int getWidth() {
        return 14;
    }

    public final int getHeight() {
        return 10;
    }

    public String[] getLayout() {
        return this.getLayoutData();
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}