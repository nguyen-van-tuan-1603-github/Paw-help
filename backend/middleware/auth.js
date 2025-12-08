const { verifyToken } = require('../config/jwt');

// Middleware kiểm tra authentication
function authenticate(req, res, next) {
    try {
        // Lấy token từ header
        const authHeader = req.headers.authorization;
        
        if (!authHeader || !authHeader.startsWith('Bearer ')) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'UNAUTHORIZED',
                    message: 'Token không hợp lệ hoặc không tồn tại'
                }
            });
        }
        
        const token = authHeader.substring(7); // Remove 'Bearer '
        
        // Verify token
        const decoded = verifyToken(token);
        
        if (!decoded) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'INVALID_TOKEN',
                    message: 'Token không hợp lệ hoặc đã hết hạn'
                }
            });
        }
        
        // Attach user info to request
        req.user = decoded;
        next();
        
    } catch (error) {
        return res.status(401).json({
            success: false,
            error: {
                code: 'AUTH_ERROR',
                message: 'Lỗi xác thực'
            }
        });
    }
}

// Middleware kiểm tra role
function authorize(...allowedRoles) {
    return (req, res, next) => {
        if (!req.user) {
            return res.status(401).json({
                success: false,
                error: {
                    code: 'UNAUTHORIZED',
                    message: 'Bạn cần đăng nhập'
                }
            });
        }
        
        if (allowedRoles.length && !allowedRoles.includes(req.user.role)) {
            return res.status(403).json({
                success: false,
                error: {
                    code: 'FORBIDDEN',
                    message: 'Bạn không có quyền truy cập'
                }
            });
        }
        
        next();
    };
}

module.exports = {
    authenticate,
    authorize
};

