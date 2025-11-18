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
        // TODO: Load history from database/Firebase
        // For now, add sample data
        historyList.clear();

        // Sample data - using temporary placeholder images
        // TODO: Replace with actual image loading from storage
        historyList.add(new RescueHistory(
            "1",
            "Một chó con bị thương ở chân",
            "Quận Hải Châu, Đà Nẵng",
            "15/11/2025 - 14:30",
            android.R.drawable.ic_menu_camera,
            true
        ));

        historyList.add(new RescueHistory(
            "2",
            "Mèo con bị kẹt trên cây cao",
            "Quận Thanh Khê, Đà Nẵng",
            "14/11/2025 - 10:15",
            android.R.drawable.ic_menu_camera,
            true
        ));

        historyList.add(new RescueHistory(
            "3",
            "Chó lạc tìm chủ",
            "Quận Sơn Trà, Đà Nẵng",
            "13/11/2025 - 16:00",
            android.R.drawable.ic_menu_gallery,
            false
        ));

        if (historyList.isEmpty()) {
            tvEmptyHistory.setVisibility(View.VISIBLE);
            recyclerViewHistory.setVisibility(View.GONE);
        } else {
            tvEmptyHistory.setVisibility(View.GONE);
            recyclerViewHistory.setVisibility(View.VISIBLE);
            historyAdapter.notifyDataSetChanged();
        }
    }
}

