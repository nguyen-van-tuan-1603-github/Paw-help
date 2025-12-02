# 🐾 PawHelp - Ứng Dụng Cứu Hộ Động Vật

## Giới Thiệu
**PawHelp** là ứng dụng di động kết nối cộng đồng yêu thương động vật tại Đà Nẵng, giúp phát hiện và cứu hộ động vật gặp nạn một cách nhanh chóng và hiệu quả.

**Slogan**: *"Yêu Thương Và Hành Động"*

---

## 🎨 Tính Năng Chính

### 1. 🏠 Trang Chủ (MainActivity)
- Hiển thị thống kê tổng quan:
  - SOS: Số lượng động vật cần cứu khẩn cấp
  - Đã cứu: Số lượng động vật đã được giúp đỡ
  - Tổng số: Tổng số bài đăng cứu hộ
- Danh sách tin cứu hộ gần đây
- Truy cập nhanh tới các chức năng quan trọng

### 2. 🚨 Cứu Hộ Khẩn Cấp (TrangCuuHoActivity)
- **Hotline cứu hộ 24/7**: Gọi ngay số điện thoại khẩn cấp
- **Lấy vị trí GPS**: Xác định vị trí hiện tại để báo cáo chính xác
- **Báo cáo sự cố**: Tạo bài đăng cứu hộ mới ngay lập tức
- **Hướng dẫn cứu hộ**: Các bước an toàn khi tiếp cận động vật

### 3. 📝 Đăng Bài Cứu Hộ (TrangDangBaiActivity)
- Tải ảnh từ camera hoặc thư viện
- Nhập thông tin chi tiết về động vật
- Chọn loại động vật: 🐕 Chó, 🐈 Mèo, 🐦 Chim, 🐰 Thỏ
- Thêm vị trí và thông tin liên hệ
- Mô tả tình trạng động vật

### 4. 📄 Chi Tiết Bài Đăng (PostDetailActivity)
- Xem ảnh và mô tả chi tiết
- Hiển thị vị trí trên bản đồ
- **Gọi điện** trực tiếp cho người đăng
- **Đăng ký tình nguyện** giúp cứu hộ
- **Báo cáo** bài đăng không phù hợp
- **Chia sẻ** bài đăng lên mạng xã hội

### 5. 👤 Quản Lý Tài Khoản
#### UserProfileActivity
- Hiển thị thông tin cá nhân
- Avatar người dùng
- Email, số điện thoại, địa chỉ
- Vai trò: Người dùng / Tình nguyện viên / Admin
- Nút chỉnh sửa và đăng xuất

#### EditProfileActivity
- Cập nhật ảnh đại diện
- Chỉnh sửa tên đầy đủ
- Thay đổi giới tính
- Cập nhật số điện thoại
- Thay đổi địa chỉ
- Lưu thông tin

### 6. 🔔 Thông Báo (NotificationsActivity)
- Danh sách thông báo theo thời gian
- Phân loại: Cứu hộ, Bình luận, Tình nguyện, Hệ thống
- Trạng thái đã đọc/chưa đọc
- Đánh dấu tất cả là đã đọc
- Nhấn vào thông báo để xem chi tiết

### 7. 📊 Bảng Cứu Hộ (RescueDashboardActivity)
- Thống kê chi tiết:
  - Số bài đăng mới nhận
  - Số bài đang xử lý
- Danh sách cần xử lý ưu tiên
- Nút đăng bài nhanh (FAB)

### 8. 📜 Lịch Sử Cứu Hộ (TrangXemLichSuCuuHoActivity)
- Xem lịch sử các ca cứu hộ đã tham gia
- Thông tin chi tiết từng ca
- Hình ảnh và vị trí
- Ngày giờ cứu hộ

### 9. 👥 Đội Ngũ (TrangDoiNguActivity)
- Danh sách thành viên đội ngũ cứu hộ
- Thông tin liên hệ từng thành viên
- Vai trò và chức vụ
- Nút gọi điện và email nhanh

### 10. ℹ️ Về Chúng Tôi (TrangVeChungToiActivity)
- Thông tin về Hội Cứu Trợ Động Vật Đà Nẵng
- Sứ mệnh và tầm nhìn
- Thành tích đạt được
- Thông tin liên hệ
- Liên kết mạng xã hội
- Thông tin ủng hộ

### 11. 🔐 Xác Thực
#### LoginActivity
- Đăng nhập bằng email/password
- Ghi nhớ tài khoản
- Quên mật khẩu
- Chuyển sang đăng ký

#### RegisterActivity
- Đăng ký tài khoản mới
- Nhập thông tin đầy đủ
- Đồng ý điều khoản
- Xác thực email

#### ForgotPasswordActivity
- Nhập email để khôi phục
- Gửi link đặt lại mật khẩu
- Hướng dẫn chi tiết

---

## 🎨 Thiết Kế Giao Diện

### Màu Sắc Chính
- **Primary**: `#4DB6AC` (Xanh ngọc - Teal)
- **Secondary**: `#E91E63` (Hồng - Pink)
- **Accent**: `#2196F3` (Xanh dương - Blue)
- **Success**: `#4CAF50` (Xanh lá)
- **Warning**: `#FF9800` (Cam)
- **Error**: `#F44336` (Đỏ)

### Phong Cách Thiết Kế
- **Material Design 3**
- **Gradient backgrounds** cho các màn hình chính
- **Card-based layout** cho danh sách
- **Rounded corners** (12dp, 16dp, 24dp)
- **Soft shadows** và elevation
- **Icon line art** với màu sắc thương hiệu
- **Typography** rõ ràng, dễ đọc

