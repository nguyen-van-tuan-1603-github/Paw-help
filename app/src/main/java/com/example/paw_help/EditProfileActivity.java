package com.example.paw_help;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnEditAvatar, imgUserAvatar;
    private EditText edtFullName, edtPhone;
    private TextView tvEmail, tvErrorMessage;
    private MaterialButton btnMale, btnFemale;
    private Button btnSaveChanges;
    private ProgressBar progressBar;

    private String selectedGender = "Nam";
    private Uri selectedAvatarUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        setupImagePicker();
        loadUserData();
        setupListeners();
        checkPermissions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEditAvatar = findViewById(R.id.btnEditAvatar);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        tvEmail = findViewById(R.id.tvEmail);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        progressBar = findViewById(R.id.progressBar);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
    }
    
    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedAvatarUri = result.getData().getData();
                    if (selectedAvatarUri != null) {
                        // Sử dụng Glide để load ảnh thay vì deprecated MediaStore.Images.Media.getBitmap
                        Glide.with(EditProfileActivity.this)
                            .load(selectedAvatarUri)
                            .into(imgUserAvatar);
                    }
                }
            }
        );
    }
    
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                100);
        }
    }

    private void loadUserData() {
        // Load user data from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
        String fullName = prefs.getString("user_name", "");
        String phone = prefs.getString("user_phone", "");
        String email = prefs.getString("user_email", "user@example.com");
        selectedGender = prefs.getString("user_gender", "Nam");

        edtFullName.setText(fullName);
        edtPhone.setText(phone);
        tvEmail.setText(email);

        // Set gender button state
        updateGenderButtons();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditAvatar.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            imagePickerLauncher.launch(intent);
        });

        btnMale.setOnClickListener(v -> {
            selectedGender = "Nam";
            updateGenderButtons();
        });

        btnFemale.setOnClickListener(v -> {
            selectedGender = "Nữ";
            updateGenderButtons();
        });

        btnSaveChanges.setOnClickListener(v -> saveChanges());
    }

    private void updateGenderButtons() {
        if (selectedGender.equals("Nam")) {
            btnMale.setBackgroundColor(ContextCompat.getColor(this, R.color.button_primary));
            btnMale.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnFemale.setBackgroundColor(0xFFF5F5F5);
            btnFemale.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        } else {
            btnFemale.setBackgroundColor(ContextCompat.getColor(this, R.color.button_primary));
            btnFemale.setTextColor(ContextCompat.getColor(this, R.color.white));
            btnMale.setBackgroundColor(0xFFF5F5F5);
            btnMale.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
        }
    }

    private void saveChanges() {
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Vui lòng nhập tên");
            edtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            edtPhone.setError("Số điện thoại không hợp lệ");
            edtPhone.requestFocus();
            return;
        }

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        // Hide error message
        hideError();
        
        // Show loading
        showLoading();
        
        btnSaveChanges.setEnabled(false);

        Call<ApiResponse<User>> call;
        
        if (selectedAvatarUri != null) {
            // Upload với avatar
            try {
                RequestBody fullNamePart = RequestBody.create(MediaType.parse("text/plain"), fullName);
                RequestBody phonePart = RequestBody.create(MediaType.parse("text/plain"), phone);
                MultipartBody.Part avatarPart = createImagePart(selectedAvatarUri);
                
                call = api.updateProfileWithAvatar(fullNamePart, phonePart, avatarPart);
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showError("Lỗi khi xử lý ảnh: " + e.getMessage());
                btnSaveChanges.setEnabled(true);
                return;
            }
        } else {
            // Update không có avatar
            call = api.updateProfile(fullName, phone);
        }

        call.enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call,
                                   Response<ApiResponse<User>> response) {
                hideLoading();
                btnSaveChanges.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    
                    // Cập nhật local
                    SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("user_name", fullName);
                    editor.putString("user_phone", phone);
                    editor.putString("user_gender", selectedGender);
                    if (user != null && user.getAvatarUrl() != null) {
                        editor.putString("user_avatar", user.getAvatarUrl());
                    }
                    editor.apply();
                    
                    // Cập nhật RetrofitClient
                    if (user != null) {
                        client.saveUser(user);
                    }

                    Toast.makeText(EditProfileActivity.this, "Đã lưu thông tin thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMsg = "Cập nhật thất bại";
                    if (response.body() != null && response.body().getMessage() != null) {
                        errorMsg = response.body().getMessage();
                    }
                    if (response.code() == 401) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                    } else if (response.code() == 400) {
                        errorMsg = "Thông tin không hợp lệ. Vui lòng kiểm tra lại.";
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                hideLoading();
                btnSaveChanges.setEnabled(true);
                
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng kiểm tra kết nối mạng.";
                } else if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không thể kết nối đến server. Vui lòng kiểm tra cài đặt mạng.";
                } else {
                    errorMsg = "Lỗi: " + t.getMessage();
                }
                
                showError(errorMsg);
            }
        });
    }
    
    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.VISIBLE);
        if (btnSaveChanges != null) btnSaveChanges.setEnabled(false);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
    }

    private void showError(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(android.view.View.VISIBLE);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    private void hideError() {
        if (tvErrorMessage != null) {
            tvErrorMessage.setVisibility(android.view.View.GONE);
        }
    }

    private MultipartBody.Part createImagePart(Uri imageUri) throws IOException {
        InputStream inputStream = getContentResolver().openInputStream(imageUri);
        if (inputStream == null) {
            throw new IOException("Không thể đọc file ảnh");
        }
        
        // Đọc toàn bộ file vào byte array
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes);
        inputStream.close();
        
        // Tạo file tạm
        File tempFile = new File(getCacheDir(), "temp_avatar_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);
        fos.close();
        
        // Tạo RequestBody từ file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
        
        // Tạo MultipartBody.Part
        return MultipartBody.Part.createFormData("avatar", tempFile.getName(), requestFile);
    }
}

