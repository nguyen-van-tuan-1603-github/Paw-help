package com.example.paw_help;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity {

    private ImageView btnBack;
    private EditText edtEmail;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        initViews();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtEmail = findViewById(R.id.edtEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();

        // Validation
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

        btnResetPassword.setEnabled(false);
        Toast.makeText(this, "Đang gửi link reset mật khẩu...", Toast.LENGTH_SHORT).show();

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<Object>> call = api.forgotPassword(email);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call,
                                   Response<ApiResponse<Object>> response) {
                btnResetPassword.setEnabled(true);
                
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Show message from backend
                    String message = response.body().getMessage();
                    if (message == null || message.isEmpty()) {
                        message = "Nếu email tồn tại, chúng tôi đã gửi link đặt lại mật khẩu đến email của bạn. Vui lòng kiểm tra hộp thư.";
                    }
                    Toast.makeText(ForgotPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    String errorMsg = "Gửi email thất bại";
                    if (response.body() != null) {
                        if (response.body().getMessage() != null && !response.body().getMessage().isEmpty()) {
                            errorMsg = response.body().getMessage();
                        } else if (response.body().getErrors() != null && !response.body().getErrors().isEmpty()) {
                            errorMsg = response.body().getErrors().get(0);
                        }
                    }
                    Toast.makeText(ForgotPasswordActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                btnResetPassword.setEnabled(true);
                Toast.makeText(ForgotPasswordActivity.this,
                    "Lỗi: " + t.getMessage(),
                    Toast.LENGTH_LONG).show();
            }
        });
    }
}

