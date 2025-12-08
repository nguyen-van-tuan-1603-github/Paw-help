// Users Management Page

let allUsers = [];

// Load users
async function loadUsers() {
    const tableBody = document.getElementById('usersTable');
    tableBody.innerHTML = '<tr><td colspan="9" class="text-center"><i class="fas fa-spinner fa-spin"></i> Đang tải...</td></tr>';
    
    try {
        // Note: This endpoint doesn't exist in backend yet, using mock data
        // When implemented, use: const response = await apiGet(API_ENDPOINTS.USERS);
        
        // Mock data for demonstration
        const mockUsers = [];
        
        // Try to get from API first
        try {
            // This will fail since endpoint doesn't exist yet
            const response = await apiGet(`${API_BASE_URL}/users`);
            allUsers = response.data || [];
        } catch {
            // Use mock data if API fails
            allUsers = mockUsers;
        }
        
        renderUsersTable(allUsers);
        updateUserStats();
    } catch (error) {
        console.error('Error loading users:', error);
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center" style="color: #F44336;">Lỗi khi tải dữ liệu. Endpoint chưa được implement.</td></tr>';
    }
}

// Render users table
function renderUsersTable(users) {
    const tableBody = document.getElementById('usersTable');
    
    if (users.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center">Chưa có người dùng nào. API endpoint đang được phát triển.</td></tr>';
        return;
    }
    
    tableBody.innerHTML = users.map(user => `
        <tr>
            <td>${user.userId || user.user_id}</td>
            <td>
                ${user.avatarUrl || user.avatar_url ? `
                    <img src="${API_BASE_URL.replace('/api', '')}${user.avatarUrl || user.avatar_url}" 
                         alt="${user.fullName || user.full_name}" 
                         class="avatar">
                ` : `
                    <div class="avatar-placeholder">${getInitials(user.fullName || user.full_name)}</div>
                `}
            </td>
            <td>${user.fullName || user.full_name || '-'}</td>
            <td>${user.email || '-'}</td>
            <td>${user.phone || '-'}</td>
            <td><span class="badge badge-${user.userRole || user.user_role}">${ROLE_LABELS[user.userRole || user.user_role] || (user.userRole || user.user_role)}</span></td>
            <td>${timeAgo(user.createdAt || user.created_at)}</td>
            <td>${timeAgo(user.lastLogin || user.last_login)}</td>
            <td>
                <div class="action-btns">
                    <button class="btn btn-sm btn-icon btn-info" onclick="viewUserDetail(${user.userId || user.user_id})" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Update user statistics
function updateUserStats() {
    const totalUsers = allUsers.length;
    const activeUsers = allUsers.filter(u => {
        const lastLogin = new Date(u.lastLogin || u.last_login);
        const daysSinceLogin = (Date.now() - lastLogin.getTime()) / (1000 * 60 * 60 * 24);
        return daysSinceLogin <= 7;
    }).length;
    const adminUsers = allUsers.filter(u => (u.userRole || u.user_role) === 'admin').length;
    
    document.getElementById('totalUsers').textContent = formatNumber(totalUsers);
    document.getElementById('activeUsers').textContent = formatNumber(activeUsers);
    document.getElementById('adminUsers').textContent = formatNumber(adminUsers);
}

// View user detail
async function viewUserDetail(userId) {
    try {
        const user = allUsers.find(u => (u.userId || u.user_id) === userId);
        
        if (!user) {
            showToast('Không tìm thấy người dùng', 'error');
            return;
        }
        
        const modalBody = document.getElementById('modalViewBody');
        modalBody.innerHTML = `
            <div style="display: flex; flex-direction: column; gap: 20px;">
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #00897B, #00695C); border-radius: 8px;">
                    ${user.avatarUrl || user.avatar_url ? `
                        <img src="${API_BASE_URL.replace('/api', '')}${user.avatarUrl || user.avatar_url}" 
                             alt="${user.fullName || user.full_name}" 
                             style="width: 100px; height: 100px; border-radius: 50%; border: 4px solid white;">
                    ` : `
                        <div style="width: 100px; height: 100px; border-radius: 50%; background: white; color: #00897B; display: inline-flex; align-items: center; justify-content: center; font-size: 36px; font-weight: 700;">
                            ${getInitials(user.fullName || user.full_name)}
                        </div>
                    `}
                    <h3 style="color: white; margin-top: 15px; margin-bottom: 5px;">${user.fullName || user.full_name}</h3>
                    <p style="color: rgba(255,255,255,0.9); margin: 0;">${user.email}</p>
                </div>
                
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px;">
                    <div>
                        <strong style="color: #757575; font-size: 13px;">ID</strong>
                        <div style="font-size: 15px; margin-top: 4px;">#${user.userId || user.user_id}</div>
                    </div>
                    
                    <div>
                        <strong style="color: #757575; font-size: 13px;">Vai trò</strong>
                        <div style="margin-top: 4px;">
                            <span class="badge badge-${user.userRole || user.user_role}">
                                ${ROLE_LABELS[user.userRole || user.user_role] || (user.userRole || user.user_role)}
                            </span>
                        </div>
                    </div>
                    
                    <div>
                        <strong style="color: #757575; font-size: 13px;">Số điện thoại</strong>
                        <div style="font-size: 15px; margin-top: 4px;">${user.phone || '-'}</div>
                    </div>
                    
                    <div>
                        <strong style="color: #757575; font-size: 13px;">Đăng ký</strong>
                        <div style="font-size: 15px; margin-top: 4px;">${formatDate(user.createdAt || user.created_at)}</div>
                    </div>
                    
                    <div style="grid-column: 1 / -1;">
                        <strong style="color: #757575; font-size: 13px;">Đăng nhập cuối</strong>
                        <div style="font-size: 15px; margin-top: 4px;">${formatDate(user.lastLogin || user.last_login) || 'Chưa đăng nhập'}</div>
                    </div>
                </div>
                
                ${user.stats ? `
                    <div style="background: #f5f5f5; padding: 15px; border-radius: 8px;">
                        <h4 style="margin-bottom: 15px; font-size: 16px;">Thống kê hoạt động</h4>
                        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 12px;">
                            <div>
                                <div style="color: #757575; font-size: 13px;">Bài đăng</div>
                                <div style="font-size: 24px; font-weight: 700; color: #00897B;">${user.stats.totalPosts || 0}</div>
                            </div>
                            <div>
                                <div style="color: #757575; font-size: 13px;">Đã cứu hộ</div>
                                <div style="font-size: 24px; font-weight: 700; color: #4CAF50;">${user.stats.rescuedPosts || 0}</div>
                            </div>
                        </div>
                    </div>
                ` : ''}
            </div>
        `;
        
        openModal('viewModal');
    } catch (error) {
        console.error('Error viewing user detail:', error);
        showToast('Lỗi khi xem chi tiết người dùng', 'error');
    }
}

// Filter users
function filterUsers() {
    const roleFilter = document.getElementById('roleFilter').value;
    
    if (!roleFilter) {
        renderUsersTable(allUsers);
        return;
    }
    
    const filtered = allUsers.filter(user => (user.userRole || user.user_role) === roleFilter);
    renderUsersTable(filtered);
}

// Search users
function searchUsers() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        renderUsersTable(allUsers);
        return;
    }
    
    const filtered = allUsers.filter(user =>
        (user.fullName || user.full_name || '').toLowerCase().includes(searchTerm) ||
        (user.email || '').toLowerCase().includes(searchTerm) ||
        (user.phone || '').includes(searchTerm)
    );
    
    renderUsersTable(filtered);
}

// Initialize
window.addEventListener('DOMContentLoaded', () => {
    loadUsers();
});

