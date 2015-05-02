package edu.auburn.eng.csse.comp3710.team14.jigsau;


public class Position {

    public int row;
    public int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public boolean matches(Position position) {
        return position.row == row && position.column == column;
    }

    public boolean sharesAxisWith(Position position) {
        return (row == position.row || column == position.column);
    }

    public boolean isToRightOf(Position position) {
        return sharesAxisWith(position) && (column > position.column);
    }

    public boolean isToLeftOf(Position position) {
        return sharesAxisWith(position) && (column < position.column);
    }

    public boolean isAbove(Position position) {
        return sharesAxisWith(position) && (row < position.row);
    }

    public boolean isBelow(Position position) {
        return sharesAxisWith(position) && (row > position.row);
    }

    /*@Override
    public String toString() {
        return "[R: "+row+" C:"+column+"]";
    }*/
}
