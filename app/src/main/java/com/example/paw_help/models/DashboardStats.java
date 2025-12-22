package com.example.paw_help.models;

import com.google.gson.annotations.SerializedName;

public class DashboardStats {
    // For /dashboard/stats
    @SerializedName("sosPosts")
    private int sosCount;
    
    @SerializedName("rescuedPosts")
    private int rescuedCount;
    
    @SerializedName("totalPosts")
    private int totalPosts;
    
    @SerializedName("inProgressPosts")
    private int inProgressPosts;
    
    @SerializedName("totalUsers")
    private int totalUsers;
    
    @SerializedName("recentPosts")
    private int recentPosts;
    
    @SerializedName("totalRequests")
    private int totalRequests;
    
    // For /dashboard/user-stats
    @SerializedName("pendingPosts")
    private int pendingPosts;
    
    // Legacy field
    private int activeVolunteers;

    // Getters
    public int getSosCount() {
        return sosCount;
    }

    public void setSosCount(int sosCount) {
        this.sosCount = sosCount;
    }

    public int getRescuedCount() {
        return rescuedCount;
    }

    public void setRescuedCount(int rescuedCount) {
        this.rescuedCount = rescuedCount;
    }

    public int getTotalPosts() {
        return totalPosts;
    }

    public void setTotalPosts(int totalPosts) {
        this.totalPosts = totalPosts;
    }

    public int getActiveVolunteers() {
        return activeVolunteers;
    }

    public void setActiveVolunteers(int activeVolunteers) {
        this.activeVolunteers = activeVolunteers;
    }

    public int getInProgressPosts() {
        return inProgressPosts;
    }

    public void setInProgressPosts(int inProgressPosts) {
        this.inProgressPosts = inProgressPosts;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getRecentPosts() {
        return recentPosts;
    }

    public void setRecentPosts(int recentPosts) {
        this.recentPosts = recentPosts;
    }

    public int getTotalRequests() {
        return totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getPendingPosts() {
        return pendingPosts;
    }

    public void setPendingPosts(int pendingPosts) {
        this.pendingPosts = pendingPosts;
    }
}

