package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.widget.ImageView;

public class Tile extends ImageView {

    public Position position;
    public int originalIndex;
    //public int numberOfMoves;
    private boolean isEmpty;

    public Tile(Context context, int originalIndex) {
        super(context);
        this.originalIndex = originalIndex;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setEmpty(boolean empty) {
        this.isEmpty = empty;
        if (empty) {
            setBackground(null);
            setImageAlpha(0);
        }
    }

    public boolean isInRowOrColumnOf(Tile otherTile) {
        return (position.sharesAxisWith(otherTile.position));
    }

    public boolean isToRightOf(Tile tile) {
        return position.isToRightOf(tile.position);
    }

    public boolean isToLeftOf(Tile tile) {
        return position.isToLeftOf(tile.position);
    }

    public boolean isAbove(Tile tile) {
        return position.isAbove(tile.position);
    }

    public boolean isBelow(Tile tile) {
        return position.isBelow(tile.position);
    }

    /**
     * Sets X Y position for the view - works for all Android versions.
     *
     * @param x
     * @param y
     */
    public void setXY(float x, float y) {
        setX(x);
        setY(y);
    }
}
