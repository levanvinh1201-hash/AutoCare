# AutoCare – Ứng dụng đặt lịch bảo dưỡng và sửa chữa ô tô

AutoCare là ứng dụng Android hỗ trợ người dùng quản lý thông tin xe, tìm hiểu dịch vụ và đặt lịch bảo dưỡng/sửa chữa tại gara. Dự án được xây dựng phục vụ mục đích học tập và thực hành phát triển ứng dụng di động.

## 1. Giới thiệu

AutoCare hướng đến việc giúp khách hàng chủ động hơn trong quá trình chăm sóc xe:

- Quản lý thông tin phương tiện cá nhân.
- Xem danh sách dịch vụ của gara.
- Đặt lịch bảo dưỡng hoặc sửa chữa theo ngày, giờ.
- Theo dõi lịch sử và trạng thái lịch đặt.
- Hủy lịch khi lịch còn ở trạng thái chờ xác nhận.
- Trao đổi với chatbot AI để nhận hỗ trợ và thông tin liên quan đến dịch vụ chăm sóc xe.

Dự án gồm ứng dụng Android dành cho khách hàng và phần quản trị web dùng để quản lý dữ liệu, lịch hẹn và dịch vụ. Hai phần sử dụng chung hệ thống Firebase.

## 2. Công nghệ sử dụng

| Công nghệ | Vai trò |
|---|---|
| Java | Ngôn ngữ lập trình ứng dụng Android |
| XML | Thiết kế giao diện Android |
| Android Studio | Môi trường phát triển ứng dụng Android |
| Firebase Authentication | Đăng ký, đăng nhập và xác thực người dùng |
| Cloud Firestore | Lưu trữ thông tin người dùng, xe, dịch vụ và lịch đặt |
| HTML, CSS, JavaScript | Xây dựng giao diện quản trị web |
| Firebase JavaScript SDK | Kết nối trang quản trị với Firebase |
| Cloudflare Workers | Tạo lớp máy chủ trung gian tiếp nhận yêu cầu chatbot |
| Groq API | Cung cấp mô hình AI để tạo phản hồi cho chatbot |

## 3. Các chức năng chính

### Ứng dụng Android
- Đăng ký và đăng nhập tài khoản.
- Quản lý thông tin cá nhân.
- Thêm và quản lý phương tiện.
- Xem danh sách dịch vụ, giá tiền và thời gian thực hiện.
- Đặt lịch sửa chữa/bảo dưỡng, chọn xe, dịch vụ, ngày, giờ và ghi chú.
- Xem lịch sử đặt lịch.
- Theo dõi trạng thái lịch hẹn.
- Hủy lịch khi lịch đang chờ xác nhận.
- Sử dụng chatbot AI để đặt câu hỏi và nhận hỗ trợ.

### Trang quản trị
- Quản lý tài khoản khách hàng.
- Quản lý danh sách dịch vụ.
- Theo dõi và cập nhật trạng thái lịch hẹn.
- Tra cứu lịch đặt theo ngày hoặc theo khách hàng.
- Theo dõi tình hình hoạt động và thống kê dữ liệu.

> Các chức năng thực tế có thể thay đổi theo phiên bản hiện tại của mã nguồn.

## 4. Kiến trúc tổng quan

