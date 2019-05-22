package com.venomtech.bellatez.gnytransax;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreenActivity extends AppCompatActivity {

    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        iv = (ImageView) findViewById(R.id.logo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.animation);
        iv.startAnimation(animation);

        final Intent i = new Intent(this, HomeActivity.class);

        Thread timer = new Thread(){
            @Override
            public void run() {

                try {
                    sleep(3000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                } finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }
}
