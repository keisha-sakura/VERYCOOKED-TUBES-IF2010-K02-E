package model.map;

public class Position {
    private int row;
    private int col;

    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow(){
        return row;
    }

    public int getCol(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public void setCol(int col){
        this.col = col;
    }

    public Position up(int x){
        return new Position(row - x, col);
    }

    public Position down(int x){
        return new Position(row + x, col);
    }

    public Position left(int x){
        return new Position(row, col - x);
    }

    public Position right(int x){
        return new Position(row, col + x);
    }

    public String toString(){
        return String.format("%d, %d", this.row, this.col);
    }
}
