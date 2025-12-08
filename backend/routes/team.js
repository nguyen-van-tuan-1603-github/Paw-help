const express = require('express');
const router = express.Router();
const db = require('../config/database');

// ==================== GET TEAM MEMBERS ====================

router.get('/', async (req, res) => {
    try {
        const [members] = await db.query(`
            SELECT 
                member_id as memberId,
                name,
                position,
                description,
                avatar_url as avatarUrl,
                display_order as displayOrder
            FROM team_members
            ORDER BY display_order ASC
        `);

        res.json({
            success: true,
            data: members
        });

    } catch (error) {
        console.error('Get team members error:', error);
        res.status(500).json({
            success: false,
            error: {
                code: 'SERVER_ERROR',
                message: 'Lỗi khi lấy thông tin đội ngũ'
            }
        });
    }
});

module.exports = router;

