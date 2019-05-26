package com.ashishgangaramani.dashboardapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.walshfernandes.dashboardapp.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final ImageView appLogo = findViewById(R.id.app_logo);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.hide();
        }

        ViewCompat.setTransitionName(appLogo, "splash_screen_transistion");
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                        // set the transition name in the logo
                        intent.putExtra("image_transition_name", ViewCompat.getTransitionName(appLogo));

                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                                SplashScreen.this,
                                appLogo,
                                ViewCompat.getTransitionName(appLogo));

                        startActivity(intent, options.toBundle());
                    }
                }, 3000
        );
    }
}
