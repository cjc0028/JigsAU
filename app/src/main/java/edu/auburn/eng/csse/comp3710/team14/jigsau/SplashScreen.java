package edu.auburn.eng.csse.comp3710.team14.jigsau;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreen extends Activity {
    
    //Splash Screen Timer
    private static int SPLASH_TIME_OUT = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your main app activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);

                //close the activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
