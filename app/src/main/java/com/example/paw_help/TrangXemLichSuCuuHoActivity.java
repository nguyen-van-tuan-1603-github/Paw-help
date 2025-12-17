package com.example.paw_help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;
import com.example.paw_help.models.PostItem;
import com.example.paw_help.models.PostListResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrangXemLichSuCuuHoActivity extends AppCompatActivity {

    private ImageView btnBack;
    private RecyclerView recyclerViewHistory;
    private TextView tvEmptyHistory;
    private RescueHistoryAdapter historyAdapter;
    private List<RescueHistory> historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trang_xem_lich_su_cuu_ho);

        initViews();
        setupRecyclerView();
        loadHistoryData();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerViewHistory = findViewById(R.id.recyclerViewHistory);
        tvEmptyHistory = findViewById(R.id.tvEmptyHistory);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupRecyclerView() {
        recyclerViewHistory.setLayoutManager(new LinearLayoutManager(this));
        historyList = new ArrayList<>();
        historyAdapter = new RescueHistoryAdapter(this, historyList);
        historyAdapter.setOnHistoryClickListener(history -> {
            // Navigate to detail screen
            android.widget.Toast.makeText(this, "Xem chi tiết: " + history.getTitle(),
                android.widget.Toast.LENGTH_SHORT).show();
        });
        recyclerViewHistory.setAdapter(historyAdapter);
    }

    private void loadHistoryData() {
        historyList.clear();

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<PostListResponse>> call = api.getMyPosts();
        call.enqueue(new Callback<ApiResponse<PostListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostListResponse>> call,
                                   Response<ApiResponse<PostListResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    PostListResponse data = response.body().getData();
                    if (data != null && data.getItems() != null) {
                        for (PostItem item : data.getItems()) {
                            historyList.add(new RescueHistory(
                                    String.valueOf(item.getPostId()),
                                    item.getDescription(),
                                    item.getLocation(),
                                    item.getCreatedAt(),
                                    android.R.drawable.ic_menu_camera,
                                    "rescued".equals(item.getStatus())
                            ));
                        }
                    }
                }

                if (historyList.isEmpty()) {
                    tvEmptyHistory.setVisibility(View.VISIBLE);
                    recyclerViewHistory.setVisibility(View.GONE);
                } else {
                    tvEmptyHistory.setVisibility(View.GONE);
                    recyclerViewHistory.setVisibility(View.VISIBLE);
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostListResponse>> call, Throwable t) {
                tvEmptyHistory.setVisibility(View.VISIBLE);
                recyclerViewHistory.setVisibility(View.GONE);
            }
        });
    }
}

