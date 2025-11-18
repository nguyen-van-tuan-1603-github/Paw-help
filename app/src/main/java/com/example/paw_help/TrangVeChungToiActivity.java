package com.example.paw_help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class TrangVeChungToiActivity extends AppCompatActivity {

    private LinearLayout btnFacebook, btnWebsite, btnEmail;
    private Button btnViewTeam, btnDonate;
    private CardView cardMission, cardVision, cardValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ve_chung_toi);

        initViews();
        setupListeners();
        loadStatistics();
    }

    private void initViews() {
        btnFacebook = findViewById(R.id.btnFacebook);
        btnWebsite = findViewById(R.id.btnWebsite);
        btnEmail = findViewById(R.id.btnEmail);
        btnViewTeam = findViewById(R.id.btnViewTeam);
        btnDonate = findViewById(R.id.btnDonate);
    }

    private void setupListeners() {
        // Social media buttons
        btnFacebook.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/pawhelp"));
            startActivity(intent);
        });

        btnWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.pawhelp.com"));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contact@pawhelp.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ ứng dụng Paw Help");
            startActivity(Intent.createChooser(intent, "Gửi email"));
        });

        // View team button
        btnViewTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TrangVeChungToiActivity.this, TrangDoiNguActivity.class);
            startActivity(intent);
        });

        // Donate button
        btnDonate.setOnClickListener(v -> {
            // TODO: Implement donation functionality
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.pawhelp.com/donate"));
            startActivity(intent);
        });
    }

    private void loadStatistics() {
        // Update statistics - can be loaded from database or API
        TextView tvRescuedCount = findViewById(R.id.tvRescuedCount);
        TextView tvAdoptedCount = findViewById(R.id.tvAdoptedCount);
        TextView tvVolunteersCount = findViewById(R.id.tvVolunteersCount);
        TextView tvYearsCount = findViewById(R.id.tvYearsCount);

        if (tvRescuedCount != null) tvRescuedCount.setText("1,500+");
        if (tvAdoptedCount != null) tvAdoptedCount.setText("800+");
        if (tvVolunteersCount != null) tvVolunteersCount.setText("50+");
        if (tvYearsCount != null) tvYearsCount.setText("10+");
    }
}

