package com.example.paw_help;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
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
    private LinearLayout layoutEmptyState, layoutErrorState;
    private TextView tvErrorMessage;
    private ProgressBar progressBar;
    private MaterialButton btnRetry;
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
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        layoutErrorState = findViewById(R.id.layoutErrorState);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        progressBar = findViewById(R.id.progressBar);
        btnRetry = findViewById(R.id.btnRetry);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> loadHistoryData());
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
        showLoading();

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        Call<ApiResponse<PostListResponse>> call = api.getMyPosts();
        call.enqueue(new Callback<ApiResponse<PostListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostListResponse>> call,
                                   Response<ApiResponse<PostListResponse>> response) {
                hideLoading();
                
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
                } else {
                    // API error
                    String errorMsg = "Không thể tải lịch sử";
                    if (response.code() == 401) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                    }
                    showErrorState(errorMsg);
                    return;
                }

                if (historyList.isEmpty()) {
                    showEmptyState();
                } else {
                    showContentState();
                    historyAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostListResponse>> call, Throwable t) {
                hideLoading();
                
                String errorMsg = "Không thể kết nối đến server";
                if (t instanceof java.net.UnknownHostException) {
                    errorMsg = "Không có kết nối mạng. Vui lòng kiểm tra WiFi hoặc dữ liệu di động.";
                } else if (t instanceof java.net.SocketTimeoutException) {
                    errorMsg = "Kết nối quá thời gian. Vui lòng thử lại.";
                }
                
                showErrorState(errorMsg);
            }
        });
    }

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerViewHistory != null) recyclerViewHistory.setVisibility(View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void showContentState() {
        if (recyclerViewHistory != null) recyclerViewHistory.setVisibility(View.VISIBLE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
    }

    private void showEmptyState() {
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.VISIBLE);
        if (recyclerViewHistory != null) recyclerViewHistory.setVisibility(View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
    }

    private void showErrorState(String message) {
        if (layoutErrorState != null) {
            layoutErrorState.setVisibility(View.VISIBLE);
            if (tvErrorMessage != null) {
                tvErrorMessage.setText(message);
            }
        }
        if (recyclerViewHistory != null) recyclerViewHistory.setVisibility(View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
    }
}

