package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.DashboardStats;
import com.example.paw_help.models.PostItem;
import com.example.paw_help.models.PostListResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        // Load pending posts (chờ cứu)
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        // Load pending posts
        Call<ApiResponse<PostListResponse>> pendingCall = api.getPosts(1, 20, "pending");
        pendingCall.enqueue(new Callback<ApiResponse<PostListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostListResponse>> call,
                                   Response<ApiResponse<PostListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PostListResponse postListResponse = response.body().getData();
                    rescuePosts.clear();

                    if (postListResponse != null && postListResponse.getItems() != null) {
                        for (PostItem item : postListResponse.getItems()) {
                            String emoji = item.getAnimalType() != null ? item.getAnimalType().getTypeEmoji() : "🐾";
                            String statusVN = convertStatus(item.getStatus());
                            String userName = item.getUser() != null ? item.getUser().getFullName() : "Người dùng";
                            String userId = item.getUser() != null ? String.valueOf(item.getUser().getUserId()) : "0";

                            RescuePost post = new RescuePost(
                                    String.valueOf(item.getPostId()),
                                    item.getDescription() != null ? item.getDescription() : item.getTitle(),
                                    item.getLocation(),
                                    emoji,
                                    statusVN,
                                    formatTime(item.getCreatedAt()),
                                    R.drawable.cho, // Default image, sẽ load từ URL sau
                                    userId,
                                    userName
                            );
                            rescuePosts.add(post);
                        }
                    }

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(RescueDashboardActivity.this, "Không thể tải danh sách bài đăng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostListResponse>> call, Throwable t) {
                Toast.makeText(RescueDashboardActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String convertStatus(String status) {
        switch (status) {
            case "pending":
                return "Chờ cứu";
            case "in_progress":
                return "Đang xử lý";
            case "rescued":
                return "Đã cứu";
            case "closed":
                return "Đã đóng";
            default:
                return status;
        }
    }

    private String formatTime(String createdAt) {
        // Tạm thời trả về string đơn giản, sau này có thể format đẹp hơn
        return "Vừa xong";
    }

    private void updateStatistics() {
        // Gọi API để lấy thống kê user
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<DashboardStats>> call = api.getUserStats();
        call.enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call,
                                   Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardStats stats = response.body().getData();
                    if (stats != null) {
                        // pendingPosts là số bài đăng chờ cứu
                        tvNewCount.setText(String.valueOf(stats.getPendingPosts()));
                        // inProgressPosts là số bài đăng đang xử lý
                        tvProcessingCount.setText(String.valueOf(stats.getInProgressPosts()));
                    }
                } else {
                    // Fallback: tính từ local data
                    calculateLocalStats();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                // Fallback: tính từ local data
                calculateLocalStats();
            }
        });
    }

    private void calculateLocalStats() {
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

