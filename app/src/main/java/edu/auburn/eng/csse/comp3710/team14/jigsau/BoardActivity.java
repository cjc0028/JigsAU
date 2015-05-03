package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class BoardActivity extends Activity implements View.OnClickListener{

    private BoardView boardView;

    private Timer timer = new Timer();
    private int time = 0;
    public int numberOfMoves = 0;
    private MediaPlayer victory;
    AudioManager audioManager;
    private boolean isMuted;
    private TextView moves_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        boardView = (BoardView) findViewById(R.id.gameboard);
        victory = MediaPlayer.create(getApplicationContext(), R.raw.victory);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        isMuted = (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0);
        moves_view = (TextView) findViewById(R.id.move_count);
        //moves_view.setText("Moves: " + numberOfMoves);

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

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run() {
                        TextView textView = (TextView)findViewById(R.id.timer);
                        textView.setText(time + "s");
                        time++;
                    }
                });
            }
        }, 1000, 1000);

        BoardView view = (BoardView)findViewById(R.id.gameboard);
        view.setObserver(new BoardView.Observer() {
            @Override
            public void callBack() {
                numberOfMoves++;
                TextView moves = (TextView)findViewById(R.id.move_count);
                moves.setText("Moves: " + numberOfMoves);
            }
        });

        Button checkPuzzle = (Button)findViewById(R.id.check_puzzle);
        checkPuzzle.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Toast toast;
        if(boardView.checkMatch()) {
            toast = Toast.makeText(this, "You Win!", Toast.LENGTH_LONG);
            toast.show();
            AlertDialog.Builder winDialog = new AlertDialog.Builder(this);
            winDialog.setTitle("You Win! (Smaller score is better)");
            winDialog.setMessage("Time: " + time + "\r\n"
                    + "Moves: " + numberOfMoves + "\r\n"
                    + "Score: " + ((time * numberOfMoves) / 10));
            winDialog.setPositiveButton(R.string.go_to_menu, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(BoardActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            winDialog.show();
            timer.cancel();
            victory.stop();
            victory.start();
        }
        else {
            toast = Toast.makeText(this, "Not Solved Yet", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("time", time);
        savedInstanceState.putInt("moves", numberOfMoves);
        savedInstanceState.putBoolean("isMuted", isMuted);
        savedInstanceState.putIntegerArrayList("currentTileOrder", boardView.getTileOrder());
        savedInstanceState.putString("imagePath", boardView.imagePath);
        savedInstanceState.putInt("imageId", boardView.imageId);
        savedInstanceState.putInt("boardSize", boardView.boardSize);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        boardView.boardSize = savedInstanceState.getInt("boardSize");
        boardView.imagePath = savedInstanceState.getString("imagePath");
        boardView.imageId = savedInstanceState.getInt("imageId");
        time = savedInstanceState.getInt("time");
        numberOfMoves = savedInstanceState.getInt("moves");
        moves_view.setText("Moves: " + numberOfMoves);
        if (savedInstanceState.getBoolean("isMuted")) {
            audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }

        boardView.setTileOrder(savedInstanceState.getIntegerArrayList("currentTileOrder"));
    }

    @SuppressWarnings({"deprecation", "unchecked"})
    public Object onRetainNonConfigurationInstance() {
        return this;
    }
}
