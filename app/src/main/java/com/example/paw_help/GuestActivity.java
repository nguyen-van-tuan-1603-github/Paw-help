package com.example.paw_help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class GuestActivity extends AppCompatActivity {

    private Button btnReportRescue, btnViewRescuePosts, btnEmergencyContact, btnRegisterNow;
    private TextView tvLoginNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        initViews();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        btnReportRescue = findViewById(R.id.btnReportRescue);
        btnViewRescuePosts = findViewById(R.id.btnViewRescuePosts);
        btnEmergencyContact = findViewById(R.id.btnEmergencyContact);
        btnRegisterNow = findViewById(R.id.btnRegisterNow);
        tvLoginNow = findViewById(R.id.tvLoginNow);
    }

    private void setupListeners() {
        // Report Rescue - Navigate to Guest Report Rescue Activity
        btnReportRescue.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, GuestReportRescueActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // View Rescue Posts - Navigate to Main Activity as Guest
        btnViewRescuePosts.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, MainActivity.class);
            intent.putExtra("IS_GUEST", true);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        // Emergency Contact - Show contact options
        btnEmergencyContact.setOnClickListener(v -> {
            showEmergencyContactDialog();
        });

        // Register Now - Navigate to Register Activity
        btnRegisterNow.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        // Login Now - Navigate to Login Activity
        tvLoginNow.setOnClickListener(v -> {
            Intent intent = new Intent(GuestActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });
    }

    private void showEmergencyContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Liên hệ khẩn cấp 🚨");
        builder.setMessage("Chọn phương thức liên hệ:\n\n" +
                "📞 Hotline: 0905-XXX-XXX\n" +
                "📧 Email: emergency@pawhelp.vn\n" +
                "📍 Địa chỉ: Đà Nẵng, Việt Nam");

        builder.setPositiveButton("Gọi điện", (dialog, which) -> {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:0905123456"));
            startActivity(callIntent);
        });

        builder.setNegativeButton("Gửi Email", (dialog, which) -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:emergency@pawhelp.vn"));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Khẩn cấp - Cần cứu hộ động vật");
            try {
                startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng email"));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(GuestActivity.this, 
                    "Không tìm thấy ứng dụng email", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNeutralButton("Đóng", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void startAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Animate all main views
        View[] views = {
            btnReportRescue,
            btnViewRescuePosts,
            btnEmergencyContact,
            btnRegisterNow
        };

        for (int i = 0; i < views.length; i++) {
            if (views[i] != null) {
                Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                anim.setStartOffset(i * 100L);
                views[i].startAnimation(anim);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog before exiting
        new AlertDialog.Builder(this)
            .setTitle("Thoát ứng dụng?")
            .setMessage("Bạn có chắc muốn thoát khỏi ứng dụng?")
            .setPositiveButton("Thoát", (dialog, which) -> {
                super.onBackPressed();
                finishAffinity();
            })
            .setNegativeButton("Ở lại", (dialog, which) -> dialog.dismiss())
            .show();
    }
}

