package com.example.paw_help;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class TrangCuuHoActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST = 100;
    private static final String EMERGENCY_PHONE = "113"; // Emergency number

    private ImageView btnBack;
    private Button btnCallEmergency, btnGetLocation, btnReportIncident;
    private TextView tvEmergencyInfo, tvLocationInfo;
    private androidx.cardview.widget.CardView cardLocationInfo;

    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_cuu_ho);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        initViews();
        setupListeners();
        checkLocationPermission();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnCallEmergency = findViewById(R.id.btnCallEmergency);
        btnGetLocation = findViewById(R.id.btnGetLocation);
        btnReportIncident = findViewById(R.id.btnReportIncident);
        tvEmergencyInfo = findViewById(R.id.tvEmergencyInfo);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
        cardLocationInfo = findViewById(R.id.cardLocationInfo);
    }

    private void setupListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        if (btnCallEmergency != null) {
            btnCallEmergency.setOnClickListener(v -> callEmergencyNumber());
        }

        if (btnGetLocation != null) {
            btnGetLocation.setOnClickListener(v -> getCurrentLocation());
        }

        if (btnReportIncident != null) {
            btnReportIncident.setOnClickListener(v -> {
                Intent intent = new Intent(TrangCuuHoActivity.this, TrangDangBaiActivity.class);
                startActivity(intent);
            });
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST);
        } else {
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Cần cấp quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
            return;
        }

        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    currentLocation = location;
                    updateLocationInfo(location);
                    Toast.makeText(this, "Đã lấy vị trí thành công", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Không thể lấy vị trí hiện tại",
                        Toast.LENGTH_SHORT).show();
                }
            })
            .addOnFailureListener(e -> {
                Toast.makeText(this, "Lỗi khi lấy vị trí: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            });
    }

    private void updateLocationInfo(Location location) {
        if (tvLocationInfo != null && cardLocationInfo != null) {
            String locationText = "Vĩ độ: " + String.format("%.6f", location.getLatitude()) + 
                "\nKinh độ: " + String.format("%.6f", location.getLongitude());
            tvLocationInfo.setText(locationText);
            cardLocationInfo.setVisibility(android.view.View.VISIBLE);
        }
    }

    private void callEmergencyNumber() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + EMERGENCY_PHONE));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                          int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, "Quyền truy cập vị trí bị từ chối",
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}

