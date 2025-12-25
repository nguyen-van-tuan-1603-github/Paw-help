package com.example.paw_help;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String time;
    private boolean isRead;
    private String type; // Notification type (info, success, warning, error, post, etc.)
    private int iconResId;

    public Notification(String id, String title, String message, String time, boolean isRead, int iconResId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.iconResId = iconResId;
        this.type = "info"; // Default type
    }

    public Notification(String id, String title, String message, String time, boolean isRead, String type, int iconResId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.type = type;
        this.iconResId = iconResId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public String getType() { return type; }
    public int getIconResId() { return iconResId; }

    // Setters
    public void setRead(boolean read) { isRead = read; }
    public void setType(String type) { this.type = type; }
}

