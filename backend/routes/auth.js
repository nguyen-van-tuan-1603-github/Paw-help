const express = require('express');
const router = express.Router();
const bcrypt = require('bcryptjs');
const { body, validationResult } = require('express-validator');
const db = require('../config/database');
const { generateToken } = require('../config/jwt');
const { authenticate } = require('../middleware/auth');

// ==================== REGISTER ====================

router.post('/register', [
    body('fullName').trim().notEmpty().withMessage('Tên đầy đủ không được để trống'),
    body('email').isEmail().withMessage('Email không hợp lệ'),
    body('phone').trim().notEmpty().withMessage('Số điện thoại không được để trống'),
    body('password').isLength({ min: 6 }).withMessage('Mật khẩu phải có ít nhất 6 ký tự')
], async (req, res) => {
    try {
        // Validate input
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'VALIDATION_ERROR',
                    message: errors.array()[0].msg
                }
            });
        }

        const { fullName, email, phone, password } = req.body;

        // Check if email exists
        const [existingUsers] = await db.query(
            'SELECT user_id FROM users WHERE email = ?',
            [email]
        );

        if (existingUsers.length > 0) {
            return res.status(409).json({
                success: false,
                error: {
                    code: 'EMAIL_EXISTS',
                    message: 'Email đã được sử dụng'
                }
            });
        }

        // Hash password
        const hashedPassword = await bcrypt.hash(password, 10);

        // Insert user
        const [result] = await db.query(
            `INSERT INTO users (full_name, email, phone, password_hash, user_role, created_at) 
             VALUES (?, ?, ?, ?, 'user', NOW())`,
            [fullName, email, phone, hashedPassword]
        );

        const userId = result.insertId;

        // Generate token
        const token = generateToken({
            userId: userId,
            email: email,
            role: 'user'
        });

        // Get user info
        const [users] = await db.query(
            'SELECT user_id, full_name, email, phone, user_role, avatar_url FROM users WHERE user_id = ?',
            [userId]
        );

        const user = users[0];

        res.status(201).json({
            success: true,
            message: 'Đăng ký thành công',
            data: {
                token: token,
                user: {
                    userId: user.user_id,
                    fullName: user.full_name,
                    email: user.email,
                    phone: user.phone,
                    userRole: user.user_role,
                    avatarUrl: user.avatar_url
                }
            }
        });

    } catch (error) {
        console.error('Register error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi server khi đăng ký'
            }
        });
    }
});

// ==================== LOGIN ====================

router.post('/login', [
    body('email').isEmail().withMessage('Email không hợp lệ'),
    body('password').notEmpty().withMessage('Mật khẩu không được để trống')
], async (req, res) => {
    try {
        // Validate input
        const errors = validationResult(req);
        if (!errors.isEmpty()) {
            return res.status(400).json({
                success: false,
                error: {
                    code: 'VALIDATION_ERROR',
                    message: errors.array()[0].msg
                }
            });
        }

        const { email, password } = req.body;

        // Get user
        const [users] = await db.query(
            'SELECT user_id, full_name, email, phone, password_hash, user_role, avatar_url FROM users WHERE email = ?',
            [email]
        );

        if (users.length === 0) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'INVALID_CREDENTIALS',
                    message: 'Email hoặc mật khẩu không đúng'
                }
            });
        }

        const user = users[0];

        // Check password
        const isPasswordValid = await bcrypt.compare(password, user.password_hash);

        if (!isPasswordValid) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'INVALID_CREDENTIALS',
                    message: 'Email hoặc mật khẩu không đúng'
                }
            });
        }

        // Update last login
        await db.query(
            'UPDATE users SET last_login = NOW() WHERE user_id = ?',
            [user.user_id]
        );

        // Generate token
        const token = generateToken({
            userId: user.user_id,
            email: user.email,
            role: user.user_role
        });

        res.json({
            success: true,
            message: 'Đăng nhập thành công',
            data: {
                token: token,
                user: {
                    userId: user.user_id,
                    fullName: user.full_name,
                    email: user.email,
                    phone: user.phone,
                    userRole: user.user_role,
                    avatarUrl: user.avatar_url
                }
            }
        });

    } catch (error) {
        console.error('Login error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi server khi đăng nhập'
            }
        });
    }
});

// ==================== GET CURRENT USER ====================

router.get('/me', authenticate, async (req, res) => {
    try {
        const [users] = await db.query(
            'SELECT user_id, full_name, email, phone, user_role, avatar_url FROM users WHERE user_id = ?',
            [req.user.userId]
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

        const user = users[0];

        res.json({
            success: true,
            data: {
                userId: user.user_id,
                fullName: user.full_name,
                email: user.email,
                phone: user.phone,
                userRole: user.user_role,
                avatarUrl: user.avatar_url
            }
        });

    } catch (error) {
        console.error('Get current user error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi server'
            }
        });
    }
});

module.exports = router;

