package com.example.paw_help;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class EditProfileActivity extends AppCompatActivity {

    private ImageView btnBack, btnEditAvatar, imgUserAvatar;
    private EditText edtFullName, edtPhone;
    private TextView tvEmail;
    private MaterialButton btnMale, btnFemale;
    private Button btnSaveChanges;

    private String selectedGender = "Nam";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
        loadUserData();
        setupListeners();
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
            // TODO: Open image picker
            Toast.makeText(this, "Chọn ảnh đại diện", Toast.LENGTH_SHORT).show();
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
            btnMale.setBackgroundColor(getColor(R.color.button_primary));
            btnMale.setTextColor(getColor(R.color.white));
            btnFemale.setBackgroundColor(0xFFF5F5F5);
            btnFemale.setTextColor(getColor(R.color.text_secondary));
        } else {
            btnFemale.setBackgroundColor(getColor(R.color.button_primary));
            btnFemale.setTextColor(getColor(R.color.white));
            btnMale.setBackgroundColor(0xFFF5F5F5);
            btnMale.setTextColor(getColor(R.color.text_secondary));
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

        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("PawHelpPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_name", fullName);
        editor.putString("user_phone", phone);
        editor.putString("user_gender", selectedGender);
        editor.apply();

        Toast.makeText(this, "Đã lưu thông tin thành công!", Toast.LENGTH_SHORT).show();

        // Return to profile screen
        finish();
    }
}

