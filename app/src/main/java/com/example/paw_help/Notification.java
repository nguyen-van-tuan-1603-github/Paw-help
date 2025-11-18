package com.example.paw_help;

public class Notification {
    private String id;
    private String title;
    private String message;
    private String time;
    private boolean isRead;
    private int iconResId;

    public Notification(String id, String title, String message, String time, boolean isRead, int iconResId) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.time = time;
        this.isRead = isRead;
        this.iconResId = iconResId;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getTime() { return time; }
    public boolean isRead() { return isRead; }
    public int getIconResId() { return iconResId; }

    // Setters
    public void setRead(boolean read) { isRead = read; }
}

