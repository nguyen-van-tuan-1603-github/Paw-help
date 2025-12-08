// Authentication utilities

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
        showToast('Bạn không có quyền truy cập trang này', 'error');
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
        showToast('Phiên đăng nhập đã hết hạn', 'error');
        logout();
    }, SESSION_TIMEOUT);
}

// Reset timer on user activity
if (isLoggedIn()) {
    resetSessionTimer();
    
    ['mousedown', 'keydown', 'scroll', 'touchstart'].forEach(event => {
        document.addEventListener(event, resetSessionTimer, true);
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

