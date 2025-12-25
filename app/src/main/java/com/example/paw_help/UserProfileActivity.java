package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView btnBack, imgUserAvatar;
    private TextView tvUserName, tvUserEmail, tvUserPhone, tvAvatarInitials;
    private Button btnEditProfile, btnLogout;
    private LinearLayout passwordSection;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initViews();
        setupListeners();
        loadUserData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        passwordSection = findViewById(R.id.passwordSection);
        retrofitClient = RetrofitClient.getInstance(this);
    }

    private void setupListeners() {
        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Edit profile button
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(UserProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        });

        // Logout button
        btnLogout.setOnClickListener(v -> showLogoutDialog());
        
        // Password section - click to change password
        if (passwordSection != null) {
            passwordSection.setOnClickListener(v -> showChangePasswordDialog());
        }
    }
    
    private void showChangePasswordDialog() {
        // Inflate dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null);
        
        EditText edtCurrentPassword = dialogView.findViewById(R.id.edtCurrentPassword);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmPassword);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        TextView tvError = dialogView.findViewById(R.id.tvError);
        
        // Material Design TextInputLayout already has built-in password toggle
        // No need for custom toggle setup
        
        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("Đổi mật khẩu")
            .setView(dialogView)
            .setPositiveButton("Đổi mật khẩu", null)
            .setNegativeButton("Hủy", (d, which) -> d.dismiss())
            .create();
        
        dialog.setOnShowListener(dialogInterface -> {
            Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(v -> {
                String currentPassword = edtCurrentPassword.getText().toString().trim();
                String newPassword = edtNewPassword.getText().toString().trim();
                String confirmPassword = edtConfirmPassword.getText().toString().trim();
                
                // Validation
                if (TextUtils.isEmpty(currentPassword)) {
                    showError(tvError, "Vui lòng nhập mật khẩu hiện tại");
                    return;
                }
                
                if (TextUtils.isEmpty(newPassword)) {
                    showError(tvError, "Vui lòng nhập mật khẩu mới");
                    return;
                }
                
                if (newPassword.length() < 6) {
                    showError(tvError, "Mật khẩu mới phải có ít nhất 6 ký tự");
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    showError(tvError, "Mật khẩu xác nhận không khớp");
                    return;
                }
                
                // Hide error and show loading
                hideError(tvError);
                showLoading(progressBar, positiveButton);
                
                // Call API
                changePassword(currentPassword, newPassword, dialog, progressBar, positiveButton, tvError);
            });
        });
        
        dialog.show();
    }
    
    private void changePassword(String currentPassword, String newPassword, 
                                AlertDialog dialog, ProgressBar progressBar, 
                                Button positiveButton, TextView tvError) {
        PawHelpApi api = retrofitClient.getApi();
        
        Call<ApiResponse<Object>> call = api.changePassword(currentPassword, newPassword);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call, Response<ApiResponse<Object>> response) {
                hideLoading(progressBar, positiveButton);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String message = response.body().getMessage();
                    if (message == null || message.isEmpty()) {
                        message = "Đổi mật khẩu thành công";
                    }
                    Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    String errorMsg = "Đổi mật khẩu thất bại";
                    if (response.body() != null) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            errorMsg = response.body().getMessage();
                        } else if (response.body().getErrors() != null && !response.body().getErrors().isEmpty()) {
                            errorMsg = response.body().getErrors().get(0);
                        }
                    }
                    
                    if (response.code() == 401) {
                        errorMsg = "Mật khẩu hiện tại không đúng";
                    } else if (response.code() == 400) {
                        errorMsg = "Mật khẩu mới không hợp lệ. Vui lòng kiểm tra lại.";
                    }
                    
                    showError(tvError, errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                hideLoading(progressBar, positiveButton);
                
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc dữ liệu di động.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
                }
                
                showError(tvError, errorMsg);
            }
        });
    }
    
    private void showLoading(ProgressBar progressBar, Button button) {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (button != null) {
            button.setEnabled(false);
            button.setText("Đang xử lý...");
        }
    }
    
    private void hideLoading(ProgressBar progressBar, Button button) {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (button != null) {
            button.setEnabled(true);
            button.setText("Đổi mật khẩu");
        }
    }
    
    private void showError(TextView tvError, String message) {
        if (tvError != null) {
            tvError.setText(message);
            tvError.setVisibility(View.VISIBLE);
        }
    }
    
    private void hideError(TextView tvError) {
        if (tvError != null) {
            tvError.setVisibility(View.GONE);
        }
    }

    private void loadUserData() {
        PawHelpApi api = retrofitClient.getApi();

        Call<ApiResponse<User>> call = api.getProfile();
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        updateUIWithUserData(user);
                        // Lưu user vào RetrofitClient
                        retrofitClient.saveUser(user);
                        return;
                    }
                }

                // Fallback: Sử dụng data từ RetrofitClient nếu gọi API lỗi
                loadUserDataFromCache();
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                // Sử dụng data từ RetrofitClient cache
                loadUserDataFromCache();
                Toast.makeText(UserProfileActivity.this, 
                    "Không thể tải thông tin. Đang hiển thị thông tin đã lưu.", 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIWithUserData(User user) {
        // Tên người dùng
        String fullName = user.getFullName();
        tvUserName.setText(fullName != null && !fullName.isEmpty() ? fullName : "Người dùng");

        // Email
        tvUserEmail.setText(user.getEmail() != null ? user.getEmail() : "");

        // Số điện thoại
        String phone = user.getPhone();
        tvUserPhone.setText(phone != null && !phone.isEmpty() ? phone : "Chưa cập nhật");

        // Avatar
        String avatarUrl = user.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String baseUrl = retrofitClient.getImageBaseUrl();
            String fullImageUrl = avatarUrl.startsWith("http") 
                ? avatarUrl 
                : baseUrl + avatarUrl;

            // Show avatar image, hide initials
            imgUserAvatar.setVisibility(android.view.View.VISIBLE);
            tvAvatarInitials.setVisibility(android.view.View.GONE);

            Glide.with(UserProfileActivity.this)
                .load(fullImageUrl)
                .placeholder(R.drawable.avatar_gradient_background)
                .error(R.drawable.avatar_gradient_background)
                .circleCrop()
                .into(imgUserAvatar);
        } else {
            // Show initials, hide avatar image
            imgUserAvatar.setVisibility(android.view.View.GONE);
            tvAvatarInitials.setVisibility(android.view.View.VISIBLE);
            
            // Generate initials from full name
            String initials = generateInitials(fullName);
            tvAvatarInitials.setText(initials);
        }
    }

    private void loadUserDataFromCache() {
        // Load từ RetrofitClient
        String userName = retrofitClient.getUserName();
        String userEmail = retrofitClient.getUserEmail();

        tvUserName.setText(userName != null && !userName.isEmpty() ? userName : "Người dùng");
        tvUserEmail.setText(userEmail != null && !userEmail.isEmpty() ? userEmail : "");

        // Phone không có trong RetrofitClient, để mặc định
        tvUserPhone.setText("Chưa cập nhật");

        // Show initials for cached data
        imgUserAvatar.setVisibility(android.view.View.GONE);
        tvAvatarInitials.setVisibility(android.view.View.VISIBLE);
        String initials = generateInitials(userName);
        tvAvatarInitials.setText(initials);
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
            // Only one word, take first 2 characters
            String first = parts[0];
            if (first.length() >= 2) {
                return first.substring(0, 2).toUpperCase();
            }
            return first.substring(0, 1).toUpperCase();
        }

        // Multiple words, take first letter of first and last word
        String firstInitial = parts[0].substring(0, 1).toUpperCase();
        String lastInitial = parts[parts.length - 1].substring(0, 1).toUpperCase();
        return firstInitial + lastInitial;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Đăng xuất")
            .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
            .setPositiveButton("Đăng xuất", (dialog, which) -> performLogout())
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void performLogout() {
        // Clear user session using RetrofitClient
        retrofitClient.logout();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Navigate to login screen - clear task stack to prevent back navigation
        Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning from EditProfileActivity
        loadUserData();
    }
}
