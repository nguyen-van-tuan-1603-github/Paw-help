package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.paw_help.api.PawHelpApi;
import com.example.paw_help.api.RetrofitClient;
import com.example.paw_help.models.ApiResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsActivity extends AppCompatActivity implements NotificationAdapter.OnNotificationClickListener {

    private ImageView btnBack;
    private CardView btnMarkAllRead;
    private RecyclerView recyclerViewNotifications;
    private LinearLayout layoutEmptyState;
    private FrameLayout dialogOverlay;
    private NotificationAdapter adapter;
    private List<Notification> notifications;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        initViews();
        setupRecyclerView();
        loadNotifications();
        setupListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnMarkAllRead = findViewById(R.id.btnMarkAllRead);
        recyclerViewNotifications = findViewById(R.id.recyclerViewNotifications);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        dialogOverlay = findViewById(R.id.dialogOverlay);
    }

    private void setupRecyclerView() {
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(this, notifications, this);
        recyclerViewNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        notifications.clear();

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        // Dùng ApiResponse<Object> để tránh tạo nhiều model
        Call<ApiResponse<Object>> call = api.getNotifications(1, 20);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call,
                                   Response<ApiResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Object data = response.body().getData();
                    if (data instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) data;
                        Object itemsObj = map.get("items");
                        if (itemsObj instanceof List) {
                            List<?> rawList = (List<?>) itemsObj;
                            for (Object o : rawList) {
                                if (o instanceof Map) {
                                    Map<?, ?> n = (Map<?, ?>) o;
                                    String id = String.valueOf(n.get("notificationId"));
                                    String title = String.valueOf(n.get("title"));
                                    String message = String.valueOf(n.get("message"));
                                    String createdAt = String.valueOf(n.get("createdAt"));
                                    boolean isRead = Boolean.TRUE.equals(n.get("isRead"));

                                    notifications.add(new Notification(
                                            id,
                                            title,
                                            message,
                                            createdAt,
                                            isRead,
                                            R.drawable.emergency
                                    ));
                                }
                            }
                        }
                    }
                }

                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                updateEmptyState();
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> showMarkAllReadDialog());

        // Dialog buttons
        findViewById(R.id.btnDialogCancel).setOnClickListener(v -> hideMarkAllReadDialog());
        findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            markAllAsReadOnServer();
        });
    }

    private void markAllAsReadOnServer() {
        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        api.markAllNotificationsAsRead().enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call,
                                   Response<ApiResponse<Object>> response) {
                adapter.markAllAsRead();
                hideMarkAllReadDialog();
                Toast.makeText(NotificationsActivity.this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                hideMarkAllReadDialog();
            }
        });
    }

    private void showMarkAllReadDialog() {
        dialogOverlay.setVisibility(View.VISIBLE);
    }

    private void hideMarkAllReadDialog() {
        dialogOverlay.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        if (notifications.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Navigate to detail based on notification type
        Toast.makeText(this, "Xem chi tiết: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Navigate to appropriate screen
    }
}

