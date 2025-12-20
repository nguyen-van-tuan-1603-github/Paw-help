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
    
    // Tạo bài đăng (chỉ gửi text, ảnh có thể bổ sung sau)
    @FormUrlEncoded
    @POST("posts")
    Call<ApiResponse<PostItem>> createPost(
        @Field("animalType") String animalType,
        @Field("description") String description,
        @Field("location") String location,
        @Field("latitude") Double latitude,
        @Field("longitude") Double longitude
    );
    
    // ==================== USER PROFILE ====================
    
    @GET("users/profile")
    Call<ApiResponse<User>> getProfile();

    @FormUrlEncoded
    @PUT("users/profile")
    Call<ApiResponse<User>> updateProfile(
        @Field("fullName") String fullName,
        @Field("phone") String phone
    );
    
    // ==================== DASHBOARD ====================
    
    @GET("dashboard/stats")
    Call<ApiResponse<DashboardStats>> getDashboardStats();

    // ==================== NOTIFICATIONS ====================

    @GET("notifications")
    Call<ApiResponse<Object>> getNotifications(
        @Query("page") int page,
        @Query("limit") int limit
    );

    @PATCH("notifications/read-all")
    Call<ApiResponse<Object>> markAllNotificationsAsRead();
}

