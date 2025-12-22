package com.example.paw_help.models;

import com.google.gson.annotations.SerializedName;

public class GuestReportResponse {

    @SerializedName("postId")
    private int postId;

    @SerializedName("requestId")
    private String requestId;

    public GuestReportResponse() {
    }

    public GuestReportResponse(int postId, String requestId) {
        this.postId = postId;
        this.requestId = requestId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}

