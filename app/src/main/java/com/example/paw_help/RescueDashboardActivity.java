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
                            // animalType giờ là String, không phải object
                            String animalType = item.getAnimalType() != null ? item.getAnimalType() : "Chưa xác định";
                            String emoji = getEmojiForAnimalType(animalType);
                            String statusVN = convertStatus(item.getStatus());
                            
                            // Lấy user info trực tiếp từ PostItem
                            String userName = item.getUserName() != null ? item.getUserName() : "Người dùng";
                            String userId = item.getUserId() != null ? String.valueOf(item.getUserId()) : "0";
                            String userAvatar = item.getUserAvatar(); // Avatar URL từ API
                            
                            String description = item.getDescription();
                            if (description == null || description.isEmpty()) {
                                description = "Phát hiện động vật cần cứu hộ";
                            }

                            RescuePost post = new RescuePost(
                                    String.valueOf(item.getPostId()),
                                    description,
                                    item.getLocation() != null ? item.getLocation() : "Chưa có địa chỉ",
                                    emoji,
                                    statusVN,
                                    formatTime(item.getCreatedAt()),
                                    R.drawable.cho, // Default image resource
                                    item.getImageUrl(), // Image URL từ server
                                    userId,
                                    userName,
                                    userAvatar // User avatar URL
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
        if (createdAt == null || createdAt.isEmpty()) {
            return "Vừa xong";
        }
        
        try {
            java.text.SimpleDateFormat inputFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault());
            java.util.Date date = inputFormat.parse(createdAt);
            
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (seconds < 60) {
                return "Vừa xong";
            } else if (minutes < 60) {
                return minutes + " phút trước";
            } else if (hours < 24) {
                return hours + " giờ trước";
            } else if (days < 7) {
                return days + " ngày trước";
            } else {
                java.text.SimpleDateFormat outputFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            return "Vừa xong";
        }
    }
    
    private String getEmojiForAnimalType(String animalType) {
        if (animalType == null) return "🐾";
        
        String type = animalType.toLowerCase();
        if (type.contains("chó") || type.contains("dog")) return "🐕";
        if (type.contains("mèo") || type.contains("cat")) return "🐈";
        if (type.contains("chim") || type.contains("bird")) return "🐦";
        if (type.contains("thỏ") || type.contains("rabbit")) return "🐰";
        return "🐾";
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

