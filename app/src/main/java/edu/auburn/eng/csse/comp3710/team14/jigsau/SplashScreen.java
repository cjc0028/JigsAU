package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
    
    private static int SPLASH_TIME_OUT = 5000;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.gameboy_start_up);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                mediaPlayer.start();

                finish();
                overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
            }
        }, SPLASH_TIME_OUT);
    }
}
