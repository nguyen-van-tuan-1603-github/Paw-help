package com.example.paw_help.api;

import com.example.paw_help.models.*;
import retrofit2.Call;
import retrofit2.http.*;

public interface PawHelpApi {
    
    // ==================== AUTH ====================
    
    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);
    
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);
    
    @GET("auth/me")
    Call<ApiResponse<User>> getCurrentUser();
    
    // ==================== POSTS ====================
    
    @GET("posts")
    Call<ApiResponse<PostListResponse>> getPosts(
        @Query("page") int page,
        @Query("limit") int limit
    );
    
    @GET("posts/{id}")
    Call<ApiResponse<PostItem>> getPost(@Path("id") int id);
    
    @GET("posts/my-posts")
    Call<ApiResponse<PostListResponse>> getMyPosts();
    
    // ==================== USER PROFILE ====================
    
    @GET("users/profile")
    Call<ApiResponse<User>> getProfile();
    
    // ==================== DASHBOARD ====================
    
    @GET("dashboard/stats")
    Call<ApiResponse<DashboardStats>> getDashboardStats();
}

