package com.example.paw_help.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Utility class chứa các helper methods dùng chung trong app
 */
public class AppUtils {

    /**
     * Chuyển đổi status từ tiếng Anh sang tiếng Việt
     */
    public static String convertStatus(String status) {
        if (status == null) return "Chưa xác định";
        
        switch (status.toLowerCase()) {
            case "pending":
                return "Chờ xử lý";
            case "in_progress":
                return "Đang xử lý";
            case "rescued":
                return "Đã cứu";
            case "closed":
                return "Đã đóng";
            default:
                return status;
        }
    }

    /**
     * Lấy emoji cho loại động vật
     */
    public static String getEmojiForAnimalType(String animalType) {
        if (animalType == null) return "🐾";
        
        String type = animalType.toLowerCase();
        if (type.contains("chó") || type.contains("dog")) return "🐕";
        if (type.contains("mèo") || type.contains("cat")) return "🐈";
        if (type.contains("chim") || type.contains("bird")) return "🐦";
        if (type.contains("thỏ") || type.contains("rabbit")) return "🐰";
        if (type.contains("chuột") || type.contains("mouse") || type.contains("rat")) return "🐭";
        if (type.contains("rắn") || type.contains("snake")) return "🐍";
        if (type.contains("cá") || type.contains("fish")) return "🐟";
        
        return "🐾";
    }

    /**
     * Format thời gian thành dạng relative (vừa xong, 5 phút trước, etc.)
     */
    public static String formatTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            return "Vừa xong";
        }
        
        try {
            // Parse ISO 8601 format từ MySQL: "2024-01-15T10:30:00.000Z"
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            
            // Xử lý cả trường hợp có .SSS và không có
            String dateStr = createdAt;
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.indexOf("."));
            }
            if (dateStr.contains("Z")) {
                dateStr = dateStr.replace("Z", "");
            }
            
            java.util.Date date = inputFormat.parse(dateStr);
            
            long diff = System.currentTimeMillis() - date.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            
            if (seconds < 60) {
                return "Vừa xong";
            } else if (minutes < 60) {
                return minutes + " phút trước";
            } else if (hours < 24) {
                return hours + " giờ trước";
            } else if (days < 7) {
                return days + " ngày trước";
            } else {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                return outputFormat.format(date);
            }
        } catch (Exception e) {
            return "Vừa xong";
        }
    }

    /**
     * Format thời gian thành dạng đầy đủ (dd/MM/yyyy HH:mm)
     */
    public static String formatFullDateTime(String createdAt) {
        if (createdAt == null || createdAt.isEmpty()) {
            return "";
        }
        
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            
            String dateStr = createdAt;
            if (dateStr.contains(".")) {
                dateStr = dateStr.substring(0, dateStr.indexOf("."));
            }
            if (dateStr.contains("Z")) {
                dateStr = dateStr.replace("Z", "");
            }
            
            java.util.Date date = inputFormat.parse(dateStr);
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) return false;
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailPattern);
    }

    /**
     * Validate phone number (Vietnamese format: 10-11 digits)
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) return false;
        // Remove spaces, dashes, parentheses
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        // Check if it's 10-11 digits
        return cleanPhone.matches("^[0-9]{10,11}$");
    }

    /**
     * Format phone number for display
     */
    public static String formatPhone(String phone) {
        if (phone == null || phone.isEmpty()) return "";
        String cleanPhone = phone.replaceAll("[\\s\\-\\(\\)]", "");
        if (cleanPhone.length() == 10) {
            return cleanPhone.substring(0, 4) + " " + 
                   cleanPhone.substring(4, 7) + " " + 
                   cleanPhone.substring(7);
        } else if (cleanPhone.length() == 11) {
            return cleanPhone.substring(0, 4) + " " + 
                   cleanPhone.substring(4, 7) + " " + 
                   cleanPhone.substring(7);
        }
        return phone;
    }
}

