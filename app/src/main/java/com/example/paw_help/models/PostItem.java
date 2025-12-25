package com.example.paw_help.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostItem {
    @SerializedName("postId")
    private int postId;
    
    private String description;
    
    @SerializedName("animalType")
    private String animalType; // API trả về string, không phải object
    
    private String location;
    
    private Double latitude;
    
    private Double longitude;
    
    @SerializedName("imageUrl")
    private String imageUrl;
    
    private String status;
    
    @SerializedName("createdAt")
    private String createdAt;
    
    @SerializedName("updatedAt")
    private String updatedAt;
    
    // User info (API trả về trực tiếp trong object, không phải nested object)
    @SerializedName("userId")
    private Integer userId;
    
    @SerializedName("userName")
    private String userName;
    
    @SerializedName("userAvatar")
    private String userAvatar;
    
    @SerializedName("userPhone")
    private String userPhone;

    // Getters and Setters
    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    // User info getters/setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    // Helper method để tương thích với code cũ đang dùng getUser()
    public PostUserDto getUser() {
        PostUserDto userDto = new PostUserDto();
        userDto.setUserId(userId != null ? userId : 0);
        userDto.setFullName(userName);
        userDto.setAvatarUrl(userAvatar);
        userDto.setPhone(userPhone);
        return userDto;
    }

    // Helper method để tương thích với code cũ đang dùng getContactPhone()
    public String getContactPhone() {
        return userPhone;
    }
}

