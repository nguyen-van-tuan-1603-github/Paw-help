// API Helper Functions

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

// API Request wrapper
async function apiRequest(url, options = {}) {
    const token = getAuthToken();
    
    const defaultHeaders = {
        'Content-Type': 'application/json'
    };
    
    if (token) {
        defaultHeaders['Authorization'] = `Bearer ${token}`;
    }
    
    const config = {
        ...options,
        headers: {
            ...defaultHeaders,
            ...options.headers
        }
    };
    
    try {
        const response = await fetch(url, config);
        const data = await response.json();
        
        // Handle unauthorized
        if (response.status === 401) {
            logout();
            throw new Error('Phiên đăng nhập đã hết hạn');
        }
        
        if (!response.ok) {
            throw new Error(data.error?.message || 'Có lỗi xảy ra');
        }
        
        return data;
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// GET request
async function apiGet(url) {
    return apiRequest(url, { method: 'GET' });
}

// POST request
async function apiPost(url, data) {
    return apiRequest(url, {
        method: 'POST',
        body: JSON.stringify(data)
    });
}

// PUT request
async function apiPut(url, data) {
    return apiRequest(url, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
}

// PATCH request
async function apiPatch(url, data) {
    return apiRequest(url, {
        method: 'PATCH',
        body: JSON.stringify(data)
    });
}

// DELETE request
async function apiDelete(url) {
    return apiRequest(url, { method: 'DELETE' });
}

// Show toast notification
function showToast(message, type = 'success') {
    // Create toast element
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
        <span>${message}</span>
    `;
    
    // Add styles if not exist
    if (!document.getElementById('toast-styles')) {
        const style = document.createElement('style');
        style.id = 'toast-styles';
        style.textContent = `
            .toast {
                position: fixed;
                top: 20px;
                right: 20px;
                padding: 16px 24px;
                border-radius: 8px;
                color: white;
                display: flex;
                align-items: center;
                gap: 12px;
                z-index: 9999;
                animation: slideInRight 0.3s ease, slideOutRight 0.3s ease 2.7s;
                box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            }
            .toast-success {
                background: linear-gradient(135deg, #4CAF50, #388E3C);
            }
            .toast-error {
                background: linear-gradient(135deg, #F44336, #D32F2F);
            }
            .toast i {
                font-size: 20px;
            }
            @keyframes slideInRight {
                from {
                    transform: translateX(400px);
                    opacity: 0;
                }
                to {
                    transform: translateX(0);
                    opacity: 1;
                }
            }
            @keyframes slideOutRight {
                from {
                    transform: translateX(0);
                    opacity: 1;
                }
                to {
                    transform: translateX(400px);
                    opacity: 0;
                }
            }
        `;
        document.head.appendChild(style);
    }
    
    document.body.appendChild(toast);
    
    // Remove after 3 seconds
    setTimeout(() => {
        toast.remove();
    }, 3000);
}

// Toggle sidebar on mobile
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    sidebar.classList.toggle('mobile-open');
}

// Modal functions
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('active');
    }
}

function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('active');
    }
}

// Close modal when clicking outside
document.addEventListener('click', (e) => {
    if (e.target.classList.contains('modal')) {
        closeModal(e.target.id);
    }
});

// Check auth on page load
window.addEventListener('DOMContentLoaded', () => {
    const currentPage = window.location.pathname.split('/').pop();
    
    // Skip auth check on login page
    if (currentPage === 'index.html' || currentPage === '') {
        return;
    }
    
    // Check if logged in
    if (!isLoggedIn()) {
        window.location.href = 'index.html';
        return;
    }
    
    // Display user name
    const user = getCurrentUser();
    if (user) {
        const userNameEl = document.getElementById('userName');
        if (userNameEl) {
            userNameEl.textContent = user.fullName || user.email;
        }
    }
});

// Format number with thousand separator
function formatNumber(num) {
    if (!num && num !== 0) return '-';
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
}

// Confirm dialog
function confirmDialog(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

