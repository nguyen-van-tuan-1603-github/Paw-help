package com.example.paw_help;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
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
    private LinearLayout layoutEmptyState, layoutErrorState;
    private FrameLayout dialogOverlay;
    private ProgressBar progressBar;
    private TextView tvErrorMessage;
    private MaterialButton btnRetry;
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
        layoutErrorState = findViewById(R.id.layoutErrorState);
        dialogOverlay = findViewById(R.id.dialogOverlay);
        progressBar = findViewById(R.id.progressBar);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnRetry = findViewById(R.id.btnRetry);
    }

    private void setupRecyclerView() {
        recyclerViewNotifications.setLayoutManager(new LinearLayoutManager(this));
        notifications = new ArrayList<>();
        adapter = new NotificationAdapter(this, notifications, this);
        recyclerViewNotifications.setAdapter(adapter);
    }

    private void loadNotifications() {
        notifications.clear();
        showLoading();

        RetrofitClient client = RetrofitClient.getInstance(this);
        PawHelpApi api = client.getApi();

        // Dùng ApiResponse<Object> để tránh tạo nhiều model
        Call<ApiResponse<Object>> call = api.getNotifications(1, 20);
        call.enqueue(new Callback<ApiResponse<Object>>() {
            @Override
            public void onResponse(Call<ApiResponse<Object>> call,
                                   Response<ApiResponse<Object>> response) {
                hideLoading();
                
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
                                    String type = n.get("type") != null ? String.valueOf(n.get("type")) : "info";

                                    notifications.add(new Notification(
                                            id,
                                            title,
                                            message,
                                            createdAt,
                                            isRead,
                                            type,
                                            R.drawable.emergency
                                    ));
                                }
                            }
                        }
                    }
                } else {
                    // API error
                    String errorMsg = "Không thể tải thông báo";
                    if (response.code() == 401) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.";
                    }
                    showErrorState(errorMsg);
                    return;
                }

                adapter.notifyDataSetChanged();
                updateEmptyState();
            }

            @Override
            public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
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

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> showMarkAllReadDialog());

        // Dialog buttons
        findViewById(R.id.btnDialogCancel).setOnClickListener(v -> hideMarkAllReadDialog());
        findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            markAllAsReadOnServer();
        });

        // Retry button
        if (btnRetry != null) {
            btnRetry.setOnClickListener(v -> loadNotifications());
        }
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

    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (recyclerViewNotifications != null) recyclerViewNotifications.setVisibility(View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
        if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
    }

    private void hideLoading() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        if (notifications.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewNotifications.setVisibility(View.GONE);
            if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewNotifications.setVisibility(View.VISIBLE);
            if (layoutErrorState != null) layoutErrorState.setVisibility(View.GONE);
        }
    }

    private void showErrorState(String message) {
        if (layoutErrorState != null) {
            layoutErrorState.setVisibility(View.VISIBLE);
            if (tvErrorMessage != null) {
                tvErrorMessage.setText(message);
            }
        }
        if (recyclerViewNotifications != null) recyclerViewNotifications.setVisibility(View.GONE);
        if (layoutEmptyState != null) layoutEmptyState.setVisibility(View.GONE);
    }

    @Override
    public void onNotificationClick(Notification notification) {
        // Mark as read on server if not already read
        if (!notification.isRead()) {
            markNotificationAsRead(notification);
        }

        // Navigate to detail based on notification type
        String type = notification.getType();
        if (type != null && type.contains("post")) {
            // Nếu notification về post, có thể navigate đến PostDetailActivity
            // Tuy nhiên, cần extract postId từ message hoặc có thêm field trong notification
            // Hiện tại chỉ show toast
            Toast.makeText(this, notification.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            // For other notification types, just show details
            Toast.makeText(this, notification.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void markNotificationAsRead(Notification notification) {
        try {
            int notificationId = Integer.parseInt(notification.getId());
            RetrofitClient client = RetrofitClient.getInstance(this);
            PawHelpApi api = client.getApi();

            api.markNotificationAsRead(notificationId).enqueue(new Callback<ApiResponse<Object>>() {
                @Override
                public void onResponse(Call<ApiResponse<Object>> call,
                                       Response<ApiResponse<Object>> response) {
                    // Notification đã được đánh dấu là đã đọc trên server
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<ApiResponse<Object>> call, Throwable t) {
                    // Mark locally anyway
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (NumberFormatException e) {
            // Invalid notification ID - mark locally
            notification.setRead(true);
            adapter.notifyDataSetChanged();
        }
    }
}

