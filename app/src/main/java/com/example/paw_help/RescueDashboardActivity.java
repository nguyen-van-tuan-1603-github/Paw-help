package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class RescueDashboardActivity extends AppCompatActivity implements RescuePostAdapter.OnPostClickListener {

    private ImageView btnBack;
    private CardView btnNotifications;
    private TextView tvNewCount, tvProcessingCount;
    private RecyclerView recyclerViewRescuePosts;
    private FloatingActionButton fabAddPost;

    private RescuePostAdapter adapter;
    private List<RescuePost> rescuePosts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_dashboard);

        initViews();
        setupRecyclerView();
        loadRescuePosts();
        updateStatistics();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvNewCount = findViewById(R.id.tvNewCount);
        tvProcessingCount = findViewById(R.id.tvProcessingCount);
        recyclerViewRescuePosts = findViewById(R.id.recyclerViewRescuePosts);
        fabAddPost = findViewById(R.id.fabAddPost);
    }

    private void setupRecyclerView() {
        rescuePosts = new ArrayList<>();
        adapter = new RescuePostAdapter(this, rescuePosts, this);
        recyclerViewRescuePosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRescuePosts.setAdapter(adapter);
    }

    private void loadRescuePosts() {
        rescuePosts.clear();

        // Sample data
        rescuePosts.add(new RescuePost(
                "1",
                "Phát hiện một chú mèo con bị thương ở chân, đang nằm bên đường. Cần cứu hộ khẩn cấp",
                "Số 123 Đường Lê Lợi, Q. Hải Châu",
                "🐱",
                "Chờ cứu",
                "2 giờ trước",
                R.drawable.meo,
                "user1",
                "Nguyễn Văn A"
        ));

        rescuePosts.add(new RescuePost(
                "2",
                "Chó con bị bỏ rơi trong thùng carton, đang đói lạnh",
                "Gần chợ Hàn, Đà Nẵng",
                "🐕",
                "Đang xử lý",
                "5 giờ trước",
                R.drawable.cho,
                "user2",
                "Trần Thị B"
        ));

        rescuePosts.add(new RescuePost(
                "3",
                "Mèo mẹ và đàn con bị kẹt trong công trình xây dựng",
                "Đường Nguyễn Văn Linh, Thanh Khê",
                "🐱",
                "Chờ cứu",
                "1 ngày trước",
                R.drawable.meo,
                "user3",
                "Lê Văn C"
        ));

        adapter.notifyDataSetChanged();
    }

    private void updateStatistics() {
        int newCount = 0;
        int processingCount = 0;

        for (RescuePost post : rescuePosts) {
            if (post.getStatus().equals("Chờ cứu")) {
                newCount++;
            } else if (post.getStatus().equals("Đang xử lý")) {
                processingCount++;
            }
        }

        tvNewCount.setText(String.valueOf(newCount));
        tvProcessingCount.setText(String.valueOf(processingCount));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(RescueDashboardActivity.this, NotificationsActivity.class);
            startActivity(intent);
        });

        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(RescueDashboardActivity.this, TrangDangBaiActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onPostClick(RescuePost post) {
        Intent intent = new Intent(RescueDashboardActivity.this, PostDetailActivity.class);
        intent.putExtra("post_id", post.getId());
        intent.putExtra("description", post.getDescription());
        intent.putExtra("location", post.getLocation());
        intent.putExtra("status", post.getStatus());
        intent.putExtra("phone", "0123456789");
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRescuePosts();
        updateStatistics();
    }
}

