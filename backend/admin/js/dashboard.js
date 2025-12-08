// Dashboard Page

let statusChart = null;
let weeklyChart = null;

// Load dashboard data
async function loadDashboard() {
    try {
        // Load stats
        await loadStats();
        
        // Load charts
        await loadCharts();
        
        // Load recent posts
        await loadRecentPosts();
    } catch (error) {
        console.error('Error loading dashboard:', error);
        showToast('Lỗi khi tải dữ liệu dashboard', 'error');
    }
}

// Load statistics
async function loadStats() {
    try {
        const response = await apiGet(API_ENDPOINTS.DASHBOARD_STATS);
        
        if (response.success) {
            const stats = response.data;
            
            document.getElementById('totalPosts').textContent = formatNumber(stats.totalPosts || 0);
            document.getElementById('sosPosts').textContent = formatNumber(stats.sosPosts || 0);
            document.getElementById('rescuedPosts').textContent = formatNumber(stats.rescuedPosts || 0);
            document.getElementById('totalUsers').textContent = formatNumber(stats.totalUsers || 0);
        }
    } catch (error) {
        console.error('Error loading stats:', error);
    }
}

// Load charts
async function loadCharts() {
    try {
        const response = await apiGet(API_ENDPOINTS.DASHBOARD_STATS);
        
        if (response.success) {
            const stats = response.data;
            
            // Status Chart (Pie)
            const statusCtx = document.getElementById('statusChart').getContext('2d');
            
            // Destroy existing chart if any
            if (statusChart) {
                statusChart.destroy();
            }
            
            statusChart = new Chart(statusCtx, {
                type: 'doughnut',
                data: {
                    labels: ['Đang chờ', 'Đang xử lý', 'Đã cứu hộ'],
                    datasets: [{
                        data: [
                            stats.sosPosts || 0,
                            stats.inProgressPosts || 0,
                            stats.rescuedPosts || 0
                        ],
                        backgroundColor: [
                            '#FF9800',
                            '#2196F3',
                            '#4CAF50'
                        ],
                        borderWidth: 0
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            position: 'bottom'
                        }
                    }
                }
            });
            
            // Weekly Chart (Bar)
            const weeklyCtx = document.getElementById('weeklyChart').getContext('2d');
            
            // Destroy existing chart if any
            if (weeklyChart) {
                weeklyChart.destroy();
            }
            
            // Generate last 7 days
            const labels = [];
            const data = [];
            for (let i = 6; i >= 0; i--) {
                const date = new Date();
                date.setDate(date.getDate() - i);
                labels.push(`${date.getDate()}/${date.getMonth() + 1}`);
                // Mock data - replace with real data from API
                data.push(Math.floor(Math.random() * 10) + 1);
            }
            
            weeklyChart = new Chart(weeklyCtx, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: 'Số bài đăng',
                        data: data,
                        backgroundColor: 'rgba(0, 137, 123, 0.8)',
                        borderColor: '#00897B',
                        borderWidth: 2,
                        borderRadius: 6
                    }]
                },
                options: {
                    responsive: true,
                    maintainAspectRatio: false,
                    plugins: {
                        legend: {
                            display: false
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                stepSize: 1
                            }
                        }
                    }
                }
            });
        }
    } catch (error) {
        console.error('Error loading charts:', error);
    }
}

// Load recent posts
async function loadRecentPosts() {
    const tableBody = document.getElementById('recentPostsTable');
    
    try {
        const response = await apiGet(`${API_ENDPOINTS.POSTS}?page=1&limit=5`);
        
        if (response.success && response.data.items.length > 0) {
            tableBody.innerHTML = response.data.items.map(post => `
                <tr>
                    <td>${post.postId}</td>
                    <td>${post.animalType || '-'}</td>
                    <td>${truncateText(post.location, 30)}</td>
                    <td><span class="badge badge-${post.status}">${STATUS_LABELS[post.status] || post.status}</span></td>
                    <td>${post.userName || '-'}</td>
                    <td>${timeAgo(post.createdAt)}</td>
                    <td>
                        <div class="action-btns">
                            <button class="btn btn-sm btn-info" onclick="viewPost(${post.postId})" title="Xem chi tiết">
                                <i class="fas fa-eye"></i>
                            </button>
                        </div>
                    </td>
                </tr>
            `).join('');
        } else {
            tableBody.innerHTML = '<tr><td colspan="7" class="text-center">Chưa có bài đăng nào</td></tr>';
        }
    } catch (error) {
        console.error('Error loading recent posts:', error);
        tableBody.innerHTML = '<tr><td colspan="7" class="text-center" style="color: #F44336;">Lỗi khi tải dữ liệu</td></tr>';
    }
}

// View post detail
function viewPost(postId) {
    window.location.href = `posts.html?id=${postId}`;
}

// Initialize dashboard
window.addEventListener('DOMContentLoaded', () => {
    loadDashboard();
    
    // Refresh every 30 seconds
    setInterval(loadStats, 30000);
});

