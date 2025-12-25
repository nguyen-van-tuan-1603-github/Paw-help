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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

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

public class MainActivity extends AppCompatActivity implements RescuePostAdapter.OnPostClickListener {

    private CardView btnProfile;
    private ImageView btnNotifications;
    private TextView tvSosCount, tvRescuedCount, tvTotalCount;
    private MaterialButton btnAboutUs, btnTeam;
    private RecyclerView recyclerViewRescuePosts;
    private ExtendedFloatingActionButton fabAddPost;
    private SwipeRefreshLayout swipeRefreshLayout;
    private android.widget.ProgressBar progressBar;
    private android.widget.LinearLayout layoutEmptyState, layoutErrorState;
    private TextView tvErrorMessage;
    private MaterialButton btnRefresh, btnRetry;

    private RescuePostAdapter adapter;
    private List<RescuePost> rescuePosts;
    private ActivityResultLauncher<Intent> addPostLauncher;
    private RetrofitClient retrofitClient;

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
                });

        // Khởi tạo RetrofitClient
        retrofitClient = RetrofitClient.getInstance(this);
        
        // Kiểm tra đăng nhập - nếu chưa đăng nhập thì chuyển về LoginActivity
        if (!retrofitClient.isLoggedIn()) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutErrorState = findViewById(R.id.layoutErrorState);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnRetry = findViewById(R.id.btnRetry);

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

        // SwipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(
            getResources().getColor(android.R.color.holo_blue_bright),
            getResources().getColor(android.R.color.holo_green_light),
            getResources().getColor(android.R.color.holo_orange_light),
            getResources().getColor(android.R.color.holo_red_light)
        );
        swipeRefreshLayout.setOnRefreshListener(() -> {
            loadRescuePosts();
            updateStatistics();
        });

        // Retry buttons
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> {
                layoutEmptyState.setVisibility(android.view.View.GONE);
                loadRescuePosts();
            });
        }

        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> {
                layoutErrorState.setVisibility(android.view.View.GONE);
                loadRescuePosts();
            });
        }
    }

    private void setupRecyclerView() {
        rescuePosts = new ArrayList<>();
        adapter = new RescuePostAdapter(this, rescuePosts, this);
        recyclerViewRescuePosts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewRescuePosts.setAdapter(adapter);
    }

    private void loadRescuePosts() {
        // Show loading
        showLoadingState();

        // Gọi API để load posts
        Call<ApiResponse<PostListResponse>> call = retrofitClient.getApi().getPosts(1, 20, null);

        call.enqueue(new Callback<ApiResponse<PostListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostListResponse>> call,
                    Response<ApiResponse<PostListResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                hideLoadingState();

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostListResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        PostListResponse postListResponse = apiResponse.getData();

                        // Clear và convert posts từ API sang RescuePost
                        rescuePosts.clear();

                        if (postListResponse != null && postListResponse.getItems() != null && !postListResponse.getItems().isEmpty()) {
                            for (PostItem item : postListResponse.getItems()) {
                                // Convert PostItem từ API sang RescuePost
                                // animalType giờ là String từ API
                                String animalType = item.getAnimalType() != null ? item.getAnimalType() : "Chưa xác định";
                                String emoji = getEmojiForAnimalType(animalType);
                                String statusVN = convertStatus(item.getStatus());

                                // Dùng description thay vì title (API không có title)
                                String description = item.getDescription();
                                if (description == null || description.isEmpty()) {
                                    description = "Phát hiện động vật cần cứu hộ";
                                }

                                // Lấy user info từ PostItem (API trả về trực tiếp)
                                String userId = item.getUserId() != null ? String.valueOf(item.getUserId()) : "0";
                                String userName = item.getUserName() != null ? item.getUserName() : "Người dùng";
                                String userAvatar = item.getUserAvatar(); // Avatar URL từ API

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
                                    userAvatar); // User avatar URL

                            rescuePosts.add(post);
                            }
                            showContentState();
                            adapter.notifyDataSetChanged();
                        } else {
                            showEmptyState();
                        }
                    } else {
                        showErrorState("Không thể tải dữ liệu. Vui lòng thử lại sau.");
                    }
                } else {
                    showErrorState("Lỗi kết nối server. Vui lòng kiểm tra kết nối mạng.");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostListResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                hideLoadingState();
                
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc dữ liệu di động.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
                }
                
                showErrorState(errorMsg);
            }
        });
    }

    private void showLoadingState() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.VISIBLE);
        if (recyclerViewRescuePosts != null) recyclerViewRescuePosts.setVisibility(android.view.View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(android.view.View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(android.view.View.GONE);
    }

    private void hideLoadingState() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
    }

    private void showContentState() {
        if (recyclerViewRescuePosts != null) recyclerViewRescuePosts.setVisibility(android.view.View.VISIBLE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(android.view.View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(android.view.View.GONE);
    }

    private void showEmptyState() {
        if (recyclerViewRescuePosts != null) recyclerViewRescuePosts.setVisibility(android.view.View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(android.view.View.VISIBLE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(android.view.View.GONE);
    }

    private void showErrorState(String message) {
        if (recyclerViewRescuePosts != null) recyclerViewRescuePosts.setVisibility(android.view.View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(android.view.View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(android.view.View.VISIBLE);
        if (tvErrorMessage != null) tvErrorMessage.setText(message);
    }

    // Helper methods
    private String convertStatus(String status) {
        if (status == null) return "Chưa xác định";
        
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ xử lý";
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

    private String getEmojiForAnimalType(String animalType) {
        if (animalType == null) return "🐾";
        
        String type = animalType.toLowerCase();
        if (type.contains("chó") || type.contains("dog")) return "🐕";
        if (type.contains("mèo") || type.contains("cat")) return "🐈";
        if (type.contains("chim") || type.contains("bird")) return "🐦";
        if (type.contains("thỏ") || type.contains("rabbit")) return "🐰";
        return "🐾";
    }

    private String formatTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            return "Vừa xong";
        }
        
        try {
            // Parse ISO 8601 format từ MySQL: "2024-01-15T10:30:00.000Z"
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

    private void updateStatistics() {
        // Gọi API để lấy thống kê
        Call<ApiResponse<DashboardStats>> call = retrofitClient.getApi().getDashboardStats();

        call.enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call,
                    Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<DashboardStats> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        DashboardStats stats = apiResponse.getData();

                        tvSosCount.setText(String.valueOf(stats.getSosCount()));
                        tvRescuedCount.setText(String.valueOf(stats.getRescuedCount()));
                        tvTotalCount.setText(String.valueOf(stats.getTotalPosts()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                // Không hiển thị lỗi cho stats, chỉ log
            }
        });
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