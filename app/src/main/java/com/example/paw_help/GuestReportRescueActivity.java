package com.example.paw_help;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GuestReportRescueActivity extends AppCompatActivity {

    private EditText edtFullName, edtPhone, edtEmail, edtAddress;
    private EditText edtDescription, edtDateTime;
    private Spinner spinnerAnimalType, spinnerCondition;
    private Button btnSubmitReport;
    private ImageView btnBack;
    private Calendar selectedDateTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_report_rescue);

        selectedDateTime = Calendar.getInstance();

        initViews();
        setupSpinners();
        setupListeners();
        startAnimations();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        edtFullName = findViewById(R.id.edtFullName);
        edtPhone = findViewById(R.id.edtPhone);
        edtEmail = findViewById(R.id.edtEmail);
        edtAddress = findViewById(R.id.edtAddress);
        edtDescription = findViewById(R.id.edtDescription);
        edtDateTime = findViewById(R.id.edtDateTime);
        spinnerAnimalType = findViewById(R.id.spinnerAnimalType);
        spinnerCondition = findViewById(R.id.spinnerCondition);
        btnSubmitReport = findViewById(R.id.btnSubmitReport);

        // Set current date time as default
        updateDateTimeDisplay();
    }

    private void setupSpinners() {
        // Animal Type Spinner
        String[] animalTypes = {
            "Chọn loại động vật",
            "🐕 Chó",
            "🐱 Mèo",
            "🐦 Chim",
            "🐰 Thỏ",
            "🐹 Chuột hamster",
            "🦎 Bò sát",
            "Khác"
        };
        ArrayAdapter<String> animalAdapter = new ArrayAdapter<>(
            this, 
            android.R.layout.simple_spinner_item, 
            animalTypes
        );
        animalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAnimalType.setAdapter(animalAdapter);

        // Condition Spinner
        String[] conditions = {
            "Chọn tình trạng",
            "❗ Khẩn cấp - Bị thương nặng",
            "⚠️ Nghiêm trọng - Cần hỗ trợ ngay",
            "⚡ Trung bình - Bị thương nhẹ",
            "📍 Ổn định - Bị bỏ rơi/lạc",
            "🆘 Nguy hiểm - Ở nơi nguy hiểm",
            "💧 Đói/Khát",
            "🤒 Ốm/Bệnh"
        };
        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            conditions
        );
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCondition.setAdapter(conditionAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        // Date Time Picker
        edtDateTime.setOnClickListener(v -> showDateTimePicker());

        // Submit Button
        btnSubmitReport.setOnClickListener(v -> submitReport());
    }

    private void showDateTimePicker() {
        Calendar currentDate = Calendar.getInstance();
        
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, month);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                
                // Show time picker after date is selected
                TimePickerDialog timePickerDialog = new TimePickerDialog(
                    this,
                    (timeView, hourOfDay, minute) -> {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        updateDateTimeDisplay();
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
                );
                timePickerDialog.show();
            },
            currentDate.get(Calendar.YEAR),
            currentDate.get(Calendar.MONTH),
            currentDate.get(Calendar.DAY_OF_MONTH)
        );
        
        datePickerDialog.show();
    }

    private void updateDateTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        edtDateTime.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void submitReport() {
        // Get values
        String fullName = edtFullName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();
        int animalTypePosition = spinnerAnimalType.getSelectedItemPosition();
        int conditionPosition = spinnerCondition.getSelectedItemPosition();

        // Validation
        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Vui lòng nhập họ tên");
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

        if (!TextUtils.isEmpty(email) && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            edtAddress.setError("Vui lòng nhập địa chỉ");
            edtAddress.requestFocus();
            return;
        }

        if (animalTypePosition == 0) {
            Toast.makeText(this, "Vui lòng chọn loại động vật", Toast.LENGTH_SHORT).show();
            return;
        }

        if (conditionPosition == 0) {
            Toast.makeText(this, "Vui lòng chọn tình trạng động vật", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            edtDescription.setError("Vui lòng mô tả tình trạng");
            edtDescription.requestFocus();
            return;
        }

        // Show confirmation dialog
        showConfirmationDialog(fullName, phone, address);
    }

    private void showConfirmationDialog(String name, String phone, String address) {
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận gửi yêu cầu")
            .setMessage("Thông tin của bạn:\n\n" +
                    "Họ tên: " + name + "\n" +
                    "Số điện thoại: " + phone + "\n" +
                    "Địa chỉ: " + address + "\n\n" +
                    "Chúng tôi sẽ liên hệ với bạn sớm nhất có thể.\n\n" +
                    "Bạn có chắc muốn gửi yêu cầu này?")
            .setPositiveButton("Gửi", (dialog, which) -> {
                // TODO: Save to database or send to server
                showSuccessDialog();
            })
            .setNegativeButton("Kiểm tra lại", (dialog, which) -> dialog.dismiss())
            .show();
    }

    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Gửi thành công! ✅")
            .setMessage("Cảm ơn bạn đã báo cáo!\n\n" +
                    "Yêu cầu cứu hộ của bạn đã được ghi nhận. " +
                    "Đội ngũ của chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.\n\n" +
                    "Mã yêu cầu: #" + generateRequestId() + "\n\n" +
                    "Nếu trường hợp khẩn cấp, vui lòng gọi: 0905-XXX-XXX")
            .setPositiveButton("Hoàn tất", (dialog, which) -> {
                // Return to previous screen
                finish();
            })
            .setNegativeButton("Xem thêm trợ giúp", (dialog, which) -> {
                // Navigate to help/about page
                Intent intent = new Intent(GuestReportRescueActivity.this, TrangVeChungToiActivity.class);
                startActivity(intent);
                finish();
            })
            .setCancelable(false)
            .show();
    }

    private String generateRequestId() {
        // Generate a simple request ID (in production, this should come from server)
        return "RQ" + System.currentTimeMillis() % 1000000;
    }

    private void startAnimations() {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);

        // Apply animations
        View[] views = {
            findViewById(R.id.btnSubmitReport)
        };

        for (View view : views) {
            if (view != null) {
                view.startAnimation(fadeIn);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Show confirmation dialog
        new AlertDialog.Builder(this)
            .setTitle("Hủy yêu cầu?")
            .setMessage("Bạn có chắc muốn hủy yêu cầu cứu hộ này? Thông tin đã nhập sẽ không được lưu.")
            .setPositiveButton("Hủy yêu cầu", (dialog, which) -> {
                super.onBackPressed();
            })
            .setNegativeButton("Tiếp tục", (dialog, which) -> dialog.dismiss())
            .show();
    }
}

