package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RescuePostAdapter.OnPostClickListener {

    private CardView btnProfile;
    private ImageView btnNotifications;
    private TextView tvSosCount, tvRescuedCount, tvTotalCount;
    private MaterialButton btnAboutUs, btnTeam;
    private RecyclerView recyclerViewRescuePosts;
    private FloatingActionButton fabAddPost;

    private RescuePostAdapter adapter;
    private List<RescuePost> rescuePosts;
    private ActivityResultLauncher<Intent> addPostLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Đăng ký Activity Result Launcher cho đăng bài
        addPostLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    // Refresh danh sách khi đăng bài thành công
                    loadRescuePosts();
                    updateStatistics();
                    Toast.makeText(this, "Đã tải lại danh sách", Toast.LENGTH_SHORT).show();
                }
            }
        );

        initViews();
        setupRecyclerView();
        loadRescuePosts();
        updateStatistics();
    }

    private void initViews() {
        btnProfile = findViewById(R.id.btnProfile);
        btnNotifications = findViewById(R.id.btnNotifications);
        tvSosCount = findViewById(R.id.tvSosCount);
        tvRescuedCount = findViewById(R.id.tvRescuedCount);
        tvTotalCount = findViewById(R.id.tvTotalCount);
        btnAboutUs = findViewById(R.id.btnAboutUs);
        btnTeam = findViewById(R.id.btnTeam);
        recyclerViewRescuePosts = findViewById(R.id.recyclerViewRescuePosts);
        fabAddPost = findViewById(R.id.fabAddPost);

        // Set click listeners
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(intent);
        });

        // Nút Notifications
        if (btnNotifications != null) {
            btnNotifications.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, NotificationsActivity.class);
                startActivity(intent);
            });
        }

        btnAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrangVeChungToiActivity.class);
            startActivity(intent);
        });

        btnTeam.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrangDoiNguActivity.class);
            startActivity(intent);
        });

        // Dùng ActivityResultLauncher thay vì startActivity thường
        fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TrangDangBaiActivity.class);
            addPostLauncher.launch(intent);
        });
    }

    private void setupRecyclerView() {
        rescuePosts = new ArrayList<>();
        adapter = new RescuePostAdapter(this, rescuePosts, this);
        recyclerViewRescuePosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRescuePosts.setAdapter(adapter);
    }

    private void loadRescuePosts() {
        // Sample data - replace with actual database/API data
        rescuePosts.clear();

        rescuePosts.add(new RescuePost(
                "1",
                "Phát hiện chó con bị thương ở chân, cần cứu hộ gấp!",
                "Số 123 Đường Lê Lợi, Quận Hải Châu",
                "🐕",
                "Chờ cứu",
                "2 giờ trước",
                R.drawable.cho,
                "user1",
                "Nguyễn Văn A"
        ));

        rescuePosts.add(new RescuePost(
                "2",
                "Mèo con bị bỏ rơi trong thùng carton, đang đói lạnh",
                "Gần chợ Hàn, Đà Nẵng",
                "🐱",
                "Đang xử lý",
                "5 giờ trước",
                R.drawable.meo,
                "user2",
                "Trần Thị B"
        ));

        rescuePosts.add(new RescuePost(
                "3",
                "Chó lớn bị xe đâm, cần đưa đi bệnh viện khẩn cấp",
                "Đường Nguyễn Văn Linh, Thanh Khê",
                "🐕",
                "Đã cứu",
                "1 ngày trước",
                R.drawable.cuucho,
                "user3",
                "Lê Văn C"
        ));

        adapter.notifyDataSetChanged();
    }

    private void updateStatistics() {
        // Count posts by status
        int sosCount = 0;
        int rescuedCount = 0;

        for (RescuePost post : rescuePosts) {
            if (post.getStatus().equals("Chờ cứu")) {
                sosCount++;
            } else if (post.getStatus().equals("Đã cứu")) {
                rescuedCount++;
            }
        }

        tvSosCount.setText(String.valueOf(sosCount));
        tvRescuedCount.setText(String.valueOf(rescuedCount));
        tvTotalCount.setText(String.valueOf(rescuePosts.size()));
    }

    @Override
    public void onPostClick(RescuePost post) {
        // Navigate to post detail screen
        Intent intent = new Intent(MainActivity.this, PostDetailActivity.class);
        intent.putExtra("post_id", post.getId());
        intent.putExtra("description", post.getDescription());
        intent.putExtra("location", post.getLocation());
        intent.putExtra("status", post.getStatus());
        intent.putExtra("phone", "0123456789"); // TODO: Get from post or user
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        loadRescuePosts();
        updateStatistics();
    }
}