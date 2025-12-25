const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const path = require('path');
require('dotenv').config();

const app = express();
const PORT = process.env.PORT || 5125;
const HOST = process.env.HOST || '0.0.0.0'; // Listen on all network interfaces

// ==================== MIDDLEWARE ====================

// Security headers
app.use(helmet());

// CORS
app.use(cors({
    origin: process.env.ALLOWED_ORIGINS || '*',
    credentials: true
}));

// Body parser
app.use(express.json());
app.use(express.urlencoded({ extended: true }));

// Logging
app.use(morgan('dev'));

// Static files (uploads)
app.use('/uploads', express.static(path.join(__dirname, 'uploads')));

// ==================== ROUTES ====================

// Health check
app.get('/', (req, res) => {
    res.json({
        success: true,
        message: '🐾 PawHelp API Server',
        version: '1.0.0',
        timestamp: new Date().toISOString()
    });
});

// API Routes
app.use('/api/auth', require('./routes/auth'));
app.use('/api/users', require('./routes/users'));
app.use('/api/posts', require('./routes/posts'));
app.use('/api/dashboard', require('./routes/dashboard'));
app.use('/api/notifications', require('./routes/notifications'));
app.use('/api/team', require('./routes/team'));

// ==================== ERROR HANDLING ====================

// 404 Handler
app.use((req, res) => {
    res.status(404).json({
        success: false,
        error: {
            code: 'NOT_FOUND',
            message: 'Endpoint không tồn tại'
        }
    });
});

// Global Error Handler
app.use((err, req, res, next) => {
    console.error('Error:', err);
    
    res.status(err.status || 500).json({
        success: false,
        error: {
            code: err.code || 'INTERNAL_ERROR',
            message: err.message || 'Lỗi server',
            ...(process.env.NODE_ENV === 'development' && { stack: err.stack })
        }
    });
});

// ==================== START SERVER ====================

app.listen(PORT, HOST, () => {
    console.log(`
╔══════════════════════════════════════════════════════════╗
║                                                          ║
║          🐾 PAW HELP API SERVER                         ║
║                                                          ║
║  Status:  ✅ Running                                     ║
║  Host:    ${HOST}                                        ║
║  Port:    ${PORT}                                           ║
║  Mode:    ${process.env.NODE_ENV || 'development'}                               ║
║  Local:   http://localhost:${PORT}                        ║
║  Network: http://${HOST === '0.0.0.0' ? 'YOUR_IP' : HOST}:${PORT}                        ║
║                                                          ║
╚══════════════════════════════════════════════════════════╝
    `);
    if (HOST === '0.0.0.0') {
        console.log(`\n⚠️  Để kết nối từ thiết bị Android, dùng IP của máy tính trong mạng WiFi.`);
        console.log(`   Ví dụ: http://192.168.1.16:${PORT}\n`);
    }
});

// Graceful shutdown
process.on('SIGTERM', () => {
    console.log('👋 SIGTERM received. Shutting down gracefully...');
    process.exit(0);
});

