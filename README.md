# 🚗 AutoCare – Ứng dụng quản lý và đặt lịch chăm sóc xe

<p align="center">
  <strong>Ứng dụng Android hỗ trợ quản lý xe, đặt lịch dịch vụ và tra cứu thông tin bằng chatbot.</strong>
</p>

---

## 📖 1. Giới thiệu dự án

**AutoCare** là ứng dụng Android được phát triển nhằm hỗ trợ khách hàng quản lý phương tiện, tìm kiếm dịch vụ chăm sóc và sửa chữa xe, đặt lịch sử dụng dịch vụ và theo dõi lịch hẹn.

Ứng dụng cung cấp giao diện quản trị dành cho quản trị viên, đồng thời tích hợp chatbot giúp người dùng tra cứu thông tin dịch vụ, phụ tùng và lịch hẹn thuận tiện hơn.

## ✨ 2. Chức năng chính

### 👤 Khách hàng

- Đăng ký và đăng nhập tài khoản.
- Quản lý thông tin cá nhân và phương tiện.
- Xem danh sách dịch vụ và giá dịch vụ.
- Đặt lịch chăm sóc, sửa chữa xe.
- Theo dõi trạng thái lịch hẹn.
- Tra cứu thông tin phụ tùng và tình trạng hàng.
- Trao đổi với chatbot và xem lịch sử trò chuyện.

### 🛠️ Quản trị viên

- Truy cập giao diện quản trị.
- Quản lý và theo dõi thông tin dịch vụ.
- Tra cứu thông tin phụ tùng.
- Theo dõi thông tin lịch hẹn.
- Xem các thông tin thống kê được hỗ trợ trong ứng dụng.
- Sử dụng chatbot để tra cứu thông tin.

### 💬 Chatbot AutoCare

Chatbot được tích hợp trong ứng dụng để hỗ trợ tra cứu thông tin:

- Thông tin dịch vụ và giá dịch vụ.
- Thông tin phụ tùng và số lượng còn hàng.
- Thông tin lịch hẹn của người dùng.
- Một số thông tin thống kê dành cho quản trị viên.
- Lịch sử trò chuyện được lưu trữ trên Firebase Firestore.

## 🧰 3. Công nghệ sử dụng

| Công nghệ | Mục đích |
|:---|:---|
| Java | Phát triển ứng dụng Android |
| Android Studio | Môi trường lập trình |
| XML | Thiết kế giao diện |
| Firebase Authentication | Xác thực người dùng, nếu được cấu hình |
| Firebase Cloud Firestore | Lưu trữ và truy vấn dữ liệu |
| Git & GitHub | Quản lý mã nguồn |

## 🗂️ 4. Cấu trúc dự án

```text
AutoCare/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/autocare/
│           │       ├── chatbox/
│           │       │   ├── ChatbotActivity.java
│           │       │   └── ChatbotEngine.java
│           │       └── ...
│           └── res/
│               ├── layout/
│               ├── drawable/
│               └── values/
├── build.gradle
├── settings.gradle
└── README.md
```

*Cấu trúc trên mang tính minh họa; các thư mục thực tế có thể khác.*

## 🔥 5. Cơ sở dữ liệu

Ứng dụng sử dụng Firebase Cloud Firestore để lưu trữ và truy vấn dữ liệu.

Các collection được sử dụng trong những chức năng đã triển khai gồm:

| Collection | Nội dung |
|:---|:---|
| `services` | Thông tin dịch vụ và giá dịch vụ |
| `spare_parts` | Thông tin phụ tùng, giá và số lượng |
| `appointments` | Thông tin lịch hẹn, xe, dịch vụ, ngày giờ và trạng thái |
| Lịch sử trò chuyện | Nội dung trao đổi giữa người dùng và chatbot |

## ⚙️ 6. Hướng dẫn cài đặt

### Yêu cầu

- Android Studio.
- JDK tương thích với cấu hình Gradle của dự án.
- Dự án Firebase đã được cấu hình.
- Thiết bị Android hoặc trình giả lập.

### Các bước cài đặt

**Bước 1:** Clone repository:

```bash
git clone <GITHUB_REPOSITORY_URL>
```

**Bước 2:** Mở dự án bằng Android Studio.

**Bước 3:** Cấu hình Firebase theo thiết lập của dự án, bao gồm `google-services.json` nếu cần.

**Bước 4:** Đồng bộ Gradle và chờ các thư viện tải xuống.

**Bước 5:** Chạy ứng dụng trên thiết bị Android hoặc trình giả lập.

> **Lưu ý bảo mật:** Không đưa mật khẩu, khóa riêng hoặc thông tin bí mật lên GitHub. Hãy cấu hình Firebase Security Rules phù hợp trước khi triển khai thực tế.

## 🎯 7. Mục tiêu dự án

- Xây dựng ứng dụng Android hỗ trợ quản lý xe và đặt lịch dịch vụ.
- Tập trung thông tin dịch vụ, phụ tùng và lịch hẹn.
- Hỗ trợ quản trị viên theo dõi dữ liệu.
- Tích hợp chatbot giúp tra cứu thông tin thuận tiện.
- Áp dụng kiến thức phát triển Android và kết nối cơ sở dữ liệu Firebase.

## 👨‍💻 8. Tác giả

- **Tên dự án:** AutoCare
- **Lĩnh vực:** Ứng dụng Android quản lý và đặt lịch chăm sóc xe
- **Tác giả:** *[Lê Văn Vinh]*

---

<p align="center">
  <em>AutoCare – Chăm sóc xe dễ dàng, quản lý thuận tiện.</em>
</p>
