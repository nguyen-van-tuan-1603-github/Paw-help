package com.example.paw_help.utils;

/**
 * Constants class chứa các hằng số dùng chung trong app
 */
public class Constants {

    // ==================== POST STATUS ====================
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_IN_PROGRESS = "in_progress";
    public static final String STATUS_RESCUED = "rescued";
    public static final String STATUS_CLOSED = "closed";

    // ==================== ANIMAL TYPES ====================
    public static final String ANIMAL_TYPE_DOG = "Chó";
    public static final String ANIMAL_TYPE_CAT = "Mèo";
    public static final String ANIMAL_TYPE_BIRD = "Chim";
    public static final String ANIMAL_TYPE_RABBIT = "Thỏ";
    public static final String ANIMAL_TYPE_OTHER = "Khác";

    // ==================== SHARED PREFERENCES ====================
    public static final String PREF_NAME = "PawHelp";
    public static final String PREF_TOKEN = "jwt_token";
    public static final String PREF_USER_ID = "user_id";
    public static final String PREF_USER_NAME = "user_name";
    public static final String PREF_USER_EMAIL = "user_email";
    public static final String PREF_USER_AVATAR = "user_avatar";
    public static final String PREF_USER_ROLE = "user_role";

    // ==================== API PAGINATION ====================
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== NOTIFICATION TYPES ====================
    public static final String NOTIFICATION_TYPE_INFO = "info";
    public static final String NOTIFICATION_TYPE_SUCCESS = "success";
    public static final String NOTIFICATION_TYPE_WARNING = "warning";
    public static final String NOTIFICATION_TYPE_ERROR = "error";

    // ==================== REQUEST CODES ====================
    public static final int PERMISSION_REQUEST_CODE_STORAGE = 100;
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 101;
    public static final int PERMISSION_REQUEST_CODE_LOCATION = 102;
    public static final int PERMISSION_REQUEST_CODE_CALL = 103;

    // ==================== IMAGE ====================
    public static final int MAX_IMAGE_SIZE_MB = 5;
    public static final int IMAGE_COMPRESSION_QUALITY = 85;
    
    // ==================== VALIDATION ====================
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final int MAX_DESCRIPTION_LENGTH = 2000;
    public static final int MAX_LOCATION_LENGTH = 500;
}

