package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.AuthResponse;
import com.example.paw_help.models.LoginRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private com.google.android.material.textfield.TextInputEditText edtEmail, edtPassword;
    private com.google.android.material.button.MaterialButton btnLogin;
    private TextView tvRegister, tvForgotPassword, tvErrorMessage;
    private ProgressBar progressBar;
    private RetrofitClient retrofitClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        progressBar = findViewById(R.id.progressBar);
        
        // Khởi tạo RetrofitClient
        retrofitClient = RetrofitClient.getInstance(this);
        
        // Kiểm tra đã login chưa
        if (retrofitClient.isLoggedIn()) {
            goToMainActivity();
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> performLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void performLogin() {
        String email = edtEmail.getText() != null ? edtEmail.getText().toString().trim() : "";
        String password = edtPassword.getText() != null ? edtPassword.getText().toString().trim() : "";

        // Hide previous errors
        hideError();

        // Validation
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

        // Show loading và disable button
        showLoading();
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");
        
        // Gọi API login
        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<ApiResponse<AuthResponse>> call = retrofitClient.getApi().login(loginRequest);
        
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                hideLoading();
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng Nhập");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        AuthResponse authResponse = apiResponse.getData();
                        
                        // Lưu token và user info
                        retrofitClient.saveToken(authResponse.getToken());
                        retrofitClient.saveUser(authResponse.getUser());
                        
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Chuyển sang MainActivity
                        goToMainActivity();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null ? apiResponse.getMessage() : "Đăng nhập thất bại";
                        showError(errorMsg);
                    }
                } else {
                    String errorMsg = "Đăng nhập thất bại";
                    if (response.code() == 401) {
                        errorMsg = "Email hoặc mật khẩu không đúng";
                    } else if (response.code() == 400) {
                        errorMsg = "Thông tin không hợp lệ";
                    }
                    showError(errorMsg);
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                hideLoading();
                btnLogin.setEnabled(true);
                btnLogin.setText("Đăng Nhập");
                
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
        if (btnLogin != null) btnLogin.setEnabled(false);
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
    
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