```text
ỨNG DỤNG ANDROID (Java/XML)          TRANG QUẢN TRỊ (HTML/CSS/JS)
              |                                  |
              +---------------+------------------+
                              |
                       FIREBASE
                 Authentication / Firestore
                              |
                    CLOUDFLARE WORKER
                     (Chatbot backend)
                              |
                          GROQ API
                       (Mô hình AI)

- Ứng dụng Android và trang quản trị kết nối Firebase để xác thực và truy xuất dữ liệu.
- Khi người dùng gửi câu hỏi trong chatbot, ứng dụng gửi nội dung đến Cloudflare Worker.
- Worker tiếp nhận yêu cầu, sử dụng khóa Groq API được cấu hình ở phía máy chủ để gọi dịch vụ AI.
- Kết quả từ Groq được Worker trả về ứng dụng Android để hiển thị cho người dùng.

## 5. Bảo mật API Key

**Không đưa API Key thật vào mã nguồn Android, GitHub hoặc các tệp cấu hình công khai.**

Trong dự án, khóa Groq API nên được lưu dưới dạng Secret ở Cloudflare Workers, ví dụ:

```text
GROQ_API_KEY = <khóa API bí mật>
```

Tên biến `GROQ_API_KEY` là tên biến môi trường; giá trị thực tế phải được nhập trực tiếp trong trang quản trị Cloudflare.

Ứng dụng Android chỉ gọi đến địa chỉ Worker. Worker mới là thành phần sử dụng khóa API để gửi yêu cầu đến Groq.

Nếu cần chia sẻ dự án trên GitHub:

- Không commit API Key, Firebase service account hoặc thông tin bí mật.
- Không đăng tải tệp `google-services.json` lên repository công khai nếu chưa đánh giá và cấu hình bảo mật phù hợp.
- Kiểm tra `.gitignore` trước khi push.
- Nếu khóa đã bị đưa lên GitHub, hãy thu hồi/đổi khóa ngay; xóa khóa khỏi commit mới không làm mất khóa khỏi lịch sử Git.

## 6. Cấu hình và chạy dự án

### Ứng dụng Android

1. Cài đặt Android Studio.
2. Clone hoặc tải repository về máy.
3. Mở thư mục dự án Android bằng Android Studio.
4. Cấu hình Firebase project của bạn.
5. Đặt tệp cấu hình Firebase Android đúng vị trí theo hướng dẫn của Firebase.
6. Kiểm tra cấu hình Firebase Authentication và Cloud Firestore.
7. Kiểm tra địa chỉ Cloudflare Worker được ứng dụng sử dụng.
8. Đồng bộ Gradle, chọn thiết bị Android và chạy ứng dụng.

### Chatbot AI

1. Tạo hoặc sử dụng Cloudflare Worker làm backend cho chatbot.
2. Cấu hình Secret có tên `GROQ_API_KEY` trong phần **Workers & Pages → Worker → Settings → Variables and Secrets**.
3. Nhập khóa Groq API vào Secret; không ghi giá trị khóa trực tiếp trong mã nguồn.
4. Triển khai Worker và cấu hình ứng dụng Android gọi đúng địa chỉ Worker.
5. Kiểm tra việc xác thực yêu cầu, giới hạn tần suất và xử lý lỗi trước khi đưa vào sử dụng thực tế.

### Trang quản trị web

1. Mở thư mục chứa mã nguồn trang quản trị.
2. Cấu hình Firebase JavaScript SDK với Firebase project tương ứng.
3. Bật các dịch vụ Firebase cần thiết.
4. Chạy trang web bằng máy chủ phát triển hoặc môi trường hosting phù hợp.
5. Kiểm tra quyền truy cập và Firebase Security Rules trước khi triển khai.

> Repository cần có mã nguồn Worker và trang quản trị tương ứng nếu muốn người khác triển khai đầy đủ toàn bộ hệ thống. README này không chứa các thông tin bí mật hoặc cấu hình riêng của tài khoản.

## 7. Cấu trúc dữ liệu tham khảo

Một số collection được sử dụng trong Cloud Firestore:

| Collection | Nội dung |
|---|---|
| `users` | Thông tin tài khoản/người dùng |
| `vehicles` | Thông tin phương tiện |
| `services` | Danh sách dịch vụ |
| `appointments` | Thông tin lịch đặt |

Tên trường và cấu trúc document cần được đối chiếu với phiên bản Firestore thực tế của dự án.

## 8. Trạng thái lịch hẹn

Các trạng thái nghiệp vụ có thể bao gồm:

- `PENDING` – Chờ xác nhận.
- `CONFIRMED` – Đã xác nhận.
- `IN_PROGRESS` – Đang sửa chữa.
- `COMPLETED` – Hoàn thành.
- `CANCELLED` – Đã hủy.
- `NO_SHOW` – Khách không đến.

Quyền cập nhật trạng thái cần được kiểm soát ở phía quản trị và/hoặc backend phù hợp.

## 9. Định hướng phát triển

- Bổ sung thông báo đẩy để nhắc khách hàng trước ngày hẹn.
- Hoàn thiện chatbot AI và mở rộng phạm vi câu hỏi hỗ trợ.
- Bổ sung báo cáo, thống kê hoạt động gara.
- Tăng cường phân quyền, kiểm tra dữ liệu đầu vào và bảo mật hệ thống.
- Cải thiện giao diện và trải nghiệm người dùng.

## 10. Tác giả và mục đích

AutoCare là dự án học tập về phát triển ứng dụng Android, xây dựng giao diện quản trị web, tích hợp Firebase và kết nối dịch vụ AI thông qua API.
.
