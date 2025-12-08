const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { authenticate } = require('../middleware/auth');

// ==================== GET DASHBOARD STATS ====================

router.get('/stats', async (req, res) => {
    try {
        // Get total posts by status
        const [statusStats] = await db.query(`
            SELECT 
                COUNT(*) as totalPosts,
                SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as sosPosts,
                SUM(CASE WHEN status = 'rescued' THEN 1 ELSE 0 END) as rescuedPosts,
                SUM(CASE WHEN status = 'in_progress' THEN 1 ELSE 0 END) as inProgressPosts
            FROM rescue_posts
        `);

        // Get total users
        const [userStats] = await db.query(`
            SELECT COUNT(*) as totalUsers
            FROM users
        `);

        // Get recent posts count (last 7 days)
        const [recentStats] = await db.query(`
            SELECT COUNT(*) as recentPosts
            FROM rescue_posts
            WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)
        `);

        // Get rescue requests count
        const [requestStats] = await db.query(`
            SELECT COUNT(*) as totalRequests
            FROM rescue_requests
        `);

        res.json({
            success: true,
            data: {
                totalPosts: statusStats[0].totalPosts || 0,
                sosPosts: statusStats[0].sosPosts || 0,
                rescuedPosts: statusStats[0].rescuedPosts || 0,
                inProgressPosts: statusStats[0].inProgressPosts || 0,
                totalUsers: userStats[0].totalUsers || 0,
                recentPosts: recentStats[0].recentPosts || 0,
                totalRequests: requestStats[0].totalRequests || 0
            }
        });

    } catch (error) {
        console.error('Get dashboard stats error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thống kê'
            }
        });
    }
});

// ==================== GET USER STATS ====================

router.get('/user-stats', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;

        // Get user's post stats
        const [postStats] = await db.query(`
            SELECT 
                COUNT(*) as totalPosts,
                SUM(CASE WHEN status = 'rescued' THEN 1 ELSE 0 END) as rescuedPosts,
                SUM(CASE WHEN status = 'pending' THEN 1 ELSE 0 END) as pendingPosts
            FROM rescue_posts
            WHERE user_id = ?
        `, [userId]);

        // Get user's rescue requests
        const [requestStats] = await db.query(`
            SELECT COUNT(*) as totalRequests
            FROM rescue_requests
            WHERE user_id = ?
        `, [userId]);

        res.json({
            success: true,
            data: {
                totalPosts: postStats[0].totalPosts || 0,
                rescuedPosts: postStats[0].rescuedPosts || 0,
                pendingPosts: postStats[0].pendingPosts || 0,
                totalRequests: requestStats[0].totalRequests || 0
            }
        });

    } catch (error) {
        console.error('Get user stats error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thống kê người dùng'
            }
        });
    }
});

module.exports = router;

