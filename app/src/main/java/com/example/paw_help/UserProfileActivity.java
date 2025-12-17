package com.example.paw_help;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private ImageView btnBack, imgUserAvatar;
    private TextView tvUserEmail, tvUserPhone;
    private Button btnEditProfile, btnLogout;

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
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserPhone = findViewById(R.id.tvUserPhone);
        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
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
    }

    private void loadUserData() {
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<User>> call = api.getProfile();
        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        tvUserEmail.setText(user.getEmail());
                        String phone = user.getPhone();
                        tvUserPhone.setText(phone != null && !phone.isEmpty()
                                ? phone : "Chưa cập nhật");
                        return;
                    }
                }

                // Fallback: SharedPreferences nếu gọi API lỗi
                SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
                String email = prefs.getString("user_email", "abc123@gmail.com");
                String phone = prefs.getString("user_phone", "Chưa cập nhật");

                tvUserEmail.setText(email);
                tvUserPhone.setText(phone);
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
                String email = prefs.getString("user_email", "abc123@gmail.com");
                String phone = prefs.getString("user_phone", "Chưa cập nhật");

                tvUserEmail.setText(email);
                tvUserPhone.setText(phone);
            }
        });
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
        // Clear user session
        SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();

        // Navigate to welcome screen
        Intent intent = new Intent(UserProfileActivity.this, WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Đã đăng xuất thành công", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload user data when returning from EditProfileActivity
        loadUserData();
    }
}
