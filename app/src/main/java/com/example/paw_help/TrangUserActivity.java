package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.paw_help.api.RetrofitClient;

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
        // Sử dụng các view có sẵn trong layout
        btnBack = findViewById(R.id.cardUserIcon);
        imgUserAvatar = findViewById(R.id.cardUserIcon);
        
        // Tìm các button trong layout
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        if (buttonLayout != null) {
            // Button "Về chúng tôi" và "Đội ngũ" đã có trong layout
        }
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        // Setup buttons trong layout
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);
        if (buttonLayout != null) {
            // Button "Về chúng tôi" (index 0)
            if (buttonLayout.getChildCount() > 0) {
                buttonLayout.getChildAt(0).setOnClickListener(v -> {
                    Intent intent = new Intent(TrangUserActivity.this, TrangVeChungToiActivity.class);
                    startActivity(intent);
                });
            }
            
            // Button "Đội ngũ" (index 1)
            if (buttonLayout.getChildCount() > 1) {
                buttonLayout.getChildAt(1).setOnClickListener(v -> {
                    Intent intent = new Intent(TrangUserActivity.this, TrangDoiNguActivity.class);
                    startActivity(intent);
                });
            }
        }
        
        // FloatingActionButton để đăng bài
        com.google.android.material.floatingactionbutton.FloatingActionButton fab = 
            findViewById(android.R.id.button1);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(TrangUserActivity.this, TrangDangBaiActivity.class);
                startActivity(intent);
            });
        }
    }

    private void loadUserData() {
        // Load user data từ RetrofitClient
        RetrofitClient client = RetrofitClient.getInstance(this);
        String userName = client.getUserName();
        String userEmail = client.getUserEmail();
        
        // Có thể hiển thị thông tin user ở đây nếu có view
        // Hiện tại layout không có các TextView này nên bỏ qua
    }

    private void performLogout() {
        new android.app.AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc muốn đăng xuất?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> {
                // Clear user session using RetrofitClient
                RetrofitClient client = RetrofitClient.getInstance(this);
                client.logout();
                
                Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
                
                // Navigate to login screen - clear task stack to prevent back navigation
                Intent intent = new Intent(TrangUserActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
}

