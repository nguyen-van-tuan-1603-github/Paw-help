package com.example.paw_help.models;

import com.google.gson.annotations.SerializedName;

public class CreatePostResponse {
    @SerializedName("postId")
    private int postId;

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }
}

