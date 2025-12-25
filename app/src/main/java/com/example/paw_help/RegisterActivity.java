package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.AuthResponse;
import com.example.paw_help.models.RegisterRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText edtFullName, edtEmail, edtPhone, edtPassword;
    private com.google.android.material.checkbox.MaterialCheckBox cbAgreeTerms;
    private com.google.android.material.button.MaterialButton btnRegister;
    private TextView tvLogin, tvErrorMessage;
    private ProgressBar progressBar;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhone = findViewById(R.id.edtPhone);
        edtPassword = findViewById(R.id.edtPassword);
        cbAgreeTerms = findViewById(R.id.cbAgreeTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        progressBar = findViewById(R.id.progressBar);
        
        // Khởi tạo RetrofitClient
        retrofitClient = RetrofitClient.getInstance(this);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> performRegister());

        tvLogin.setOnClickListener(v -> {
            finish(); // Go back to login
        });
    }

    private void performRegister() {
        String fullName = edtFullName.getText() != null ? edtFullName.getText().toString().trim() : "";
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String phone = edtPhone.getText() != null ? edtPhone.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";
        String confirmPassword = password; // Không có field confirm password trong layout

        // Hide previous errors
        hideError();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            showError("Vui lòng nhập họ tên");
            edtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            showError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            showError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        if (phone.length() < 10) {
            showError("Số điện thoại không hợp lệ (tối thiểu 10 số)");
            edtPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (!cbAgreeTerms.isChecked()) {
            showError("Vui lòng đồng ý với điều khoản sử dụng");
            return;
        }

        // Show loading và disable button
        showLoading();
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");
        
        // Gọi API register
        RegisterRequest registerRequest = new RegisterRequest(fullName, email, phone, password, confirmPassword);
        Call<ApiResponse<AuthResponse>> call = retrofitClient.getApi().register(registerRequest);
        
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                hideLoading();
                btnRegister.setEnabled(true);
                btnRegister.setText("Tạo Tài Khoản");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        // Đăng ký thành công - chuyển sang màn hình đăng nhập
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
                        
                        // Chuyển sang LoginActivity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Hiển thị lỗi từ server
                        String errorMsg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đăng ký thất bại";
                        if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                            errorMsg += "\n" + String.join("\n", apiResponse.getErrors());
                        }
                        showError(errorMsg);
                    }
                } else {
                    String errorMsg = "Đăng ký thất bại";
                    if (response.code() == 400) {
                        errorMsg = "Thông tin không hợp lệ. Vui lòng kiểm tra lại.";
                    } else if (response.code() == 409) {
                        errorMsg = "Email đã được sử dụng. Vui lòng chọn email khác.";
                    }
                    showError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                hideLoading();
                btnRegister.setEnabled(true);
                btnRegister.setText("Tạo Tài Khoản");
                
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc dữ liệu di động.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
                }
                showError(errorMsg);
            }
        });
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (btnRegister != null) btnRegister.setEnabled(false);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        if (tvErrorMessage != null) {
            tvErrorMessage.setText(message);
            tvErrorMessage.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }

    private void hideError() {
        if (tvErrorMessage != null) {
            tvErrorMessage.setVisibility(View.GONE);
        }
    }
}

