/**
 * Script tự động tạo database và các bảng từ schema.sql
 * Usage: node scripts/init-database.js
 */

require('dotenv').config();
const mysql = require('mysql2/promise');
const fs = require('fs');
const path = require('path');

const config = {
    host: process.env.DB_HOST || 'localhost',
    port: parseInt(process.env.DB_PORT) || 3306,
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
};

async function initDatabase() {
    let connection;

    try {
        // Kết nối với multipleStatements enabled
        connection = await mysql.createConnection({
            ...config,
            multipleStatements: true
        });

        // Đọc file schema.sql
        const schemaPath = path.join(__dirname, '..', 'database', 'schema.sql');

        if (!fs.existsSync(schemaPath)) {
            throw new Error(`File schema.sql không tồn tại tại: ${schemaPath}`);
        }

        let schemaSQL = fs.readFileSync(schemaPath, 'utf8');

        // Loại bỏ DELIMITER commands và procedures (sẽ bỏ qua stored procedures)
        schemaSQL = schemaSQL.replace(/DELIMITER\s+\/\//gi, '');
        schemaSQL = schemaSQL.replace(/DELIMITER\s+;/gi, '');

        // Tách SQL thành các phần: trước và sau DELIMITER
        const mainSQL = schemaSQL.split(/CREATE\s+(PROCEDURE|FUNCTION)/i)[0];

        // Thực thi toàn bộ SQL (trước phần procedures)
        await connection.query(mainSQL);

        // Kiểm tra các bảng đã tạo
        const [tables] = await connection.query(
            "SELECT TABLE_NAME, TABLE_ROWS FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'pawhelp_db' ORDER BY TABLE_NAME"
        );

        await connection.end();

        console.log('✅ Database created successfully!');
        if (tables.length > 0) {
            console.table(tables);
        }

    } catch (error) {
        console.error('❌ Error:', error.message);
        if (connection) {
            await connection.end();
        }
        process.exit(1);
    }
}

// Chạy script
initDatabase();

