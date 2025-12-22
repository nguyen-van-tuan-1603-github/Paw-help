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
    private FloatingActionButton fabAddPost;

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
        // Gọi API để load posts
        Call<ApiResponse<PostListResponse>> call = retrofitClient.getApi().getPosts(1, 20, null);

        call.enqueue(new Callback<ApiResponse<PostListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostListResponse>> call,
                    Response<ApiResponse<PostListResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<PostListResponse> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        PostListResponse postListResponse = apiResponse.getData();

                        // Clear và convert posts từ API sang RescuePost
                        rescuePosts.clear();

                        for (PostItem item : postListResponse.getItems()) {
                            // Convert PostItem từ API sang RescuePost
                            String emoji = item.getAnimalType() != null ? item.getAnimalType().getTypeEmoji() : "🐾";
                            String statusVN = convertStatus(item.getStatus());

                            RescuePost post = new RescuePost(
                                    String.valueOf(item.getPostId()),
                                    item.getTitle(),
                                    item.getLocation(),
                                    emoji,
                                    statusVN,
                                    formatTime(item.getCreatedAt()),
                                    R.drawable.cho, // Default image, sau này có thể load từ URL
                                    String.valueOf(item.getUser().getUserId()),
                                    item.getUser().getFullName());

                            rescuePosts.add(post);
                        }

                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MainActivity.this, "Không thể tải dữ liệu", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostListResponse>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Helper methods
    private String convertStatus(String status) {
        switch (status) {
            case "waiting":
                return "Chờ cứu";
            case "processing":
                return "Đang xử lý";
            case "rescued":
                return "Đã cứu";
            case "cancelled":
                return "Đã hủy";
            default:
                return status;
        }
    }

    private String formatTime(String createdAt) {
        // Tạm thời trả về string đơn giản, sau này có thể format đẹp hơn
        return "Vừa xong";
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