### Components
- **Buttons**: Gradient, Outlined, Text
- **Cards**: Elevated, Flat với bo góc mềm mại
- **Badges**: Status indicators với màu sắc phân loại
- **Icons**: Material icons + custom paw logo
- **Input Fields**: Outline style với icon

---

## 📱 Màn Hình Chi Tiết

### 🎬 SplashActivity
- Logo PawHelp với hiệu ứng
- Slogan "Yêu Thương Và Hành Động"
- Loading indicator
- Chuyển tự động sang WelcomeActivity

### 🎉 WelcomeActivity
- Hình ảnh hero động vật
- Thông điệp chào mừng
- Nút "Đăng nhập"
- Nút "Bắt đầu" cho người dùng mới

---

## 🔧 Công Nghệ Sử Dụng

### Framework & Language
- **Android SDK**: API 24+ (Android 7.0+)
- **Language**: Java
- **Build System**: Gradle

### Libraries
- **Material Components**: UI components hiện đại
- **CardView**: Hiển thị cards
- **RecyclerView**: Danh sách động
- **ConstraintLayout**: Responsive layouts
- **Google Location Services**: GPS & Maps

### Architecture
- **Single Activity** với nhiều fragments
- **Adapter Pattern** cho RecyclerView
- **MVC Pattern** đơn giản

---

## 📁 Cấu Trúc Dự Án

```
Paw-help/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/paw_help/
│   │   │   │   ├── MainActivity.java
│   │   │   │   ├── TrangCuuHoActivity.java
│   │   │   │   ├── TrangDangBaiActivity.java
│   │   │   │   ├── PostDetailActivity.java
│   │   │   │   ├── UserProfileActivity.java
│   │   │   │   ├── EditProfileActivity.java
│   │   │   │   ├── NotificationsActivity.java
│   │   │   │   ├── RescueDashboardActivity.java
│   │   │   │   ├── TrangXemLichSuCuuHoActivity.java
│   │   │   │   ├── TrangDoiNguActivity.java
│   │   │   │   ├── TrangVeChungToiActivity.java
│   │   │   │   ├── LoginActivity.java
│   │   │   │   ├── RegisterActivity.java
│   │   │   │   ├── ForgotPasswordActivity.java
│   │   │   │   ├── SplashActivity.java
│   │   │   │   ├── WelcomeActivity.java
│   │   │   │   ├── Models/
│   │   │   │   │   ├── RescuePost.java
│   │   │   │   │   ├── RescueHistory.java
│   │   │   │   │   ├── Notification.java
│   │   │   │   │   └── TeamMember.java
│   │   │   │   └── Adapters/
│   │   │   │       ├── RescuePostAdapter.java
│   │   │   │       ├── RescueHistoryAdapter.java
│   │   │   │       ├── NotificationAdapter.java
│   │   │   │       └── TeamAdapter.java
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_*.xml
│   │   │   │   │   └── item_*.xml
│   │   │   │   ├── drawable/
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   ├── dimens.xml
│   │   │   │   │   ├── styles.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   └── mipmap/
│   │   │   └── AndroidManifest.xml
│   │   └── build.gradle.kts
│   └── build.gradle.kts
└── README.md
```

---

## 🚀 Hướng Dẫn Cài Đặt

### Yêu Cầu
- Android Studio Arctic Fox hoặc mới hơn
- JDK 11+
- Android SDK API 24+
- Gradle 7.0+

### Các Bước

1. **Clone dự án**
```bash
git clone https://github.com/your-repo/paw-help.git
cd paw-help
```

2. **Mở trong Android Studio**
- File → Open → Chọn thư mục `Paw-help`
- Đợi Gradle sync hoàn tất

3. **Cấu hình**
- Tạo file `local.properties` nếu chưa có
- Thêm đường dẫn Android SDK

4. **Build & Run**
- Kết nối thiết bị Android hoặc tạo AVD
- Click "Run" hoặc nhấn `Shift + F10`

---

## 🔮 Tính Năng Sắp Tới

### Backend Integration
- [ ] Kết nối API với backend ASP.NET Core
- [ ] JWT Authentication
- [ ] Real-time notifications
- [ ] Upload ảnh lên server

### Social Features
- [ ] Chat giữa người dùng
- [ ] Chia sẻ lên Facebook, Instagram
- [ ] Bình luận và like bài đăng
- [ ] Follow người dùng khác

### Advanced Features
- [ ] Bản đồ hiển thị vị trí động vật
- [ ] Tìm kiếm và lọc bài đăng
- [ ] Push notifications
- [ ] Dark mode
- [ ] Multi-language support
- [ ] Offline mode

---

## 🤝 Đóng Góp

Chúng tôi luôn chào đón mọi đóng góp! Nếu bạn muốn:
1. Fork dự án
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Mở Pull Request

---

## 📝 License
Dự án này được phát triển cho mục đích giáo dục và phi lợi nhuận.

---

## 📧 Liên Hệ

**Hội Cứu Trợ Động Vật Đà Nẵng**
- 📍 Địa chỉ: Đà Nẵng, Việt Nam
- 📧 Email: contact@pawhelp.vn
- 📱 Hotline: 113
- 🌐 Website: www.pawhelp.vn
- 📘 Facebook: /PawHelpDanang

---

## 🙏 Cảm Ơn

Cảm ơn bạn đã quan tâm đến **PawHelp**! Mỗi động vật được cứu là một mảnh ghép nhỏ trong thế giới tốt đẹp hơn. 🐾

**"Yêu thương và hành động - cùng nhau tạo nên sự khác biệt!"**

