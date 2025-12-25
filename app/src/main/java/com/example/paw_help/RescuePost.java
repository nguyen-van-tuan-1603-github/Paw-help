package com.example.paw_help;

public class RescuePost {
    private String id;
    private String description;
    private String location;
    private String animalType;
    private String status;
    private String timestamp;
    private int imageResId;
    private String imageUrl; // URL ảnh từ server
    private String authorId;
    private String authorName;
    private String userAvatar; // Avatar URL của người đăng bài

    public RescuePost() {
    }

    public RescuePost(String id, String description, String location, String animalType,
                      String status, String timestamp, int imageResId, String authorId, String authorName) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.animalType = animalType;
        this.status = status;
        this.timestamp = timestamp;
        this.imageResId = imageResId;
        this.authorId = authorId;
        this.authorName = authorName;
    }
    
    public RescuePost(String id, String description, String location, String animalType,
                      String status, String timestamp, int imageResId, String imageUrl, String authorId, String authorName) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.animalType = animalType;
        this.status = status;
        this.timestamp = timestamp;
        this.imageResId = imageResId;
        this.imageUrl = imageUrl;
        this.authorId = authorId;
        this.authorName = authorName;
    }
    
    public RescuePost(String id, String description, String location, String animalType,
                      String status, String timestamp, int imageResId, String imageUrl, String authorId, String authorName, String userAvatar) {
        this.id = id;
        this.description = description;
        this.location = location;
        this.animalType = animalType;
        this.status = status;
        this.timestamp = timestamp;
        this.imageResId = imageResId;
        this.imageUrl = imageUrl;
        this.authorId = authorId;
        this.authorName = authorName;
        this.userAvatar = userAvatar;
    }

    // Getters
    public String getId() { return id; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public String getAnimalType() { return animalType; }
    public String getStatus() { return status; }
    public String getTimestamp() { return timestamp; }
    public int getImageResId() { return imageResId; }
    public String getImageUrl() { return imageUrl; }
    public String getAuthorId() { return authorId; }
    public String getAuthorName() { return authorName; }
    public String getUserAvatar() { return userAvatar; }
    
    // Helper methods for compatibility
    public String getUserName() { return authorName; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setDescription(String description) { this.description = description; }
    public void setLocation(String location) { this.location = location; }
    public void setAnimalType(String animalType) { this.animalType = animalType; }
    public void setStatus(String status) { this.status = status; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
}

