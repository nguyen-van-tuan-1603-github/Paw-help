package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TrangUserActivity extends AppCompatActivity {

    private ImageView btnBack, imgUserAvatar;
    private TextView tvUserName, tvUserEmail, tvUserPhone;
    private CardView cardProfile, cardHistory, cardSettings, cardAbout, cardTeam, cardLogout;
    private Button btnEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_user);

        initViews();
        setupListeners();
        loadUserData();
    }

    private void initViews() {
        // TODO: Update layout to include these views or use existing IDs
        // btnBack = findViewById(R.id.btnBack);
        // imgUserAvatar = findViewById(R.id.imgUserAvatar);
        // tvUserName = findViewById(R.id.tvUserName);
        // tvUserEmail = findViewById(R.id.tvUserEmail);
        // tvUserPhone = findViewById(R.id.tvUserPhone);

        // cardProfile = findViewById(R.id.cardProfile);
        // cardHistory = findViewById(R.id.cardHistory);
        // cardSettings = findViewById(R.id.cardSettings);
        // cardAbout = findViewById(R.id.cardAbout);
        // cardTeam = findViewById(R.id.cardTeam);
        // cardLogout = findViewById(R.id.cardLogout);

        // btnEditProfile = findViewById(R.id.btnEditProfile);
    }

    private void setupListeners() {
        // TODO: Uncomment when views are added to layout
        /*
        btnBack.setOnClickListener(v -> finish());

        btnEditProfile.setOnClickListener(v -> {
            // TODO: Implement edit profile
            android.widget.Toast.makeText(this, "Tính năng đang phát triển",
                android.widget.Toast.LENGTH_SHORT).show();
        });

        cardProfile.setOnClickListener(v -> {
            // TODO: Go to profile detail
            android.widget.Toast.makeText(this, "Xem hồ sơ",
                android.widget.Toast.LENGTH_SHORT).show();
        });

        cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(TrangUserActivity.this,
                TrangXemLichSuCuuHoActivity.class);
            startActivity(intent);
        });

        cardSettings.setOnClickListener(v -> {
            // TODO: Go to settings
            android.widget.Toast.makeText(this, "Cài đặt",
                android.widget.Toast.LENGTH_SHORT).show();
        });

        cardAbout.setOnClickListener(v -> {
            Intent intent = new Intent(TrangUserActivity.this, TrangVeChungToiActivity.class);
            startActivity(intent);
        });

        cardTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TrangUserActivity.this, TrangDoiNguActivity.class);
            startActivity(intent);
        });

        cardLogout.setOnClickListener(v -> {
            performLogout();
        });
        */
    }

    private void loadUserData() {
        // TODO: Load user data from SharedPreferences or Firebase
        // TODO: Uncomment when views are added
        /*
        tvUserName.setText("Người dùng");
        tvUserEmail.setText("user@example.com");
        tvUserPhone.setText("0123456789");
        */
    }

    private void performLogout() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                // TODO: Clear user session data

                Intent intent = new Intent(TrangUserActivity.this, WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}

