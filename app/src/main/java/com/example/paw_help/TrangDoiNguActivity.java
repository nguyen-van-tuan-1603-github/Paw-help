package com.example.paw_help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangDoiNguActivity extends AppCompatActivity {

    private RecyclerView recyclerViewTeam;
    private TeamAdapter teamAdapter;
    private List<TeamMember> teamMembers;
    private ImageView btnBack;
    private TextView tvTeamCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_doi_ngu);

        initViews();
        setupRecyclerView();
        loadTeamMembers();
    }

    private void initViews() {
        recyclerViewTeam = findViewById(R.id.recyclerViewTeam);
        btnBack = findViewById(R.id.btnBack);
        tvTeamCount = findViewById(R.id.tvTeamCount);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        recyclerViewTeam.setLayoutManager(new LinearLayoutManager(this));
        teamMembers = new ArrayList<>();
        teamAdapter = new TeamAdapter(this, teamMembers);
        recyclerViewTeam.setAdapter(teamAdapter);
    }

    private void loadTeamMembers() {
        // Gọi API để load team members
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<List<TeamMember>>> call = api.getTeamMembers();
        call.enqueue(new Callback<ApiResponse<List<TeamMember>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<TeamMember>>> call,
                                   Response<ApiResponse<List<TeamMember>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TeamMember> apiMembers = response.body().getData();
                    if (apiMembers != null && !apiMembers.isEmpty()) {
                        teamMembers.clear();
                        teamMembers.addAll(apiMembers);
                        teamAdapter.notifyDataSetChanged();

                        // Update team count
                        if (tvTeamCount != null) {
                            tvTeamCount.setText(teamMembers.size() + " thành viên");
                        }
                    } else {
                        // Fallback to empty state or show message
                        teamMembers.clear();
                        teamAdapter.notifyDataSetChanged();
                        if (tvTeamCount != null) {
                            tvTeamCount.setText("0 thành viên");
                        }
                    }
                } else {
                    Toast.makeText(TrangDoiNguActivity.this, "Không thể tải danh sách đội ngũ", Toast.LENGTH_SHORT).show();
                    loadFallbackData();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<TeamMember>>> call, Throwable t) {
                Toast.makeText(TrangDoiNguActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadFallbackData();
            }
        });
    }

    private void loadFallbackData() {
        // Fallback data if API fails
        teamMembers.clear();
        teamAdapter.notifyDataSetChanged();
        if (tvTeamCount != null) {
            tvTeamCount.setText("0 thành viên");
        }
    }
}

