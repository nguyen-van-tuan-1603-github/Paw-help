// Authentication utilities

// Get auth token
function getAuthToken() {
    return localStorage.getItem('admin_token');
}

// Check if user is logged in
function isLoggedIn() {
    return !!getAuthToken();
}

// Get current admin user
function getCurrentUser() {
    const userStr = localStorage.getItem('admin_user');
    return userStr ? JSON.parse(userStr) : null;
}

// Logout
function logout() {
    localStorage.removeItem('admin_token');
    localStorage.removeItem('admin_user');
    localStorage.removeItem('admin_remember');
    window.location.href = 'index.html';
}

// Check if admin role
function isAdmin() {
    const user = getCurrentUser();
    return user && user.userRole === 'admin';
}

// Protect admin pages
function requireAdmin() {
    if (!isLoggedIn()) {
        window.location.href = 'index.html';
        return false;
    }
    
    if (!isAdmin()) {
        alert('Bạn không có quyền truy cập trang này');
        logout();
        return false;
    }
    
    return true;
}

// Session timeout (30 minutes)
const SESSION_TIMEOUT = 30 * 60 * 1000;
let sessionTimer;

function resetSessionTimer() {
    clearTimeout(sessionTimer);
    sessionTimer = setTimeout(() => {
        alert('Phiên đăng nhập đã hết hạn');
        logout();
    }, SESSION_TIMEOUT);
}

// Reset timer on user activity (chỉ chạy khi DOM đã load)
if (typeof document !== 'undefined') {
    document.addEventListener('DOMContentLoaded', () => {
        if (isLoggedIn()) {
            resetSessionTimer();
            
            ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
                document.addEventListener(event, resetSessionTimer, true);
            });
        }
    });
}

// Validate email
function isValidEmail(email) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
}

// Validate password strength
function validatePasswordStrength(password) {
    if (password.length < 6) {
        return { valid: false, message: 'Mật khẩu phải có ít nhất 6 ký tự' };
    }
    
    return { valid: true };
}

