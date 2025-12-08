package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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

    private EditText edtFullName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    private CheckBox cbAgreeTerms;
    private Button btnRegister;
    private TextView tvLogin;
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
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        cbAgreeTerms = findViewById(R.id.cbAgreeTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        
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
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Vui lòng nhập họ tên");
            edtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
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

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu không khớp");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!cbAgreeTerms.isChecked()) {
            Toast.makeText(this, "Vui lòng đồng ý với điều khoản sử dụng",
                Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable button và show loading
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");
        
        // Gọi API register
        RegisterRequest registerRequest = new RegisterRequest(fullName, email, phone, password, confirmPassword);
        Call<ApiResponse<AuthResponse>> call = retrofitClient.getApi().register(registerRequest);
        
        call.enqueue(new Callback<ApiResponse<AuthResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<AuthResponse>> call, Response<ApiResponse<AuthResponse>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng Ký");
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<AuthResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        AuthResponse authResponse = apiResponse.getData();
                        
                        // Lưu token và user info
                        retrofitClient.saveToken(authResponse.getToken());
                        retrofitClient.saveUser(authResponse.getUser());
                        
                        Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Chuyển sang MainActivity
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Hiển thị lỗi từ server
                        String errorMsg = apiResponse.getMessage();
                        if (apiResponse.getErrors() != null && !apiResponse.getErrors().isEmpty()) {
                            errorMsg += "\n" + String.join("\n", apiResponse.getErrors());
                        }
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại. Vui lòng thử lại!", Toast.LENGTH_LONG).show();
                }
            }
            
            @Override
            public void onFailure(Call<ApiResponse<AuthResponse>> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Đăng Ký");
                Toast.makeText(RegisterActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

