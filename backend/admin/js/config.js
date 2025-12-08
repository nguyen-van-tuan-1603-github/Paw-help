// API Configuration
const API_BASE_URL = 'http://localhost:5125/api';

// API Endpoints
const API_ENDPOINTS = {
    // Auth
    LOGIN: `${API_BASE_URL}/auth/login`,
    ME: `${API_BASE_URL}/auth/me`,
    
    // Posts
    POSTS: `${API_BASE_URL}/posts`,
    POST_BY_ID: (id) => `${API_BASE_URL}/posts/${id}`,
    POST_STATUS: (id) => `${API_BASE_URL}/posts/${id}/status`,
    MY_POSTS: `${API_BASE_URL}/posts/my-posts`,
    
    // Users
    USERS: `${API_BASE_URL}/users`,
    USER_PROFILE: `${API_BASE_URL}/users/profile`,
    
    // Dashboard
    DASHBOARD_STATS: `${API_BASE_URL}/dashboard/stats`,
    USER_STATS: `${API_BASE_URL}/dashboard/user-stats`,
    
    // Notifications
    NOTIFICATIONS: `${API_BASE_URL}/notifications`,
    NOTIFICATION_READ: (id) => `${API_BASE_URL}/notifications/${id}/read`,
    NOTIFICATIONS_READ_ALL: `${API_BASE_URL}/notifications/read-all`,
    
    // Team
    TEAM: `${API_BASE_URL}/team`
};

// Status labels
const STATUS_LABELS = {
    pending: 'Đang chờ',
    in_progress: 'Đang xử lý',
    rescued: 'Đã cứu hộ',
    closed: 'Đã đóng'
};

// Role labels
const ROLE_LABELS = {
    admin: 'Quản trị viên',
    user: 'Người dùng',
    rescuer: 'Người cứu hộ'
};

// Date formatter
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const day = date.getDate().toString().padStart(2, '0');
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const year = date.getFullYear();
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${day}/${month}/${year} ${hours}:${minutes}`;
}

// Time ago formatter
function timeAgo(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    const now = new Date();
    const seconds = Math.floor((now - date) / 1000);
    
    if (seconds < 60) return 'Vừa xong';
    if (seconds < 3600) return `${Math.floor(seconds / 60)} phút trước`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)} giờ trước`;
    if (seconds < 604800) return `${Math.floor(seconds / 86400)} ngày trước`;
    
    return formatDate(dateString);
}

// Truncate text
function truncateText(text, maxLength = 50) {
    if (!text) return '-';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
}

// Get initials from name
function getInitials(name) {
    if (!name) return '?';
    const parts = name.trim().split(' ');
    if (parts.length === 1) return parts[0].charAt(0).toUpperCase();
    return (parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
}

