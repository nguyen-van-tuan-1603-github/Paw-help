package com.example.paw_help.models;

import com.google.gson.annotations.SerializedName;

public class PaginationInfo {
    @SerializedName("currentPage")
    private int currentPage;
    
    @SerializedName("pageSize")
    private int pageSize;
    
    @SerializedName("totalItems")
    private int totalItems;
    
    @SerializedName("totalPages")
    private int totalPages;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    // Legacy getters để tương thích với code cũ
    public int getPage() {
        return currentPage;
    }

    public int getLimit() {
        return pageSize;
    }

    public int getTotal() {
        return totalItems;
    }
}

