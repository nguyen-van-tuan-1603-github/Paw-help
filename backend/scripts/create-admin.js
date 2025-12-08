#!/usr/bin/env node

/**
 * Script to create admin user for PawHelp
 * Usage: node scripts/create-admin.js
 */

const readline = require('readline');
const bcrypt = require('bcryptjs');
const mysql = require('mysql2/promise');
require('dotenv').config();

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

function question(query) {
    return new Promise(resolve => rl.question(query, resolve));
}

async function createAdmin() {
    console.log('\n🐾 ===== PawHelp - Create Admin User ===== 🐾\n');
    
    try {
        // Get user input
        const fullName = await question('👤 Tên đầy đủ: ');
        const email = await question('📧 Email: ');
        const phone = await question('📱 Số điện thoại: ');
        const password = await question('🔒 Mật khẩu: ');
        
        if (!fullName || !email || !phone || !password) {
            throw new Error('Vui lòng điền đầy đủ thông tin');
        }
        
        // Validate email
        if (!email.match(/^[^\s@]+@[^\s@]+\.[^\s@]+$/)) {
            throw new Error('Email không hợp lệ');
        }
        
        // Validate password
        if (password.length < 6) {
            throw new Error('Mật khẩu phải có ít nhất 6 ký tự');
        }
        
        console.log('\n⏳ Đang xử lý...\n');
        
        // Connect to database
        const connection = await mysql.createConnection({
            host: process.env.DB_HOST || 'localhost',
            port: process.env.DB_PORT || 3306,
            user: process.env.DB_USER || 'root',
            password: process.env.DB_PASSWORD || '',
            database: process.env.DB_NAME || 'pawhelp_db'
        });
        
        console.log('✅ Kết nối database thành công');
        
        // Check if email exists
        const [existing] = await connection.query(
            'SELECT email FROM users WHERE email = ?',
            [email]
        );
        
        if (existing.length > 0) {
            throw new Error('Email đã tồn tại trong hệ thống');
        }
        
        // Hash password
        const passwordHash = await bcrypt.hash(password, 10);
        console.log('✅ Hash password thành công');
        
        // Insert admin user
        const [result] = await connection.query(
            `INSERT INTO users (full_name, email, phone, password_hash, user_role, created_at) 
             VALUES (?, ?, ?, ?, 'admin', NOW())`,
            [fullName, email, phone, passwordHash]
        );
        
        console.log('✅ Tạo tài khoản admin thành công');
        console.log(`\n📊 Thông tin tài khoản:\n`);
        console.log(`   ID: ${result.insertId}`);
        console.log(`   Tên: ${fullName}`);
        console.log(`   Email: ${email}`);
        console.log(`   Số điện thoại: ${phone}`);
        console.log(`   Vai trò: Admin`);
        console.log('\n🎉 Hoàn thành! Bạn có thể đăng nhập vào Admin Panel ngay bây giờ.\n');
        
        await connection.end();
        rl.close();
        
    } catch (error) {
        console.error('\n❌ Lỗi:', error.message);
        console.error('\n💡 Giải pháp:');
        
        if (error.message.includes('connect')) {
            console.error('   - Kiểm tra MySQL đã chạy chưa');
            console.error('   - Kiểm tra thông tin trong file .env');
        } else if (error.message.includes('Email đã tồn tại')) {
            console.error('   - Sử dụng email khác');
            console.error('   - Hoặc update role: UPDATE users SET user_role = "admin" WHERE email = "' + email + '"');
        }
        
        rl.close();
        process.exit(1);
    }
}

// Run
createAdmin();

