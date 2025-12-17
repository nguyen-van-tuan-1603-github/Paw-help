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
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.IOException;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.PostItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangDangBaiActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;

    private ImageView btnBack, imgPreview, btnUploadImage;
    private EditText edtTitle, edtLocation, edtDescription;
    private Button btnSubmitPost;
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

        // Validation
        if (TextUtils.isEmpty(title)) {
            edtTitle.setError("Vui lòng nhập tiêu đề");
            edtTitle.requestFocus();
            return;
        }

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

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Toast.makeText(this, "Đang gửi bài đăng...", Toast.LENGTH_SHORT).show();

        Call<ApiResponse<PostItem>> call = api.createPost(
                "Chó",          // tạm thời fix cứng, nếu cần có thể thêm chọn loại động vật
                description,
                location,
                null,
                null
        );

        call.enqueue(new Callback<ApiResponse<PostItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostItem>> call,
                                   Response<ApiResponse<PostItem>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(TrangDangBaiActivity.this, "Đăng bài thành công!", Toast.LENGTH_LONG).show();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("refresh", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(TrangDangBaiActivity.this, "Đăng bài thất bại", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostItem>> call, Throwable t) {
                Toast.makeText(TrangDangBaiActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

