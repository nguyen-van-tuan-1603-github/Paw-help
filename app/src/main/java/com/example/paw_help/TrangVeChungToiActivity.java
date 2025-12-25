package com.example.paw_help;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.DashboardStats;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangVeChungToiActivity extends AppCompatActivity {

    private LinearLayout btnFacebook, btnWebsite, btnEmail;
    private Button btnViewTeam, btnDonate;
    private CardView cardMission, cardVision, cardValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_ve_chung_toi);

        initViews();
        setupListeners();
        loadStatistics();
    }

    private void initViews() {
        btnFacebook = findViewById(R.id.btnFacebook);
        btnWebsite = findViewById(R.id.btnWebsite);
        btnEmail = findViewById(R.id.btnEmail);
        btnViewTeam = findViewById(R.id.btnViewTeam);
        btnDonate = findViewById(R.id.btnDonate);
    }

    private void setupListeners() {
        // Social media buttons
        btnFacebook.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/pawhelp"));
            startActivity(intent);
        });

        btnWebsite.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.pawhelp.com"));
            startActivity(intent);
        });

        btnEmail.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:contact@pawhelp.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ ứng dụng Paw Help");
            startActivity(Intent.createChooser(intent, "Gửi email"));
        });

        // View team button
        btnViewTeam.setOnClickListener(v -> {
            Intent intent = new Intent(TrangVeChungToiActivity.this, TrangDoiNguActivity.class);
            startActivity(intent);
        });

        // Donate button
        btnDonate.setOnClickListener(v -> {
            // TODO: Implement donation functionality
            Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.pawhelp.com/donate"));
            startActivity(intent);
        });
    }

    private void loadStatistics() {
        TextView tvRescuedCount = findViewById(R.id.tvRescuedCount);
        TextView tvAdoptedCount = findViewById(R.id.tvAdoptedCount);
        TextView tvVolunteersCount = findViewById(R.id.tvVolunteersCount);
        TextView tvYearsCount = findViewById(R.id.tvYearsCount);

        // Load từ API
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<DashboardStats>> call = api.getDashboardStats();
        call.enqueue(new Callback<ApiResponse<DashboardStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<DashboardStats>> call,
                                   Response<ApiResponse<DashboardStats>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    DashboardStats stats = response.body().getData();
                    if (stats != null) {
                        if (tvRescuedCount != null) {
                            tvRescuedCount.setText(formatNumber(stats.getRescuedCount()));
                        }
                        if (tvAdoptedCount != null) {
                            // Sử dụng totalPosts hoặc inProgressPosts làm adopted count
                            tvAdoptedCount.setText(formatNumber(stats.getInProgressPosts()));
                        }
                        if (tvVolunteersCount != null) {
                            // Sử dụng totalUsers làm volunteer count
                            tvVolunteersCount.setText(formatNumber(stats.getTotalUsers()));
                        }
                        if (tvYearsCount != null) {
                            // Sử dụng totalPosts / 100 làm years (ước tính)
                            int years = Math.max(1, stats.getTotalPosts() / 100);
                            tvYearsCount.setText(formatNumber(years));
                        }
                    }
                } else {
                    // Fallback to default values if API fails
                    setDefaultStatistics();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<DashboardStats>> call, Throwable t) {
                // Fallback to default values if API fails
                setDefaultStatistics();
            }
        });
    }
    
    private void setDefaultStatistics() {
        TextView tvRescuedCount = findViewById(R.id.tvRescuedCount);
        TextView tvAdoptedCount = findViewById(R.id.tvAdoptedCount);
        TextView tvVolunteersCount = findViewById(R.id.tvVolunteersCount);
        TextView tvYearsCount = findViewById(R.id.tvYearsCount);

        if (tvRescuedCount != null) tvRescuedCount.setText("1,500+");
        if (tvAdoptedCount != null) tvAdoptedCount.setText("800+");
        if (tvVolunteersCount != null) tvVolunteersCount.setText("50+");
        if (tvYearsCount != null) tvYearsCount.setText("10+");
    }
    
    private String formatNumber(int number) {
        if (number >= 1000) {
            return String.format("%.1fK+", number / 1000.0);
        }
        return String.valueOf(number) + "+";
    }
}

