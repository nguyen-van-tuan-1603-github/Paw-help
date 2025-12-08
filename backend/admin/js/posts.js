// Posts Management Page

let currentPage = 1;
let currentFilter = '';
let allPosts = [];

// Load posts
async function loadPosts(page = 1) {
    const tableBody = document.getElementById('postsTable');
    tableBody.innerHTML = '<tr><td colspan="9" class="text-center"><i class="fas fa-spinner fa-spin"></i> Đang tải...</td></tr>';
    
    try {
        let url = `${API_ENDPOINTS.POSTS}?page=${page}&limit=20`;
        
        if (currentFilter) {
            url += `&status=${currentFilter}`;
        }
        
        const response = await apiGet(url);
        
        if (response.success) {
            allPosts = response.data.items;
            currentPage = page;
            
            renderPostsTable(allPosts);
            renderPagination(response.data.pagination);
        }
    } catch (error) {
        console.error('Error loading posts:', error);
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center" style="color: #F44336;">Lỗi khi tải dữ liệu</td></tr>';
        showToast('Lỗi khi tải danh sách bài đăng', 'error');
    }
}

// Render posts table
function renderPostsTable(posts) {
    const tableBody = document.getElementById('postsTable');
    
    if (posts.length === 0) {
        tableBody.innerHTML = '<tr><td colspan="9" class="text-center">Không có bài đăng nào</td></tr>';
        return;
    }
    
    tableBody.innerHTML = posts.map(post => `
        <tr>
            <td>${post.postId}</td>
            <td>
                ${post.imageUrl ? `
                    <img src="${API_BASE_URL.replace('/api', '')}${post.imageUrl}" 
                         alt="Image" 
                         class="img-thumb"
                         onerror="this.src='https://via.placeholder.com/60?text=No+Image'">
                ` : `
                    <div class="img-thumb" style="background: #E0E0E0; display: flex; align-items: center; justify-content: center;">
                        <i class="fas fa-image" style="color: #999;"></i>
                    </div>
                `}
            </td>
            <td>${post.animalType || '-'}</td>
            <td>${truncateText(post.description, 40)}</td>
            <td>${truncateText(post.location, 30)}</td>
            <td><span class="badge badge-${post.status}">${STATUS_LABELS[post.status] || post.status}</span></td>
            <td>${post.userName || '-'}</td>
            <td>${timeAgo(post.createdAt)}</td>
            <td>
                <div class="action-btns">
                    <button class="btn btn-sm btn-icon btn-info" onclick="viewPostDetail(${post.postId})" title="Xem chi tiết">
                        <i class="fas fa-eye"></i>
                    </button>
                    <button class="btn btn-sm btn-icon btn-warning" onclick="openEditStatus(${post.postId}, '${post.status}')" title="Cập nhật">
                        <i class="fas fa-edit"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Render pagination
function renderPagination(pagination) {
    const paginationEl = document.getElementById('pagination');
    
    if (!pagination || pagination.totalPages <= 1) {
        paginationEl.innerHTML = '';
        return;
    }
    
    let html = '';
    
    // Previous button
    html += `
        <button class="page-btn" ${pagination.currentPage === 1 ? 'disabled' : ''} onclick="loadPosts(${pagination.currentPage - 1})">
            <i class="fas fa-chevron-left"></i>
        </button>
    `;
    
    // Page numbers
    for (let i = 1; i <= pagination.totalPages; i++) {
        if (
            i === 1 ||
            i === pagination.totalPages ||
            (i >= pagination.currentPage - 1 && i <= pagination.currentPage + 1)
        ) {
            html += `
                <button class="page-btn ${i === pagination.currentPage ? 'active' : ''}" onclick="loadPosts(${i})">
                    ${i}
                </button>
            `;
        } else if (i === pagination.currentPage - 2 || i === pagination.currentPage + 2) {
            html += '<span>...</span>';
        }
    }
    
    // Next button
    html += `
        <button class="page-btn" ${pagination.currentPage === pagination.totalPages ? 'disabled' : ''} onclick="loadPosts(${pagination.currentPage + 1})">
            <i class="fas fa-chevron-right"></i>
        </button>
    `;
    
    paginationEl.innerHTML = html;
}

// View post detail
async function viewPostDetail(postId) {
    try {
        const response = await apiGet(API_ENDPOINTS.POST_BY_ID(postId));
        
        if (response.success) {
            const post = response.data;
            
            const modalBody = document.getElementById('modalViewBody');
            modalBody.innerHTML = `
                <div class="row">
                    <div class="col-md-6">
                        ${post.imageUrl ? `
                            <img src="${API_BASE_URL.replace('/api', '')}${post.imageUrl}" 
                                 style="width: 100%; border-radius: 8px; margin-bottom: 20px;"
                                 alt="Post Image"
                                 onerror="this.src='https://via.placeholder.com/400?text=No+Image'">
                        ` : '<div style="background: #f5f5f5; height: 300px; display: flex; align-items: center; justify-content: center; border-radius: 8px; margin-bottom: 20px;"><i class="fas fa-image" style="font-size: 48px; color: #999;"></i></div>'}
                    </div>
                    <div class="col-md-6">
                        <div style="padding: 10px;">
                            <h4 style="margin-bottom: 20px; color: #00897B;">Thông tin bài đăng</h4>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>ID:</strong> #${post.postId}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Loại động vật:</strong> ${post.animalType || '-'}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Địa điểm:</strong><br>
                                ${post.location || '-'}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Trạng thái:</strong><br>
                                <span class="badge badge-${post.status}">${STATUS_LABELS[post.status] || post.status}</span>
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Người đăng:</strong> ${post.userName || '-'}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Số điện thoại:</strong> ${post.userPhone || '-'}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Ngày tạo:</strong> ${formatDate(post.createdAt)}
                            </div>
                            
                            <div style="margin-bottom: 15px;">
                                <strong>Mô tả:</strong><br>
                                <div style="background: #f5f5f5; padding: 12px; border-radius: 6px; margin-top: 8px;">
                                    ${post.description || '-'}
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Add basic grid styles
            if (!document.getElementById('grid-styles')) {
                const style = document.createElement('style');
                style.id = 'grid-styles';
                style.textContent = `
                    .row { display: flex; flex-wrap: wrap; margin: -10px; }
                    .col-md-6 { flex: 0 0 50%; max-width: 50%; padding: 10px; }
                    @media (max-width: 768px) {
                        .col-md-6 { flex: 0 0 100%; max-width: 100%; }
                    }
                `;
                document.head.appendChild(style);
            }
            
            openModal('viewModal');
        }
    } catch (error) {
        console.error('Error loading post detail:', error);
        showToast('Lỗi khi tải chi tiết bài đăng', 'error');
    }
}

