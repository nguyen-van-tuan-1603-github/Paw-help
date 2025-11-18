package com.example.paw_help;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        // TODO: Implement actual password reset with Firebase/Backend
        // Send password reset email
        Toast.makeText(this, "Đang gửi link reset mật khẩu...", Toast.LENGTH_SHORT).show();

        // Simulate sending email
        new android.os.Handler().postDelayed(() -> {
            Toast.makeText(this,
                "Đã gửi link reset mật khẩu đến " + email + "\nVui lòng kiểm tra email!",
                Toast.LENGTH_LONG).show();
            finish();
        }, 1500);
    }
}

