package com.example.paw_help.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.paw_help.models.User;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    
    // ⚠️ QUAN TRỌNG: Đổi BASE_URL phù hợp với môi trường
    // Emulator: http://10.0.2.2:5125/api/
    // Thiết bị thật: http://192.168.X.X:5125/api/ (IP máy tính)
    // 
    // IP máy tính hiện tại: 192.168.1.16 (chạy lệnh ipconfig để kiểm tra)
    // 
    // CÁCH ĐỔI:
    // 1. Để chạy trên EMULATOR: dùng "http://10.0.2.2:5125/api/"
    // 2. Để chạy trên THIẾT BỊ THẬT: 
    //    - Chạy lệnh: ipconfig (Windows) hoặc ifconfig (Mac/Linux)
    //    - Tìm IPv4 Address (ví dụ: 192.168.1.16)
    //    - Đổi thành: "http://192.168.1.16:5125/api/"
    //    - Đảm bảo thiết bị và máy tính cùng mạng WiFi
    //    - Đảm bảo backend server đang chạy (node server.js)
    //    - Kiểm tra firewall không chặn port 5125
    
    // 🔄 CHỌN MÔI TRƯỜNG (đổi dòng này):
    private static final boolean USE_EMULATOR = false; // true = Emulator, false = Thiết bị thật
    
    // IP máy tính (cập nhật theo IP thực tế của máy bạn)
    private static final String PC_IP = "192.168.1.16";
    
    // Base URLs
    private static final String EMULATOR_URL = "http://10.0.2.2:5125/api/";
    private static final String DEVICE_URL = "http://" + PC_IP + ":5125/api/";
    
    // Chọn URL dựa trên môi trường
    private static final String BASE_URL = USE_EMULATOR ? EMULATOR_URL : DEVICE_URL;

    private static RetrofitClient instance;
    private PawHelpApi api;
    private Context context;
    
    private RetrofitClient(Context context) {
        this.context = context.getApplicationContext();
        
        // Logging interceptor (debug purposes)
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        // OkHttp client với JWT token interceptor
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(chain -> {
                Request originalRequest = chain.request();
                
                // Lấy token từ SharedPreferences
                String token = getToken();
                
                if (token != null && !token.isEmpty()) {
                    // Thêm Authorization header
                    Request newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer " + token)
                        .build();
                    return chain.proceed(newRequest);
                }
                
                return chain.proceed(originalRequest);
            })
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        
        // Retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
        
        api = retrofit.create(PawHelpApi.class);
    }
    
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }
    
    public PawHelpApi getApi() {
        return api;
    }
    
    public String getBaseUrl() {
        return BASE_URL;
    }
    
    // Lấy base URL cho images (không có /api/)
    public String getImageBaseUrl() {
        // Remove /api/ from end if present
        String url = BASE_URL;
        if (url.endsWith("/api/")) {
            url = url.substring(0, url.length() - 5);
        } else if (url.endsWith("/api")) {
            url = url.substring(0, url.length() - 4);
        }
        // Remove trailing slash
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    // ==================== TOKEN MANAGEMENT ====================
    
    public void saveToken(String token) {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        prefs.edit().putString("jwt_token", token).apply();
    }
    
    public String getToken() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        return prefs.getString("jwt_token", null);
    }
    
    public void clearToken() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        prefs.edit().remove("jwt_token").apply();
    }
    
    public boolean isLoggedIn() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }
    
    // ==================== USER INFO ====================
    
    public void saveUser(User user) {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        prefs.edit()
            .putInt("user_id", user.getUserId())
            .putString("user_name", user.getFullName())
            .putString("user_email", user.getEmail())
            .putString("user_avatar", user.getAvatarUrl())
            .putString("user_role", user.getUserRole())
            .apply();
    }
    
    public int getUserId() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        return prefs.getInt("user_id", -1);
    }
    
    public String getUserName() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        return prefs.getString("user_name", "");
    }
    
    public String getUserEmail() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        return prefs.getString("user_email", "");
    }
    
    public String getUserRole() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        return prefs.getString("user_role", "user");
    }
    
    public void clearUser() {
        SharedPreferences prefs = context.getSharedPreferences("PawHelp", Context.MODE_PRIVATE);
        prefs.edit()
            .remove("user_id")
            .remove("user_name")
            .remove("user_email")
            .remove("user_avatar")
            .remove("user_role")
            .apply();
    }
    
    public void logout() {
        clearToken();
        clearUser();
    }
}

