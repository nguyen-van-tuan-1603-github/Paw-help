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
        // Sample notifications
        notifications.clear();

        notifications.add(new Notification(
                "1",
                "Mèo tố tình trạng",
                "Phát hiện một chú mèo con bị kẹt trên cây cao 5m, cần hỗ trợ gấp",
                "5 phút trước",
                false,
                R.drawable.emergency
        ));

        notifications.add(new Notification(
                "2",
                "Cứu hộ thành công",
                "Chú chó bị thương ở chân đã được đưa đến bệnh viện thú y an toàn",
                "1 giờ trước",
                true,
                R.drawable.pets
        ));

        notifications.add(new Notification(
                "3",
                "Cần tình nguyện viên",
                "Đội cứu hộ A đang cần thêm 2 tình nguyện viên hỗ trợ tại Quận Hải Châu",
                "3 giờ trước",
                false,
                R.drawable.volunteer
        ));

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnMarkAllRead.setOnClickListener(v -> showMarkAllReadDialog());

        // Dialog buttons
        findViewById(R.id.btnDialogCancel).setOnClickListener(v -> hideMarkAllReadDialog());
        findViewById(R.id.btnDialogConfirm).setOnClickListener(v -> {
            adapter.markAllAsRead();
            hideMarkAllReadDialog();
            Toast.makeText(this, "Đã đánh dấu tất cả là đã đọc", Toast.LENGTH_SHORT).show();
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

