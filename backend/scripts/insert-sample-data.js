/**
 * Script để insert dữ liệu mẫu vào database
 * Usage: node scripts/insert-sample-data.js
 */

require('dotenv').config();
const mysql = require('mysql2/promise');
const bcrypt = require('bcryptjs');
const fs = require('fs');
const path = require('path');

const dbConfig = {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'pawhelp_db',
    multipleStatements: true
};

async function insertSampleData() {
    let connection;

    try {
        console.log('🔄 Đang kết nối database...');
        connection = await mysql.createConnection(dbConfig);
        console.log('✅ Đã kết nối database thành công!\n');

        // Hash password một lần cho tất cả users (password: 123456)
        const passwordHash = await bcrypt.hash('123456', 10);
        console.log(`🔑 Password hash: ${passwordHash}\n`);

        // Đọc file sample_data.sql
        const sampleDataPath = path.join(__dirname, '..', 'database', 'sample_data.sql');
        let sampleSQL = fs.readFileSync(sampleDataPath, 'utf8');

        // Replace password hash placeholder
        sampleSQL = sampleSQL.replace(
            /\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy/g,
            passwordHash
        );

        // Tách các câu lệnh SQL (bỏ qua SELECT statements ở cuối)
        const statements = sampleSQL
            .split(';')
            .map(s => s.trim())
            .filter(s => s.length > 0 && !s.startsWith('SELECT'));

        console.log(`🔄 Đang insert ${statements.length} câu lệnh SQL...\n`);

        // Thực thi từng câu lệnh
        for (let i = 0; i < statements.length; i++) {
            const statement = statements[i];
            if (statement.trim()) {
                try {
                    await connection.query(statement);
                    console.log(`✅ [${i + 1}/${statements.length}] Đã thực thi thành công`);
                } catch (err) {
                    // Bỏ qua lỗi duplicate entry (INSERT IGNORE)
                    if (err.code === 'ER_DUP_ENTRY' || err.message.includes('Duplicate entry')) {
                        console.log(`⚠️  [${i + 1}/${statements.length}] Đã tồn tại (bỏ qua)`);
                    } else {
                        console.error(`❌ [${i + 1}/${statements.length}] Lỗi:`, err.message);
                    }
                }
            }
        }

        // Đếm số lượng records
        console.log('\n📊 Thống kê dữ liệu:');
        const [users] = await connection.query('SELECT COUNT(*) as count FROM users');
        const [team] = await connection.query('SELECT COUNT(*) as count FROM team_members');
        const [posts] = await connection.query('SELECT COUNT(*) as count FROM rescue_posts');
        const [requests] = await connection.query('SELECT COUNT(*) as count FROM rescue_requests');
        const [notifications] = await connection.query('SELECT COUNT(*) as count FROM notifications');

        console.log(`   👤 Users: ${users[0].count}`);
        console.log(`   👥 Team Members: ${team[0].count}`);
        console.log(`   📝 Posts: ${posts[0].count}`);
        console.log(`   📋 Requests: ${requests[0].count}`);
        console.log(`   🔔 Notifications: ${notifications[0].count}`);

        await connection.end();
        console.log('\n✅ Hoàn thành insert dữ liệu mẫu!');
        console.log('\n📌 Thông tin đăng nhập:');
        console.log('   Email: nguyenvana@example.com');
        console.log('   Password: 123456');
        console.log('   (Tất cả users đều dùng password: 123456)');

    } catch (error) {
        console.error('\n❌ Lỗi khi insert dữ liệu:', error.message);
        if (connection) {
            await connection.end();
        }
        process.exit(1);
    }
}

// Chạy script
insertSampleData();

