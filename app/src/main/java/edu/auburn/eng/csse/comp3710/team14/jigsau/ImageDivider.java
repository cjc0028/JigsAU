package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ImageDivider {

    private Bitmap originalBitmap;
    private int tileSize, boardSize;
    public List<Bitmap> tiles;
    private int lastTileServed;
    private List<Integer> tileOrder;
    private Context context;

    public ImageDivider(Bitmap originalBitmap, int boardSize, Context context) {

        super();
        lastTileServed = 0;
        this.originalBitmap = originalBitmap;
        this.boardSize = boardSize;
        this.context = context;
        this.tileSize = originalBitmap.getWidth() / boardSize;
        tiles = new LinkedList<Bitmap>();
        divideImage();
    }

    private void divideImage() {
        int x, y;
        Bitmap bitmap;
        lastTileServed = 0;

        for (int col = 0; col < boardSize; col++) {
            for (int row = 0; row < boardSize; row++) {
                // don't divide last part - empty tile
                if (row == boardSize - 1 && col == boardSize - 1) {

                } else {
                    x = row * tileSize;
                    y = col * tileSize;

                    // divide
                    bitmap = Bitmap.createBitmap(originalBitmap, x, y, tileSize, tileSize);

                    // draw border lines
                    Canvas canvas = new Canvas(bitmap);
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#000000"));
                    int end = tileSize - 1;
                    canvas.drawLine(0, 0, 0, end, paint);
                    canvas.drawLine(0, end, end, end, paint);
                    canvas.drawLine(end, end, end, 0, paint);
                    canvas.drawLine(end, 0, 0, 0, paint);
                    tiles.add(bitmap);
                }
            }
        }
        // remove reference to original bitmap
        originalBitmap = null;
    }

    public Tile getTile() {
        Tile tile = null;
        if (tiles.size() > 0 ) {
            int originalIndex;
            if (tileOrder == null) {
                originalIndex = lastTileServed++;
            }
            else {
                originalIndex = tileOrder.get(lastTileServed++);
            }
            tile = new Tile(context, originalIndex);
            if (tiles.get(0) == null) {
                //empty tile
                tile.setEmpty(true);
            }
            tile.setImageBitmap(tiles.remove(0));
        }
        return tile;
    }

    public List<Bitmap> getBitmap() {
        return new ArrayList<Bitmap>(tiles);
    }
}
