const express = require('express');
const router = express.Router();
const db = require('../config/database');
const { authenticate } = require('../middleware/auth');
const multer = require('multer');
const path = require('path');

// ==================== FILE UPLOAD CONFIGURATION ====================

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'uploads/');
    },
    filename: (req, file, cb) => {
        const uniqueSuffix = Date.now() + '-' + Math.round(Math.random() * 1E9);
        cb(null, 'rescue-' + uniqueSuffix + path.extname(file.originalname));
    }
});

const upload = multer({
    storage: storage,
    limits: { fileSize: 5 * 1024 * 1024 }, // 5MB
    fileFilter: (req, file, cb) => {
        const allowedTypes = /jpeg|jpg|png|gif/;
        const extname = allowedTypes.test(path.extname(file.originalname).toLowerCase());
        const mimetype = allowedTypes.test(file.mimetype);

        if (mimetype && extname) {
            return cb(null, true);
        } else {
            cb(new Error('Chỉ chấp nhận file ảnh (jpg, jpeg, png, gif)'));
        }
    }
});

// ==================== GET ALL POSTS ====================

router.get('/', async (req, res) => {
    try {
        const page = parseInt(req.query.page) || 1;
        const limit = parseInt(req.query.limit) || 10;
        const offset = (page - 1) * limit;
        const status = req.query.status || null;

        let query = `
            SELECT 
                rp.rescue_post_id as postId,
                rp.animal_type as animalType,
                rp.description,
                rp.location,
                rp.latitude,
                rp.longitude,
                rp.status,
                rp.image_url as imageUrl,
                rp.created_at as createdAt,
                u.user_id as userId,
                u.full_name as userName,
                u.avatar_url as userAvatar
            FROM rescue_posts rp
            INNER JOIN users u ON rp.user_id = u.user_id
        `;

        const params = [];

        if (status) {
            query += ' WHERE rp.status = ?';
            params.push(status);
        }

        query += ' ORDER BY rp.created_at DESC LIMIT ? OFFSET ?';
        params.push(limit, offset);

        const [posts] = await db.query(query, params);

        // Get total count
        let countQuery = 'SELECT COUNT(*) as total FROM rescue_posts';
        if (status) {
            countQuery += ' WHERE status = ?';
        }
        const [countResult] = await db.query(countQuery, status ? [status] : []);
        const total = countResult[0].total;

        res.json({
            success: true,
            data: {
                items: posts,
                pagination: {
                    currentPage: page,
                    pageSize: limit,
                    totalItems: total,
                    totalPages: Math.ceil(total / limit)
                }
            }
        });

    } catch (error) {
        console.error('Get posts error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy danh sách bài đăng'
            }
        });
    }
});

// ==================== GET POST BY ID ====================

router.get('/:id', async (req, res) => {
    try {
        const postId = req.params.id;

        const [posts] = await db.query(`
            SELECT 
                rp.rescue_post_id as postId,
                rp.animal_type as animalType,
                rp.description,
                rp.location,
                rp.latitude,
                rp.longitude,
                rp.status,
                rp.image_url as imageUrl,
                rp.created_at as createdAt,
                rp.updated_at as updatedAt,
                u.user_id as userId,
                u.full_name as userName,
                u.phone as userPhone,
                u.avatar_url as userAvatar
            FROM rescue_posts rp
            INNER JOIN users u ON rp.user_id = u.user_id
            WHERE rp.rescue_post_id = ?
        `, [postId]);

        if (posts.length === 0) {
            return res.status(404).json({
                success: false,
                error: {
                    code: 'POST_NOT_FOUND',
                    message: 'Bài đăng không tồn tại'
                }
            });
        }

        res.json({
            success: true,
            data: posts[0]
        });

    } catch (error) {
        console.error('Get post error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thông tin bài đăng'
            }
        });
    }
});

// ==================== CREATE POST ====================

router.post('/', authenticate, upload.single('image'), async (req, res) => {
    try {
        const { animalType, description, location, latitude, longitude } = req.body;
        const userId = req.user.userId;
        const imageUrl = req.file ? `/uploads/${req.file.filename}` : null;

        if (!animalType || !description || !location) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'VALIDATION_ERROR',
                    message: 'Thiếu thông tin bắt buộc'
                }
            });
        }

        const [result] = await db.query(`
            INSERT INTO rescue_posts 
            (user_id, animal_type, description, location, latitude, longitude, image_url, status, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, 'pending', NOW())
        `, [userId, animalType, description, location, latitude || null, longitude || null, imageUrl]);

        const postId = result.insertId;

        res.status(201).json({
            success: true,
            message: 'Tạo bài đăng thành công',
            data: {
                postId: postId
            }
        });

    } catch (error) {
        console.error('Create post error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi tạo bài đăng'
            }
        });
    }
});

// ==================== GET MY POSTS ====================

router.get('/my-posts', authenticate, async (req, res) => {
    try {
        const userId = req.user.userId;

        const [posts] = await db.query(`
            SELECT 
                rescue_post_id as postId,
                animal_type as animalType,
                description,
                location,
                status,
                image_url as imageUrl,
                created_at as createdAt
            FROM rescue_posts
            WHERE user_id = ?
            ORDER BY created_at DESC
        `, [userId]);

        res.json({
            success: true,
            data: {
                items: posts,
                pagination: {
                    currentPage: 1,
                    pageSize: posts.length,
                    totalItems: posts.length,
                    totalPages: 1
                }
            }
        });

    } catch (error) {
        console.error('Get my posts error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy bài đăng của bạn'
            }
        });
    }
});

// ==================== UPDATE POST STATUS ====================

router.patch('/:id/status', authenticate, async (req, res) => {
    try {
        const postId = req.params.id;
        const { status } = req.body;
        const userId = req.user.userId;

        if (!['pending', 'in_progress', 'rescued', 'closed'].includes(status)) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'INVALID_STATUS',
                    message: 'Trạng thái không hợp lệ'
                }
            });
        }

        // Check ownership
        const [posts] = await db.query(
            'SELECT user_id FROM rescue_posts WHERE rescue_post_id = ?',
            [postId]
        );

        if (posts.length === 0) {
            return res.status(404).json({
                success: false,
                error: {
                    code: 'POST_NOT_FOUND',
                    message: 'Bài đăng không tồn tại'
                }
            });
        }

        if (posts[0].user_id !== userId && req.user.role !== 'admin') {
            return res.status(403).json({
                success: false,
                error: {
                    code: 'FORBIDDEN',
                    message: 'Bạn không có quyền cập nhật bài đăng này'
                }
            });
        }

        await db.query(
            'UPDATE rescue_posts SET status = ?, updated_at = NOW() WHERE rescue_post_id = ?',
            [status, postId]
        );

        res.json({
            success: true,
            message: 'Cập nhật trạng thái thành công'
        });

    } catch (error) {
        console.error('Update post status error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi cập nhật trạng thái'
            }
        });
    }
});

module.exports = router;

