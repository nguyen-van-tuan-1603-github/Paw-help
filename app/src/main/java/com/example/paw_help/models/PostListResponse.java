package com.example.paw_help.models;

import java.util.List;

public class PostListResponse {
    private List<PostItem> items;
    private PaginationInfo pagination;

    public List<PostItem> getItems() {
        return items;
    }

    public void setItems(List<PostItem> items) {
        this.items = items;
    }

    public PaginationInfo getPagination() {
        return pagination;
    }

    public void setPagination(PaginationInfo pagination) {
        this.pagination = pagination;
    }
}

