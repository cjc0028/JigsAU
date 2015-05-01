package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Activity;
import android.os.Bundle;

import java.util.LinkedList;

public class BoardActivity extends Activity {

    private BoardView boardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        boardView = (BoardView) findViewById(R.id.gameboard);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("image") != null) {
                boardView.imagePath = bundle.getString("image");
            }
            else {
                boardView.imageId = bundle.getInt("image");
            }
            boardView.boardSize = bundle.getInt("gridSize");
        }

        @SuppressWarnings({"deprecation", "unchecked"})
        final LinkedList<Integer> tileOrder
                = (LinkedList<Integer>) getLastNonConfigurationInstance();
        if (tileOrder != null) {
            boardView.setTileOrder(tileOrder);
        }
    }
}
