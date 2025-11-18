package com.example.paw_help;

public class RescueHistory {
    private String id;
    private String title;
    private String location;
    private String date;
    private int imageResId;
    private boolean isCompleted;

    public RescueHistory(String id, String title, String location, String date, int imageResId, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.location = location;
        this.date = date;
        this.imageResId = imageResId;
        this.isCompleted = isCompleted;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLocation() { return location; }
    public String getDate() { return date; }
    public int getImageResId() { return imageResId; }
    public boolean isCompleted() { return isCompleted; }

    // Setters
    public void setCompleted(boolean completed) { isCompleted = completed; }
}

