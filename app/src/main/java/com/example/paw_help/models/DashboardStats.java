package com.example.paw_help.models;

public class DashboardStats {
    private int sosCount;
    private int rescuedCount;
    private int totalPosts;
    private int activeVolunteers;

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
}

