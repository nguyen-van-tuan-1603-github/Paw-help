const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const db = require('../config/database');
const { authenticate } = require('../middleware/auth');
const multer = require('multer');
const path = require('path');

// ==================== FILE UPLOAD ====================

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'avatar-' + uniqueSuffix + path.extname(file.originalname));
    }
});

const upload = multer({
    storage: storage,
    limits: { fileSize: 2 * 1024 * 1024 }, // 2MB
    fileFilter: (req, file, cb) => {
        const allowedTypes = /jpeg|jpg|png|gif/;
        const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
        const mimetype = allowedTypes.test(file.mimetype);

        if (mimetype && extname) {
            return cb(null, true);
        } else {
            cb(new Error('Chỉ chấp nhận file ảnh'));
        }
    }
});

// ==================== GET PROFILE ====================

router.get('/profile', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;

        const [users] = await db.query(`
            SELECT 
                user_id as userId,
                full_name as fullName,
                email,
                phone,
                user_role as userRole,
                avatar_url as avatarUrl,
                created_at as createdAt,
                last_login as lastLogin
            FROM users
            WHERE user_id = ?
        `, [userId]);

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                error: {
                    code: 'USER_NOT_FOUND',
                    message: 'Người dùng không tồn tại'
                }
            });
        }

        // Get user stats
        const [stats] = await db.query(`
            SELECT 
                COUNT(*) as totalPosts,
                SUM(CASE WHEN status = 'rescued' THEN 1 ELSE 0 END) as rescuedPosts
            FROM rescue_posts
            WHERE user_id = ?
        `, [userId]);

        res.json({
            success: true,
            data: {
                ...users[0],
                stats: stats[0]
            }
        });

    } catch (error) {
        console.error('Get profile error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thông tin profile'
            }
        });
    }
});

// ==================== UPDATE PROFILE ====================

router.put('/profile', authenticate, upload.single('avatar'), async (req, res) => {
    try {
        const userId = req.user.userId;
        const { fullName, phone } = req.body;
        
        let avatarUrl = null;
        if (req.file) {
            avatarUrl = `/uploads/${req.file.filename}`;
        }

        let updateQuery = 'UPDATE users SET ';
        const updateFields = [];
        const updateValues = [];

        if (fullName) {
            updateFields.push('full_name = ?');
            updateValues.push(fullName);
        }

        if (phone) {
            updateFields.push('phone = ?');
            updateValues.push(phone);
        }

        if (avatarUrl) {
            updateFields.push('avatar_url = ?');
            updateValues.push(avatarUrl);
        }

        if (updateFields.length === 0) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'NO_UPDATE_FIELDS',
                    message: 'Không có thông tin để cập nhật'
                }
            });
        }

        updateQuery += updateFields.join(', ') + ' WHERE user_id = ?';
        updateValues.push(userId);

        await db.query(updateQuery, updateValues);

        res.json({
            success: true,
            message: 'Cập nhật profile thành công'
        });

    } catch (error) {
        console.error('Update profile error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi cập nhật profile'
            }
        });
    }
});

// ==================== CHANGE PASSWORD ====================

router.post('/change-password', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;
        const { currentPassword, newPassword } = req.body;

        if (!currentPassword || !newPassword) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'VALIDATION_ERROR',
                    message: 'Thiếu thông tin mật khẩu'
                }
            });
        }

        if (newPassword.length < 6) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'WEAK_PASSWORD',
                    message: 'Mật khẩu mới phải có ít nhất 6 ký tự'
                }
            });
        }

        // Get current password hash
        const [users] = await db.query(
            'SELECT password_hash FROM users WHERE user_id = ?',
            [userId]
        );

        if (users.length === 0) {
            return res.status(404).json({
                success: false,
                error: {
                    code: 'USER_NOT_FOUND',
                    message: 'Người dùng không tồn tại'
                }
            });
        }

        // Verify current password
        const isValid = await bcrypt.compare(currentPassword, users[0].password_hash);

        if (!isValid) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'INVALID_PASSWORD',
                    message: 'Mật khẩu hiện tại không đúng'
                }
            });
        }

        // Hash new password
        const newPasswordHash = await bcrypt.hash(newPassword, 10);

        // Update password
        await db.query(
            'UPDATE users SET password_hash = ? WHERE user_id = ?',
            [newPasswordHash, userId]
        );

        res.json({
            success: true,
            message: 'Đổi mật khẩu thành công'
        });

    } catch (error) {
        console.error('Change password error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi đổi mật khẩu'
            }
        });
    }
});

module.exports = router;

