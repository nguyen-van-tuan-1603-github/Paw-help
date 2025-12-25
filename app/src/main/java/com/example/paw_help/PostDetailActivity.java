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

import com.bumptech.glide.Glide;
import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.PostItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    private CardView btnBack, btnShare, btnCall, btnReport;
    private ImageView imgPostPhoto, imgUserAvatar;
    private TextView tvStatus, tvTitle, tvLocation, tvPosterName, tvPosterPhone, tvUserAvatarInitials;
    private Button btnRescue, btnUpdateStatus;

    private String postId;
    private String postTitle;
    private String postLocation;
    private String postStatus;
    private String contactPhone;
    private String postImageUrl;
    private Integer postUserId; // User ID của người đăng bài
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        initViews();
        loadPostFromApi();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnShare = findViewById(R.id.btnShare);
        btnCall = findViewById(R.id.btnCall);
        btnReport = findViewById(R.id.btnReport);
        imgPostPhoto = findViewById(R.id.imgPostPhoto);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvStatus = findViewById(R.id.tvStatus);
        tvTitle = findViewById(R.id.tvTitle);
        tvLocation = findViewById(R.id.tvLocation);
        tvPosterName = findViewById(R.id.tvPosterName);
        tvPosterPhone = findViewById(R.id.tvPosterPhone);
        tvUserAvatarInitials = findViewById(R.id.tvUserAvatarInitials);
        btnRescue = findViewById(R.id.btnRescue);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        
        retrofitClient = RetrofitClient.getInstance(this);
    }

    private void loadPostFromApi() {
        Intent intent = getIntent();
        String idStr = intent.getStringExtra("post_id");
        if (idStr == null) {
            Toast.makeText(this, "Không tìm thấy ID bài đăng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID bài đăng không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<PostItem>> call = api.getPost(id);
        call.enqueue(new Callback<ApiResponse<PostItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostItem>> call,
                                   Response<ApiResponse<PostItem>> response) {
                if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                    Toast.makeText(PostDetailActivity.this, "Không tải được chi tiết bài đăng", Toast.LENGTH_SHORT).show();
                    return;
                }

                PostItem item = response.body().getData();
                if (item == null) {
                    Toast.makeText(PostDetailActivity.this, "Bài đăng không tồn tại", Toast.LENGTH_SHORT).show();
                    return;
                }

                postId = String.valueOf(item.getPostId());
                postTitle = item.getDescription();
                postLocation = item.getLocation();
                postStatus = item.getStatus();
                postUserId = item.getUserId(); // Lưu user ID của người đăng bài
                
                // Lấy phone từ userPhone (API trả về trực tiếp trong PostItem)
                contactPhone = item.getUserPhone();
                if (contactPhone == null || contactPhone.isEmpty()) {
                    // Fallback nếu không có phone
                    contactPhone = "Chưa có số điện thoại";
                }
                
                postImageUrl = item.getImageUrl();

                if (postTitle == null || postTitle.isEmpty()) {
                    postTitle = "Phát hiện động vật cần cứu hộ";
                }
                if (postLocation == null || postLocation.isEmpty()) {
                    postLocation = "Chưa có địa chỉ";
                }
                if (postStatus == null || postStatus.isEmpty()) {
                    postStatus = "Chờ xử lý";
                }

                tvTitle.setText(postTitle);
                tvLocation.setText(postLocation);
                tvStatus.setText(postStatus);
                
                // Load ảnh bằng Glide
                if (postImageUrl != null && !postImageUrl.isEmpty()) {
                    String baseUrl = client.getImageBaseUrl();
                    String fullImageUrl = postImageUrl.startsWith("http") 
                        ? postImageUrl 
                        : baseUrl + postImageUrl;
                    
                    Glide.with(PostDetailActivity.this)
                        .load(fullImageUrl)
                        .placeholder(R.drawable.cho)
                        .error(R.drawable.cho)
                        .into(imgPostPhoto);
                } else {
                    imgPostPhoto.setImageResource(R.drawable.cho);
                }
                
                // Convert status sang tiếng Việt và update UI
                updateStatusBadge(postStatus);
                
                // Load user info
                loadUserInfo(item, client);
                
                // Check if current user can update status (owner or admin)
                checkAndShowUpdateStatusButton();
            }

            @Override
            public void onFailure(Call<ApiResponse<PostItem>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        
        // Update Status button
        if (btnUpdateStatus != null) {
            btnUpdateStatus.setOnClickListener(v -> showUpdateStatusDialog());
        }
    }
    
    private void checkAndShowUpdateStatusButton() {
        // Check if current user is logged in
        if (!retrofitClient.isLoggedIn()) {
            if (btnUpdateStatus != null) {
                btnUpdateStatus.setVisibility(android.view.View.GONE);
            }
            return;
        }
        
        // Get current user ID
        int currentUserId = retrofitClient.getUserId();
        String currentUserRole = retrofitClient.getUserRole(); // Get from RetrofitClient or SharedPreferences
        
        // Show update button if:
        // 1. Current user is the post owner (postUserId == currentUserId), OR
        // 2. Current user is admin
        boolean canUpdate = false;
        if (postUserId != null && currentUserId > 0 && postUserId == currentUserId) {
            canUpdate = true;
        } else if ("admin".equalsIgnoreCase(currentUserRole)) {
            canUpdate = true;
        }
        
        if (btnUpdateStatus != null) {
            btnUpdateStatus.setVisibility(canUpdate ? android.view.View.VISIBLE : android.view.View.GONE);
        }
    }
    
    private void showUpdateStatusDialog() {
        String[] statusOptions = {
            "Chờ xử lý (pending)",
            "Đang xử lý (in_progress)",
            "Đã cứu (rescued)",
            "Đã đóng (closed)"
        };
        
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Cập nhật trạng thái")
            .setItems(statusOptions, (dialog, which) -> {
                String selectedStatus = "";
                String displayStatus = "";
                
                switch (which) {
                    case 0:
                        selectedStatus = "pending";
                        displayStatus = "Chờ xử lý";
                        break;
                    case 1:
                        selectedStatus = "in_progress";
                        displayStatus = "Đang xử lý";
                        break;
                    case 2:
                        selectedStatus = "rescued";
                        displayStatus = "Đã cứu";
                        break;
                    case 3:
                        selectedStatus = "closed";
                        displayStatus = "Đã đóng";
                        break;
                }
                
                updatePostStatus(selectedStatus, displayStatus);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void updatePostStatus(String status, String displayStatus) {
        if (postId == null) {
            Toast.makeText(this, "Không tìm thấy ID bài đăng", Toast.LENGTH_SHORT).show();
            return;
        }
        
        int postIdInt;
        try {
            postIdInt = Integer.parseInt(postId);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "ID bài đăng không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        PawHelpApi api = retrofitClient.getApi();
        
        Call<ApiResponse<Object>> call = api.updatePostStatus(postIdInt, status);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Update UI
                    postStatus = status;
                    updateStatusBadge(status);
                    
                    String message = response.body().getMessage();
                    if (message == null || message.isEmpty()) {
                        message = "Cập nhật trạng thái thành công";
                    }
                    Toast.makeText(PostDetailActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    String errorMsg = "Cập nhật trạng thái thất bại";
                    if (response.body() != null) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            errorMsg = response.body().getMessage();
                        } else if (response.body().getErrors() != null && !response.body().getErrors().isEmpty()) {
                            errorMsg = response.body().getErrors().get(0);
                        }
                    }
                    
                    if (response.code() == 403) {
                        errorMsg = "Bạn không có quyền cập nhật bài đăng này";
                    } else if (response.code() == 404) {
                        errorMsg = "Bài đăng không tồn tại";
                    } else if (response.code() == 400) {
                        errorMsg = "Trạng thái không hợp lệ";
                    }
                    
                    Toast.makeText(PostDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc dữ liệu di động.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
                }
                Toast.makeText(PostDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
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
        if (contactPhone == null || contactPhone.equals("Chưa có số điện thoại")) {
            Toast.makeText(this, "Không có số điện thoại liên hệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + contactPhone));
        startActivity(callIntent);
    }
    
    private String convertStatusToVietnamese(String status) {
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
    
    private void updateStatusBadge(String status) {
        String statusText = convertStatusToVietnamese(status);
        tvStatus.setText(statusText);
        
        // Update status badge color based on status
        android.view.View statusParent = (android.view.View) tvStatus.getParent();
        if (statusParent instanceof android.widget.LinearLayout) {
            android.widget.LinearLayout statusLayout = (android.widget.LinearLayout) statusParent;
            
            int backgroundColor = 0xFFF3E0; // Default orange
            int textColor = 0xFF9800; // Default orange text
            
            if (status != null) {
                switch (status.toLowerCase()) {
                    case "pending":
                        backgroundColor = 0xFFF3E0; // Orange
                        textColor = 0xFF9800;
                        break;
                    case "in_progress":
                        backgroundColor = 0xE3F2FD; // Blue
                        textColor = 0x2196F3;
                        break;
                    case "rescued":
                        backgroundColor = 0xE8F5E9; // Green
                        textColor = 0x4CAF50;
                        break;
                    case "closed":
                        backgroundColor = 0xF5F5F5; // Gray
                        textColor = 0x757575;
                        break;
                }
            }
            
            statusLayout.setBackgroundTintList(android.content.res.ColorStateList.valueOf(backgroundColor));
            tvStatus.setTextColor(textColor);
            
            // Update icon color too
            if (statusLayout.getChildAt(0) instanceof android.widget.ImageView) {
                android.widget.ImageView icon = (android.widget.ImageView) statusLayout.getChildAt(0);
                icon.setColorFilter(textColor);
            }
        }
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

    private void loadUserInfo(PostItem item, RetrofitClient client) {
        // Get user info from PostItem
        String userName = item.getUserName();
        String userAvatar = item.getUserAvatar();
        String userPhone = item.getUserPhone();

        // Set poster name
        if (userName != null && !userName.isEmpty()) {
            tvPosterName.setText(userName);
        } else {
            tvPosterName.setText("Người dùng");
        }

        // Set poster phone
        if (userPhone != null && !userPhone.isEmpty()) {
            tvPosterPhone.setText(userPhone);
        } else {
            tvPosterPhone.setText("Chưa có số điện thoại");
        }

        // Load user avatar
        if (userAvatar != null && !userAvatar.isEmpty()) {
            String baseUrl = client.getImageBaseUrl();
            String fullImageUrl = userAvatar.startsWith("http") 
                ? userAvatar 
                : baseUrl + userAvatar;

            // Show avatar image, hide initials
            imgUserAvatar.setVisibility(android.view.View.VISIBLE);
            tvUserAvatarInitials.setVisibility(android.view.View.GONE);

            Glide.with(PostDetailActivity.this)
                .load(fullImageUrl)
                .placeholder(R.drawable.avatar_gradient_background)
                .error(R.drawable.avatar_gradient_background)
                .circleCrop()
                .into(imgUserAvatar);
        } else {
            // Show initials, hide avatar image
            imgUserAvatar.setVisibility(android.view.View.GONE);
            tvUserAvatarInitials.setVisibility(android.view.View.VISIBLE);

            // Generate initials from user name
            String initials = generateInitials(userName);
            tvUserAvatarInitials.setText(initials);
        }
    }

    private String generateInitials(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            return "U";
        }

        String[] parts = fullName.trim().split("\\s+");
        if (parts.length == 0) {
            return "U";
        }

        if (parts.length == 1) {
            String first = parts[0];
            if (first.length() >= 2) {
                return first.substring(0, 2).toUpperCase();
            }
            return first.substring(0, 1).toUpperCase();
        }

        String firstInitial = parts[0].substring(0, 1).toUpperCase();
        String lastInitial = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return firstInitial + lastInitial;
    }
}

