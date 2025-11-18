package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnGetStarted, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnGetStarted = findViewById(R.id.btnGetStarted);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void setupListeners() {
        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        // Disable back button on welcome screen
        // User must choose login or register
        moveTaskToBack(true);
    }
}

