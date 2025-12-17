package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paw_help.api.RetrofitClient;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DURATION = 3000; // 3 seconds

    private ImageView imgLogo;
    private TextView tvAppName, tvSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        startAnimations();
        navigateToNextScreen();
    }

    private void initViews() {
        imgLogo = findViewById(R.id.imgLogo);
        tvAppName = findViewById(R.id.tvAppName);
        tvSlogan = findViewById(R.id.tvSlogan);
    }

    private void startAnimations() {
        // Fade in animation for logo
        Animation fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeIn.setDuration(1500);
        imgLogo.startAnimation(fadeIn);

        // Slide up animation for app name
        Animation slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        slideUp.setDuration(1000);
        slideUp.setStartOffset(500);
        tvAppName.startAnimation(slideUp);

        // Fade in for slogan
        Animation fadeInSlogan = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        fadeInSlogan.setDuration(1000);
        fadeInSlogan.setStartOffset(1000);
        tvSlogan.startAnimation(fadeInSlogan);
    }

    private void navigateToNextScreen() {
        new Handler().postDelayed(() -> {
            // Check if user is logged in
            boolean isLoggedIn = checkIfUserLoggedIn();

            Intent intent;
            if (isLoggedIn) {
                // User is logged in, go to main activity
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                // User not logged in, go to welcome screen
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }

    private boolean checkIfUserLoggedIn() {
        RetrofitClient client = RetrofitClient.getInstance(this);
        return client.isLoggedIn();
    }
}

