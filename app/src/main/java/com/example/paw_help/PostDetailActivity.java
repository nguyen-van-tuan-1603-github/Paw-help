package com.example.paw_help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class PostDetailActivity extends AppCompatActivity {

    private CardView btnBack, btnShare, btnCall, btnReport;
    private ImageView imgPostPhoto;
    private TextView tvStatus, tvTitle, tvLocation;
    private Button btnRescue;

    private String postId;
    private String postTitle;
    private String postLocation;
    private String postStatus;
    private String contactPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initViews();
        loadPostData();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnCall = findViewById(R.id.btnCall);
        btnReport = findViewById(R.id.btnReport);
        imgPostPhoto = findViewById(R.id.imgPostPhoto);
        tvStatus = findViewById(R.id.tvStatus);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        btnRescue = findViewById(R.id.btnRescue);
    }

    private void loadPostData() {
        // Get data from intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("post_id");
        postTitle = intent.getStringExtra("description");
        postLocation = intent.getStringExtra("location");
        postStatus = intent.getStringExtra("status");
        contactPhone = intent.getStringExtra("phone");

        // Set default values if null
        if (postTitle == null) postTitle = "Phát hiện động vật cần cứu hộ";
        if (postLocation == null) postLocation = "Đà Nẵng";
        if (postStatus == null) postStatus = "Mới cần cứu hộ";
        if (contactPhone == null) contactPhone = "0123456789";

        // Display data
        tvTitle.setText(postTitle);
        tvLocation.setText(postLocation);
        tvStatus.setText(postStatus);

        // TODO: Load image from URL or resource
        // For now using default image in layout
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Share button
        btnShare.setOnClickListener(v -> sharePost());

        // Call button
        btnCall.setOnClickListener(v -> callContact());

        // Report button
        btnReport.setOnClickListener(v -> reportPost());

        // Rescue button
        btnRescue.setOnClickListener(v -> offerRescue());
    }

    private void sharePost() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareMessage = "🐾 Cần cứu hộ khẩn cấp!\n\n" +
                            postTitle + "\n\n" +
                            "Địa điểm: " + postLocation + "\n\n" +
                            "Hãy giúp đỡ qua ứng dụng PawHelp!";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    private void callContact() {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + contactPhone));
        startActivity(callIntent);
    }

    private void reportPost() {
        String[] reportReasons = {
            "Thông tin sai sự thật",
            "Nội dung không phù hợp",
            "Spam",
            "Lừa đảo",
            "Khác"
        };

        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Báo cáo bài đăng")
            .setItems(reportReasons, (dialog, which) -> {
                String reason = reportReasons[which];
                // TODO: Gửi báo cáo lên server
                Toast.makeText(this, "Đã gửi báo cáo: " + reason,
                             Toast.LENGTH_SHORT).show();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void offerRescue() {
        // Chuyển đến màn hình Rescue Dashboard
        Intent intent = new Intent(PostDetailActivity.this, RescueDashboardActivity.class);
        intent.putExtra("post_id", postId);
        intent.putExtra("post_title", postTitle);
        intent.putExtra("location", postLocation);
        startActivity(intent);
    }
}

