const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { authenticate } = require('../middleware/auth');

// ==================== GET USER NOTIFICATIONS ====================

router.get('/', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 20;
        const offset = (page - 1) * limit;

        const [notifications] = await db.query(`
            SELECT 
                notification_id as notificationId,
                title,
                message,
                type,
                is_read as isRead,
                created_at as createdAt
            FROM notifications
            WHERE user_id = ?
            ORDER BY created_at DESC
            LIMIT ? OFFSET ?
        `, [userId, limit, offset]);

        // Get unread count
        const [unreadCount] = await db.query(`
            SELECT COUNT(*) as count
            FROM notifications
            WHERE user_id = ? AND is_read = 0
        `, [userId]);

        res.json({
            success: true,
            data: {
                items: notifications,
                unreadCount: unreadCount[0].count
            }
        });

    } catch (error) {
        console.error('Get notifications error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thông báo'
            }
        });
    }
});

// ==================== MARK AS READ ====================

router.patch('/:id/read', authenticate, async (req, res) => {
    try {
        const notificationId = req.params.id;
        const userId = req.user.userId;

        await db.query(`
            UPDATE notifications
            SET is_read = 1
            WHERE notification_id = ? AND user_id = ?
        `, [notificationId, userId]);

        res.json({
            success: true,
            message: 'Đánh dấu đã đọc thành công'
        });

    } catch (error) {
        console.error('Mark notification as read error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi đánh dấu thông báo'
            }
        });
    }
});

// ==================== MARK ALL AS READ ====================

router.patch('/read-all', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;

        await db.query(`
            UPDATE notifications
            SET is_read = 1
            WHERE user_id = ? AND is_read = 0
        `, [userId]);

        res.json({
            success: true,
            message: 'Đánh dấu tất cả đã đọc thành công'
        });

    } catch (error) {
        console.error('Mark all notifications as read error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi đánh dấu thông báo'
            }
        });
    }
});

module.exports = router;

