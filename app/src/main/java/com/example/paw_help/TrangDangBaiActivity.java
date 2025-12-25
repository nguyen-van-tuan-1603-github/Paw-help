package com.example.paw_help;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.CreatePostResponse;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangDangBaiActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView btnBack, imgPreview, btnUploadImage;
    private EditText edtTitle, edtLocation, edtDescription;
    private Button btnSubmitPost;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private Uri selectedImageUri;

    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_dang_bai);

        initViews();
        setupImagePicker();
        checkPermissions();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);
        btnUploadImage = findViewById(R.id.btnUploadImage);
        edtTitle = findViewById(R.id.edtTitle);
        edtLocation = findViewById(R.id.edtLocation);
        edtDescription = findViewById(R.id.edtDescription);
        btnSubmitPost = findViewById(R.id.btnSubmitPost);
        progressBar = findViewById(R.id.progressBar);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);

        btnBack.setOnClickListener(v -> finish());
        btnUploadImage.setOnClickListener(v -> openImagePicker());
        btnSubmitPost.setOnClickListener(v -> submitPost());
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), selectedImageUri);
                        imgPreview.setImageBitmap(bitmap);
                        imgPreview.setVisibility(ImageView.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi khi tải ảnh", Toast.LENGTH_SHORT).show();
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
                PERMISSION_REQUEST_CODE);
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void submitPost() {
        String title = edtTitle.getText().toString().trim();
        String location = edtLocation.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        // Validation - location và description là bắt buộc
        if (TextUtils.isEmpty(location)) {
            edtLocation.setError("Vui lòng nhập địa điểm");
            edtLocation.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            edtDescription.setError("Vui lòng nhập mô tả chi tiết");
            edtDescription.requestFocus();
            return;
        }

        // Kết hợp title và description nếu có title (API chỉ có description)
        String fullDescription = description;
        if (!TextUtils.isEmpty(title)) {
            fullDescription = title + "\n\n" + description;
        }

        // Loại động vật - mặc định là "Chó" (có thể mở rộng thêm spinner sau)
        String animalType = "Chó";

        // Kiểm tra đăng nhập
        RetrofitClient client = RetrofitClient.getInstance(this);
        if (!client.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng bài", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        PawHelpApi api = client.getApi();

        // Hide error message
        hideError();
        
        // Show loading
        showLoading();
        
        btnSubmitPost.setEnabled(false);
        btnSubmitPost.setText("Đang gửi...");

        Call<ApiResponse<CreatePostResponse>> call;
        
        if (selectedImageUri != null) {
            // Upload với ảnh - sử dụng Multipart
            try {
                MultipartBody.Part imagePart = createImagePart(selectedImageUri);
                
                // Tạo RequestBody cho các field
                RequestBody animalTypePart = RequestBody.create(MediaType.parse("text/plain"), animalType);
                RequestBody descriptionPart = RequestBody.create(MediaType.parse("text/plain"), fullDescription);
                RequestBody locationPart = RequestBody.create(MediaType.parse("text/plain"), location);
                // Latitude và longitude là optional - backend sẽ xử lý empty string thành null
                RequestBody latitudePart = RequestBody.create(MediaType.parse("text/plain"), "");
                RequestBody longitudePart = RequestBody.create(MediaType.parse("text/plain"), "");
                
                call = api.createPost(
                    animalTypePart,
                    descriptionPart,
                    locationPart,
                    latitudePart,
                    longitudePart,
                    imagePart
                );
            } catch (IOException e) {
                e.printStackTrace();
                hideLoading();
                showError("Lỗi khi xử lý ảnh: " + e.getMessage());
                btnSubmitPost.setEnabled(true);
                btnSubmitPost.setText("Đăng bài cứu hộ");
                return;
            }
        } else {
            // Upload không có ảnh - sử dụng FormUrlEncoded
            call = api.createPostWithoutImage(
                animalType,
                fullDescription,
                location,
                null,  // latitude - null sẽ không được gửi
                null   // longitude - null sẽ không được gửi
            );
        }

        call.enqueue(new Callback<ApiResponse<CreatePostResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CreatePostResponse>> call,
                                   Response<ApiResponse<CreatePostResponse>> response) {
                hideLoading();
                btnSubmitPost.setEnabled(true);
                btnSubmitPost.setText("Đăng bài cứu hộ");
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TrangDangBaiActivity.this, "Đăng bài thành công!", Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("refresh", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    String errorMsg = "Đăng bài thất bại";
                    if (response.body() != null) {
                        if (response.body().getMessage() != null) {
                            errorMsg = response.body().getMessage();
                        }
                        // Check error object
                        if (response.code() == 401) {
                            errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                        } else if (response.code() == 400) {
                            errorMsg = "Thông tin không hợp lệ. Vui lòng kiểm tra lại.";
                        }
                    }
                    showError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CreatePostResponse>> call, Throwable t) {
                hideLoading();
                btnSubmitPost.setEnabled(true);
                btnSubmitPost.setText("Đăng bài cứu hộ");
                
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
        if (btnSubmitPost != null) btnSubmitPost.setEnabled(false);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(android.view.View.GONE);
    }

    private void showError(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(android.view.View.VISIBLE);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
        File tempFile = new File(getCacheDir(), "temp_image_" + System.currentTimeMillis() + ".jpg");
        FileOutputStream fos = new FileOutputStream(tempFile);
        fos.write(bytes);
        fos.close();
        
        // Tạo RequestBody từ file
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), tempFile);
        
        // Tạo MultipartBody.Part
        return MultipartBody.Part.createFormData("image", tempFile.getName(), requestFile);
    }
}

