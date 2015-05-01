package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.animation.Animator;
import android.animation.FloatEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.LinkedList;



public class BoardView extends RelativeLayout implements OnTouchListener {

    public enum Direction {
        X, Y
    }

    public int boardSize = 4;
    public String imagePath = null;
    public int imageId = 0;
    private MediaPlayer mediaPlayer = MediaPlayer.create(this.getContext().getApplicationContext(), R.raw.tileclick);


    private int tileSize;
    private ArrayList<Tile> tiles;
    private Tile emptyTile, movedTile;
    private boolean boardCreated;
    private RectF gameboardRect;
    private PointF lastDragPoint;
    private ArrayList<Movement> currentMovement;
    private LinkedList<Integer> tileOrder;

    public BoardView(Context context, AttributeSet attrSet) {
        super(context, attrSet);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if(!boardCreated) {
            determineGameboardSizes();
            fillTiles();
            boardCreated = true;
        }
    }

    private void determineGameboardSizes() {
        int viewWidth = findViewById(R.id.gameboard).getWidth();
        int viewHeight = findViewById(R.id.gameboard).getHeight();

        if (viewWidth > viewHeight) {
            tileSize = viewHeight / boardSize;
        }
        else {
            tileSize = viewWidth / boardSize;
        }
        int gameboardSize = tileSize * boardSize;

        int gameboardTop = viewHeight / 2 - gameboardSize / 2;
        int gameboardLeft = viewWidth / 2 - gameboardSize / 2;
        gameboardRect = new RectF(gameboardLeft, gameboardTop,
                gameboardLeft + gameboardSize, gameboardTop + gameboardSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        Tile touchedTile = (Tile) v;
        if (touchedTile.isEmpty() || !touchedTile.isInRowOrColumnOf(emptyTile)) {
            return false;
        }
        else {
            mediaPlayer.start();
            if (event.getActionMasked() ==  MotionEvent.ACTION_DOWN) {
                movedTile = touchedTile;
                currentMovement = getTilesBetweenEmptyTileAndTile(movedTile);
                movedTile.numberOfMoves = 0;
            }
            else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                if (lastDragPoint != null) {
                    followFinger(event);
                }
                lastDragPoint = new PointF(event.getRawX(), event.getRawY());
            }
            else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                currentMovement = getTilesBetweenEmptyTileAndTile(movedTile);

                if (lastDragMovedAtLeastHalfway() || isClicked()) {
                    animateTilesToEmptySpace();
                }
                else {
                    animateTilesBackToOrigin();
                }

                currentMovement = null;
                lastDragPoint = null;
                movedTile = null;
            }
            return true;
        }
    }


    public void fillTiles() {

        Bitmap original;
        // load image to slicer
        if (imagePath != null) {
            original = BitmapFactory.decodeFile(imagePath);
        }
        else {
            original = BitmapFactory.decodeResource(getContext().getResources(), imageId);
        }
        ImageDivider imageDivider = new ImageDivider(original, boardSize, getContext());
        // order slices
        if (tileOrder == null) {
            imageDivider.randomizeTiles();
        } else {
            imageDivider.setTileOrder(tileOrder);
        }
        // fill game board with slices
        tiles = new ArrayList<>();
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                Tile tile;
                if (tileOrder == null) {
                    tile = imageDivider.getTile();
                } else {
                    tile = imageDivider.getTile();
                }
                tile.position = new Position(row, col);
                if (tile.isEmpty()) {
                    emptyTile = tile;
                }
                placeTile(tile);
                tiles.add(tile);
            }
        }
    }

    private void placeTile(Tile tile) {

        Rect tileRect = rectForPosition(tile.position);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(tileSize, tileSize);
        params.topMargin = tileRect.top;
        params.leftMargin = tileRect.left;
        addView(tile, params);
        tile.setOnTouchListener(this);
    }

    private boolean lastDragMovedAtLeastHalfway() {
        if (lastDragPoint != null && currentMovement != null && currentMovement.size() > 0) {
            Movement firstMotionDescriptor = currentMovement.get(0);
            if (firstMotionDescriptor.axialDelta > tileSize / 2) {
                return true;
            }
        }
        return false;
    }

    private boolean isClicked() {
        if (lastDragPoint == null) {
            return true;
        }

        if (currentMovement != null && currentMovement.size() > 0 && movedTile.numberOfMoves < 10) {
            Movement firstMotionDescriptor = currentMovement.get(0);

            if (firstMotionDescriptor.axialDelta < tileSize / 20) {
                return true;
            }
        }
        return false;
    }

    private void followFinger(MotionEvent event) {
        boolean impossibleMove = true;
        float dxEvent = event.getRawX() - lastDragPoint.x;
        float dyEvent = event.getRawY() - lastDragPoint.y;
        Tile tile;
        movedTile.numberOfMoves++;
        for (Movement descriptor : currentMovement) {
            tile = descriptor.tile;
            Pair<Float, Float> xy = getXYFromEvent(tile, dxEvent, dyEvent, descriptor.direction);
            // detect if this move is valid
            RectF candidateRect = new RectF(xy.first, xy.second, xy.first + tile.getWidth(), xy.second
                    + tile.getHeight());
            ArrayList<Tile> tilesToCheck = null;
            if (tile.position.row == emptyTile.position.row) {
                tilesToCheck = allTilesInRow(tile.position.row);
            } else if (tile.position.column == emptyTile.position.column) {
                tilesToCheck = allTilesInColumn(tile.position.column);
            }

            boolean candidateRectInGameboard = (gameboardRect.contains(candidateRect));
            boolean collides = collidesWithTiles(candidateRect, tile, tilesToCheck);

            impossibleMove = impossibleMove && (!candidateRectInGameboard || collides);
        }
        if (!impossibleMove) {
            // perform the move for all moved tiles in the descriptors
            for (Movement descriptor : currentMovement) {
                tile = descriptor.tile;
                Pair<Float, Float> xy = getXYFromEvent(tile, dxEvent, dyEvent, descriptor.direction);
                tile.setXY(xy.first, xy.second);
            }
        }
    }

    private Pair<Float, Float> getXYFromEvent(Tile tile, float dxEvent, float dyEvent, Direction direction) {
        float dxTile = 0, dyTile = 0;
        if (direction == Direction.X) {
            dxTile = tile.getX() + dxEvent;
            dyTile = tile.getY();
        }
        if (direction == Direction.Y) {
            dyTile = tile.getY() + dyEvent;
            dxTile = tile.getX();
        }
        return new Pair<>(dxTile, dyTile);
    }

    private boolean collidesWithTiles(RectF candidateRect, Tile tile, ArrayList<Tile> tilesToCheck) {
        RectF otherTileRect;
        for (Tile otherTile : tilesToCheck) {
            if (!otherTile.isEmpty() && otherTile != tile) {
                otherTileRect = new RectF(otherTile.getX(), otherTile.getY(), otherTile.getX()
                        + otherTile.getWidth(), otherTile.getY() + otherTile.getHeight());
                if (RectF.intersects(otherTileRect, candidateRect)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void animateTilesToEmptySpace() {
        emptyTile.setXY(movedTile.getX(), movedTile.getY());
        emptyTile.position = movedTile.position;
        ObjectAnimator animator;
        for (final Movement motionDescriptor : currentMovement) {
            animator = ObjectAnimator.ofObject(motionDescriptor.tile, motionDescriptor.direction.toString(),
                    new FloatEvaluator(), motionDescriptor.from, motionDescriptor.to);
            animator.setDuration(16);
            animator.addListener(new Animator.AnimatorListener() {

                public void onAnimationStart(Animator animation) {
                }

                public void onAnimationCancel(Animator animation) {
                }

                public void onAnimationRepeat(Animator animation) {
                }

                public void onAnimationEnd(Animator animation) {
                    motionDescriptor.tile.position = motionDescriptor.finalPosition;
                    motionDescriptor.tile.setXY(motionDescriptor.finalRect.left, motionDescriptor.finalRect.top);
                }
            });
            animator.start();
        }
    }

    private void animateTilesBackToOrigin() {
        ObjectAnimator animator;
        if (currentMovement != null) {
            for (final Movement motionDescriptor : currentMovement) {
                animator = ObjectAnimator.ofObject(motionDescriptor.tile, motionDescriptor.direction.toString(),
                        new FloatEvaluator(), motionDescriptor.currentPosition(), motionDescriptor.originalPosition());
                animator.setDuration(16);
                animator.addListener(new Animator.AnimatorListener() {

                    public void onAnimationStart(Animator animation) {
                    }

                    public void onAnimationCancel(Animator animation) {
                    }

                    public void onAnimationRepeat(Animator animation) {
                    }

                    public void onAnimationEnd(Animator animation) {
                        motionDescriptor.tile.setXY(motionDescriptor.originalRect.left,
                                motionDescriptor.originalRect.top);
                    }
                });
                animator.start();
            }
        }
    }

    private ArrayList<Movement> getTilesBetweenEmptyTileAndTile(Tile tile) {
        ArrayList<Movement> descriptors = new ArrayList<>();
        Position position, finalPosition;
        Tile foundTile;
        Movement motionDescriptor;
        Rect finalRect, currentRect;
        float axialDelta;
        if (tile.isToRightOf(emptyTile)) {
            // add all tiles left of the tile
            for (int i = tile.position.column; i > emptyTile.position.column; i--) {
                position = new Position(tile.position.row, i);
                foundTile = (tile.position.matches(position)) ? tile : getTileAtPosition(position);
                finalPosition = new Position(tile.position.row, i - 1);
                currentRect = rectForPosition(foundTile.position);
                finalRect = rectForPosition(finalPosition);
                axialDelta = Math.abs(foundTile.getX() - currentRect.left);
                motionDescriptor = new Movement(foundTile, Direction.X, foundTile.getX(),
                        finalRect.left);
                motionDescriptor.finalPosition = finalPosition;
                motionDescriptor.finalRect = finalRect;
                motionDescriptor.axialDelta = axialDelta;
                descriptors.add(motionDescriptor);
            }
        } else if (tile.isToLeftOf(emptyTile)) {
            // add all tiles right of the tile
            for (int i = tile.position.column; i < emptyTile.position.column; i++) {
                position = new Position(tile.position.row, i);
                foundTile = (tile.position.matches(position)) ? tile : getTileAtPosition(position);
                finalPosition = new Position(tile.position.row, i + 1);
                currentRect = rectForPosition(foundTile.position);
                finalRect = rectForPosition(finalPosition);
                axialDelta = Math.abs(foundTile.getX() - currentRect.left);
                motionDescriptor = new Movement(foundTile, Direction.X, foundTile.getX(),
                        finalRect.left);
                motionDescriptor.finalPosition = finalPosition;
                motionDescriptor.finalRect = finalRect;
                motionDescriptor.axialDelta = axialDelta;
                descriptors.add(motionDescriptor);
            }
        } else if (tile.isAbove(emptyTile)) {
            // add all tiles bellow the tile
            for (int i = tile.position.row; i < emptyTile.position.row; i++) {
                position = new Position(i, tile.position.column);
                foundTile = (tile.position.matches(position)) ? tile : getTileAtPosition(position);
                finalPosition = new Position(i + 1, tile.position.column);
                currentRect = rectForPosition(foundTile.position);
                finalRect = rectForPosition(finalPosition);
                axialDelta = Math.abs(foundTile.getY() - currentRect.top);
                motionDescriptor = new Movement(foundTile, Direction.Y, foundTile.getY(),
                        finalRect.top);
                motionDescriptor.finalPosition = finalPosition;
                motionDescriptor.finalRect = finalRect;
                motionDescriptor.axialDelta = axialDelta;
                descriptors.add(motionDescriptor);
            }
        } else if (tile.isBelow(emptyTile)) {
            // add all tiles above the tile
            for (int i = tile.position.row; i > emptyTile.position.row; i--) {
                position = new Position(i, tile.position.column);
                foundTile = (tile.position.matches(position)) ? tile : getTileAtPosition(position);
                finalPosition = new Position(i - 1, tile.position.column);
                currentRect = rectForPosition(foundTile.position);
                finalRect = rectForPosition(finalPosition);
                axialDelta = Math.abs(foundTile.getY() - currentRect.top);
                motionDescriptor = new Movement(foundTile, Direction.Y, foundTile.getY(),
                        finalRect.top);
                motionDescriptor.finalPosition = finalPosition;
                motionDescriptor.finalRect = finalRect;
                motionDescriptor.axialDelta = axialDelta;
                descriptors.add(motionDescriptor);
            }
        }
        return descriptors;
    }

    private Tile getTileAtPosition(Position position) {
        for (Tile tile : tiles) {
            if (tile.position.matches(position)) {
                return tile;
            }
        }
        return null;
    }

    private ArrayList<Tile> allTilesInRow(int row) {
        ArrayList<Tile> tilesInRow = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.position.row == row) {
                tilesInRow.add(tile);
            }
        }
        return tilesInRow;
    }

    private ArrayList<Tile> allTilesInColumn(int column) {
        ArrayList<Tile> tilesInColumn = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.position.column == column) {
                tilesInColumn.add(tile);
            }
        }
        return tilesInColumn;
    }

    public Rect rectForPosition(Position position) {
        int gameboardY = (int) Math.floor(gameboardRect.top);
        int gameboardX = (int) Math.floor(gameboardRect.left);
        int top = (position.row * tileSize) + gameboardY;
        int left = (position.column * tileSize) + gameboardX;
        return new Rect(left, top, left + tileSize, top + tileSize);
    }

    public LinkedList<Integer> getTileOrder() {
        LinkedList<Integer> tileLocations = new LinkedList<Integer>();
        for (int rowI = 0; rowI < boardSize; rowI++) {
            for (int colI = 0; colI < boardSize; colI++) {
                Tile tile = getTileAtPosition(new Position(rowI, colI));
                tileLocations.add(tile.originalIndex);
            }
        }
        return tileLocations;
    }

    public void setTileOrder(LinkedList<Integer> tileLocations) {
        this.tileOrder = tileLocations;
    }

    public class Movement {

        public Rect finalRect, originalRect;
        public BoardView.Direction direction; // x or y
        public Tile tile;
        public float from, to, axialDelta;
        public Position finalPosition;

        public Movement(Tile tile, BoardView.Direction direction, float from, float to) {
            super();
            this.tile = tile;
            this.from = from;
            this.to = to;
            this.direction = direction;
            this.originalRect = rectForPosition(tile.position);
        }

        /**
         * @return current position of the tile
         */
        public float currentPosition() {
            if (direction == BoardView.Direction.X) {
                return tile.getX();
            } else if (direction == BoardView.Direction.Y) {
                return tile.getY();
            }
            return 0;
        }

        /**
         * @return original position of the tile. It is used in movement to
         *         original position.
         */
        public float originalPosition() {
            if (direction == BoardView.Direction.X) {
                return originalRect.left;
            } else if (direction == BoardView.Direction.Y) {
                return originalRect.top;
            }
            return 0;
        }
    }

}
