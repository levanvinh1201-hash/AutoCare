AutoCare – Ứng dụng quản lý và đặt lịch chăm sóc xe
Giới thiệu
AutoCare là ứng dụng Android hỗ trợ người dùng quản lý thông tin xe, tìm kiếm dịch vụ chăm sóc và sửa chữa xe, đặt lịch sử dụng dịch vụ và theo dõi trạng thái lịch hẹn. Ứng dụng đồng thời cung cấp các chức năng quản lý dành cho quản trị viên và tích hợp chatbot nhằm hỗ trợ người dùng tra cứu thông tin nhanh chóng.
Dự án được phát triển bằng Java trên Android Studio, sử dụng Firebase để lưu trữ và quản lý dữ liệu.
 Chức năng chính
 1. Dành cho khách hàng

Đăng ký và đăng nhập tài khoản.

Quản lý thông tin cá nhân.


Quản lý thông tin phương tiện.

Xem danh sách dịch vụ chăm sóc và sửa chữa xe.

Tra cứu giá dịch vụ.

Đặt lịch sử dụng dịch vụ.

Theo dõi trạng thái lịch hẹn.

Tra cứu thông tin phụ tùng và tình trạng hàng.

Sử dụng chatbot để hỏi đáp về dịch vụ, phụ tùng và lịch hẹn.

Xem lại lịch sử trò chuyện.

 2. Dành cho quản trị viên

Truy cập giao diện quản trị.

Quản lý và theo dõi thông tin dịch vụ.

Tra cứu thông tin phụ tùng.

Theo dõi dữ liệu lịch hẹn.

Xem các thông tin thống kê được hỗ trợ trong ứng dụng.

Sử dụng chatbot để tra cứu thông tin phục vụ quản lý.

3. Chatbot AutoCare
Chatbot được tích hợp trực tiếp trong ứng dụng nhằm hỗ trợ người dùng tra cứu thông tin thuận tiện hơn.
Các chức năng hỗ trợ gồm:
Tra cứu danh sách dịch vụ và giá dịch vụ.

Tra cứu phụ tùng và tình trạng còn hàng.

Tra cứu thông tin lịch hẹn của người dùng.

Hỗ trợ quản trị viên truy vấn một số thông tin thống kê.

Lưu trữ lịch sử trò chuyện trên Firebase Firestore.

 Công nghệ sử dụng
Công nghệ	Vai trò
Java	Ngôn ngữ lập trình Android
Android Studio	Môi trường phát triển ứng dụng
XML	Thiết kế giao diện
Firebase Authentication	Xác thực người dùng, nếu được cấu hình trong dự án
Firebase Cloud Firestore	Lưu trữ và truy vấn dữ liệu
Git & GitHub	Quản lý mã nguồn
 Cấu trúc dự án
AutoCare/
├── app/
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/example/autocare/
│           │       ├── chatbox/
│           │       │   ├── ChatbotActivity.java
│           │       │   └── ChatbotEngine.java
│           │       ├── ...
│           └── res/
│               ├── layout/
│               ├── drawable/
│               └── values/
├── build.gradle
├── settings.gradle
└── README.md
Cấu trúc trên mang tính minh họa; các thư mục và tệp thực tế có thể khác tùy phiên bản dự án.
 Cơ sở dữ liệu
Ứng dụng sử dụng Firebase Cloud Firestore để lưu trữ và truy vấn dữ liệu. Một số collection được sử dụng trong các chức năng đã triển khai gồm:

services: Thông tin dịch vụ và giá dịch vụ.

spare_parts: Thông tin phụ tùng, giá và số lượng tồn.

appointments: Thông tin lịch hẹn, xe, dịch vụ, ngày giờ và trạng thái.

Dữ liệu lịch sử trò chuyện của chatbot.

Tên collection và cấu trúc trường cần được đối chiếu với cấu hình Firestore thực tế của dự án.
 Hướng dẫn cài đặt
Yêu cầu

Android Studio.

JDK tương thích với phiên bản Android Gradle Plugin của dự án.

Tài khoản Firebase và một dự án Firebase đã cấu hình.

Thiết bị Android hoặc trình giả lập để chạy ứng dụng.

Các bước thực hiện
1.Clone repository về máy:

2.git clone <GITHUB_REPOSITORY_URL>

3.Mở dự án bằng Android Studio.

4.Cấu hình Firebase theo dự án, bao gồm tệp google-services.json nếu ứng dụng sử dụng Firebase Android SDK.

5.Đồng bộ Gradle và chờ tải các thư viện cần thiết.

6.Chạy ứng dụng trên thiết bị Android hoặc trình giả lập.

Lưu ý: Không tải lên GitHub các thông tin bí mật như mật khẩu, khóa riêng, thông tin đăng nhập hoặc tệp cấu hình chứa thông tin nhạy cảm. Cần cấu hình quyền truy cập và Firebase Security Rules phù hợp trước khi triển khai thực tế.
Mục tiêu dự án

Xây dựng ứng dụng Android hỗ trợ đặt lịch và quản lý dịch vụ chăm sóc xe.

Tập trung thông tin dịch vụ, phụ tùng và lịch hẹn trên một nền tảng.

Hỗ trợ quản trị viên theo dõi dữ liệu.


Tích hợp chatbot để cải thiện trải nghiệm tra cứu thông tin.

Thực hành phát triển ứng dụng Android và tích hợp cơ sở dữ liệu Firebase.

 Tác giả
Tên tác giả: Lê văn Vinh-Nguyễn Thị Hoàng Hằng
Dự án: AutoCare – Ứng dụng quản lý và đặt lịch chăm sóc xe.
 Nếu bạn thấy dự án hữu ích, hãy để lại một Star cho repository!