// Open edit status modal
function openEditStatus(postId, currentStatus) {
    document.getElementById('editPostId').value = postId;
    document.getElementById('newStatus').value = currentStatus;
    openModal('statusModal');
}

// Update post status
document.getElementById('statusForm')?.addEventListener('submit', async (e) => {
    e.preventDefault();
    
    const postId = document.getElementById('editPostId').value;
    const newStatus = document.getElementById('newStatus').value;
    
    try {
        const response = await apiPatch(API_ENDPOINTS.POST_STATUS(postId), {
            status: newStatus
        });
        
        if (response.success) {
            showToast('Cập nhật trạng thái thành công');
            closeModal('statusModal');
            loadPosts(currentPage);
        }
    } catch (error) {
        console.error('Error updating status:', error);
        showToast('Lỗi khi cập nhật trạng thái', 'error');
    }
});

// Filter posts
function filterPosts() {
    currentFilter = document.getElementById('statusFilter').value;
    loadPosts(1);
}

// Search posts
function searchPosts() {
    const searchTerm = document.getElementById('searchInput').value.toLowerCase();
    
    if (!searchTerm) {
        renderPostsTable(allPosts);
        return;
    }
    
    const filtered = allPosts.filter(post =>
        (post.animalType || '').toLowerCase().includes(searchTerm) ||
        (post.description || '').toLowerCase().includes(searchTerm) ||
        (post.location || '').toLowerCase().includes(searchTerm) ||
        (post.userName || '').toLowerCase().includes(searchTerm)
    );
    
    renderPostsTable(filtered);
}

// Initialize
window.addEventListener('DOMContentLoaded', () => {
    loadPosts(1);
});

