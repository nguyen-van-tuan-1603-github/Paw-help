require('dotenv').config();
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'pawhelp_db',
    multipleStatements: true
};

async function createAdmin() {
    let connection;

    try {
        connection = await mysql.createConnection(dbConfig);
        console.log('✅ Đã kết nối database thành công!');

        const adminEmail = 'admin@pawhelp.com';
        const adminPassword = '123456';

        // Check if admin exists
        const [existingAdmins] = await connection.query(
            'SELECT user_id, email FROM users WHERE email = ?',
            [adminEmail]
        );

        if (existingAdmins.length > 0) {
            console.log('⚠️  Admin đã tồn tại. Đang cập nhật password...');
            
            // Update password
            const hashedPassword = await bcrypt.hash(adminPassword, 10);
            await connection.query(
                'UPDATE users SET password_hash = ?, user_role = ? WHERE email = ?',
                [hashedPassword, 'admin', adminEmail]
            );
            
            console.log('✅ Đã cập nhật password cho admin!');
        } else {
            console.log('📝 Đang tạo tài khoản admin...');
            
            // Create admin
            const hashedPassword = await bcrypt.hash(adminPassword, 10);
            await connection.query(
                `INSERT INTO users (full_name, email, phone, password_hash, user_role, created_at) 
                 VALUES (?, ?, ?, ?, 'admin', NOW())`,
                ['Admin PawHelp', adminEmail, '0900000000', hashedPassword]
            );
            
            console.log('✅ Đã tạo tài khoản admin thành công!');
        }

        // Verify admin account
        const [admins] = await connection.query(
            'SELECT user_id, full_name, email, user_role FROM users WHERE email = ?',
            [adminEmail]
        );

        if (admins.length > 0) {
            const admin = admins[0];
            console.log('\n📌 Thông tin đăng nhập Admin:');
            console.log(`   Email: ${admin.email}`);
            console.log(`   Password: ${adminPassword}`);
            console.log(`   Role: ${admin.user_role}`);
            console.log(`   User ID: ${admin.user_id}`);
        }

    } catch (error) {
        console.error('❌ Lỗi khi tạo admin:', error.message);
        if (error.code === 'ER_BAD_DB_ERROR') {
            console.error('   ⚠️  Database chưa được tạo. Hãy chạy: npm run init-db');
        } else if (error.code === 'ER_ACCESS_DENIED_ERROR') {
            console.error('   ⚠️  Lỗi kết nối database. Kiểm tra lại DB_PASSWORD trong .env');
        }
        process.exit(1);
    } finally {
        if (connection) {
            await connection.end();
        }
    }
}

createAdmin();
