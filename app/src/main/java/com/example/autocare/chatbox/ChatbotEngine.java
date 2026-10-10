package com.example.autocare.chatbox;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ChatbotEngine {

    public interface Callback {
        void onAnswer(String answer);
    }

    private final FirebaseFirestore db;
    private final String uid;
    private final boolean isAdmin;

    public ChatbotEngine(String uid, boolean isAdmin) {
        this.db = FirebaseFirestore.getInstance();
        this.uid = uid;
        this.isAdmin = isAdmin;
    }

    // Chuẩn hóa văn bản để nhận diện tiếng Việt có dấu/không dấu.
    private String normalize(String text) {
        if (text == null) return "";

        String result = text.toLowerCase(Locale.ROOT).trim();
        result = Normalizer.normalize(result, Normalizer.Form.NFD);
        result = Pattern.compile("\\p{M}+").matcher(result).replaceAll("");
        return result.replace('đ', 'd');
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) return true;
        }
        return false;
    }

    public void answer(String question, Callback callback) {
        if (callback == null) return;

        if (question == null || question.trim().isEmpty()) {
            callback.onAnswer("Bạn hãy nhập câu hỏi nhé.");
            return;
        }

        String q = normalize(question);

        if (containsAny(q, "xin chao", "chao ban", "hello",
                "chao autocare", "ban oi", "hi")) {
            callback.onAnswer(
                    "Xin chào! Mình là trợ lý AutoCare.\n"
                            + "Mình có thể hỗ trợ tra cứu dịch vụ, phụ tùng và lịch đặt."
                            + (isAdmin
                            ? "\nAdmin có thể hỏi về doanh thu, lịch chờ xác nhận, "
                              + "xe đã hoàn thành và thống kê tổng quan."
                            : "")
            );
            return;
        }

        if (containsAny(q, "cam on", "thanks", "thank you")) {
            callback.onAnswer("Rất vui được hỗ trợ bạn. Bạn cứ nhắn nếu cần tra cứu thêm nhé!");
            return;
        }

        // Các ý định thống kê chỉ dành cho admin, kiểm tra trước từ khóa chung.
        if (isAdmin) {
            if (containsAny(q, "thong ke tong quan", "bao cao tong quan",
                    "tong hop so lieu", "thong ke tat ca")) {
                traCuuThongKeAdmin(callback, "all");
                return;
            }

            if (containsAny(q, "doanh thu", "tong doanh thu")) {
                traCuuThongKeAdmin(callback, "revenue");
                return;
            }

            if (containsAny(q, "chua xac nhan", "cho xac nhan",
                    "chua duyet", "cho duyet", "pending")) {
                traCuuThongKeAdmin(callback, "pending");
                return;
            }

            if (containsAny(q, "hoan thanh", "da sua xong",
                    "da hoan tat", "xe xong")) {
                if (containsAny(q, "xe", "bao nhieu xe")) {
                    traCuuThongKeAdmin(callback, "completed_vehicles");
                } else {
                    traCuuThongKeAdmin(callback, "completed_appointments");
                }
                return;
            }

            if (containsAny(q, "tong lich", "tong so lich",
                    "bao nhieu lich", "so lich", "tat ca lich",
                    "lich dat", "lich hen")) {
                // Nếu người dùng hỏi số lượng/tổng lịch thì trả thống kê admin.
                if (containsAny(q, "bao nhieu", "tong", "so", "tat ca",
                        "co may", "thong ke")) {
                    traCuuThongKeAdmin(callback, "total");
                    return;
                }
            }

            if (containsAny(q, "dich vu da hoan thanh",
                    "luot dung dich vu", "bao nhieu loai dich vu")) {
                traCuuThongKeAdmin(callback, "completed_services");
                return;
            }
        }

        if (containsAny(q, "dich vu", "thay dau", "bao duong",
                "sua chua", "gia tien dich vu")) {
            traCuuDichVu(callback);
            return;
        }

        if (containsAny(q, "phu tung", "linh kien", "con hang",
                "ton kho", "so luong hang")) {
            traCuuPhuTung(callback);
            return;
        }

        if (containsAny(q, "lich dat", "dat lich", "lich hen")) {
            if (isAdmin && containsAny(q, "bao nhieu", "tong",
                    "tat ca", "so luong", "thong ke")) {
                traCuuThongKeAdmin(callback, "total");
            } else {
                traCuuLichCuaToi(callback);
            }
            return;
        }

        String goiY = "Mình chưa hiểu rõ câu hỏi này. Bạn thử hỏi:\n"
                + "• Gara có dịch vụ nào?\n"
                + "• Phụ tùng nào còn hàng?\n"
                + "• Lịch đặt của tôi thế nào?";

        if (isAdmin) {
            goiY += "\n• Tổng doanh thu là bao nhiêu?"
                    + "\n• Có bao nhiêu xe đã hoàn thành?"
                    + "\n• Có bao nhiêu lịch chưa xác nhận?"
                    + "\n• Có tổng cộng bao nhiêu lịch đặt?"
                    + "\n• Thống kê tổng quan";
        }

        callback.onAnswer(goiY);
    }

    // ==========================================
    // TRA CỨU DỊCH VỤ
    // ==========================================

    private void traCuuDichVu(Callback callback) {
        db.collection("services")
                .whereEqualTo("hoạt động", true)
                .limit(15)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        callback.onAnswer("Hiện chưa tìm thấy dịch vụ đang hoạt động.");
                        return;
                    }

                    StringBuilder result = new StringBuilder("Các dịch vụ đang hoạt động:\n");

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String ten = doc.getString("name");
                        Object gia = doc.get("giá tiền");
                        String moTa = doc.getString("mô tả");
                        String thoiGian = doc.getString("thời gian");

                        if (ten == null || ten.trim().isEmpty()) ten = "Dịch vụ chưa có tên";

                        result.append("• ").append(ten);
                        if (gia != null) result.append(" — ").append(gia).append(" VNĐ");
                        if (thoiGian != null && !thoiGian.isEmpty()) {
                            result.append("\n  Thời gian: ").append(thoiGian);
                        }
                        if (moTa != null && !moTa.isEmpty()) {
                            result.append("\n  Mô tả: ").append(moTa);
                        }
                        result.append("\n");
                    }

                    callback.onAnswer(result.toString().trim());
                })
                .addOnFailureListener(e ->
                        callback.onAnswer("Mình chưa thể tải danh sách dịch vụ. Bạn hãy thử lại sau nhé.")
                );
    }

    // ==========================================
    // TRA CỨU PHỤ TÙNG
    // ==========================================

    private void traCuuPhuTung(Callback callback) {
        db.collection("spare_parts")
                .limit(50)
                .get()
                .addOnSuccessListener(snapshot -> {
                    StringBuilder result = new StringBuilder("Thông tin phụ tùng còn hàng:\n");
                    int count = 0;

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        Long soLuong = doc.getLong("soLuong");
                        boolean conHang = soLuong != null
                                ? soLuong > 0
                                : Boolean.TRUE.equals(doc.getBoolean("conHang"));

                        if (!conHang) continue;

                        String ten = doc.getString("ten");
                        Object gia = doc.get("gia");
                        if (ten == null) ten = "Phụ tùng chưa có tên";

                        result.append("• ").append(ten);
                        if (gia != null) result.append(" — ").append(gia).append(" VNĐ");
                        if (soLuong != null) result.append(" (còn ").append(soLuong).append(")");
                        result.append("\n");
                        count++;
                    }

                    if (count == 0) {
                        callback.onAnswer("Hiện chưa tìm thấy phụ tùng còn hàng.");
                    } else {
                        callback.onAnswer(result.toString().trim());
                    }
                })
                .addOnFailureListener(e ->
                        callback.onAnswer("Mình chưa thể tải dữ liệu phụ tùng. Bạn hãy thử lại sau nhé.")
                );
    }

    // ==========================================
    // LỊCH ĐẶT CỦA KHÁCH HÀNG
    // ==========================================

    private void traCuuLichCuaToi(Callback callback) {
        if (uid == null || uid.isEmpty()) {
            callback.onAnswer("Bạn cần đăng nhập để tra cứu lịch đặt.");
            return;
        }

        db.collection("appointments")
                .whereEqualTo("maNguoiDung", uid)
                .limit(20)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.isEmpty()) {
                        callback.onAnswer("Mình chưa tìm thấy lịch đặt nào của tài khoản này.");
                        return;
                    }

                    StringBuilder result = new StringBuilder("Lịch đặt của bạn:\n");

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String ngay = doc.getString("ngay");
                        String gio = doc.getString("gio");
                        String trangThai = doc.getString("trangThai");

                        result.append("• ").append(ngay == null ? "Chưa có ngày" : ngay);
                        if (gio != null && !gio.isEmpty()) result.append(" — ").append(gio);
                        if (trangThai != null && !trangThai.isEmpty()) {
                            result.append("\n  Trạng thái: ").append(trangThai);
                        }
                        result.append("\n");
                    }

                    callback.onAnswer(result.toString().trim());
                })
                .addOnFailureListener(e ->
                        callback.onAnswer("Không thể tra cứu lịch đặt. Bạn hãy thử lại sau.")
                );
    }

    // ==========================================
    // THỐNG KÊ DÀNH CHO ADMIN
    // ==========================================

    private void traCuuThongKeAdmin(Callback callback, String loaiThongKe) {
        if (!isAdmin) {
            callback.onAnswer("Chức năng thống kê chỉ dành cho quản trị viên.");
            return;
        }

        db.collection("appointments")
                .get()
                .addOnSuccessListener(snapshot -> {
                    final int tongLich = snapshot.size();
                    int choXacNhan = 0;
                    int soLichHoanThanh = 0;
                    Set<String> xeHoanThanh = new HashSet<>();
                    Set<String> maDichVuHoanThanh = new HashSet<>();
                    Map<String, Integer> soLanDungDichVu = new HashMap<>();

                    for (DocumentSnapshot doc : snapshot.getDocuments()) {
                        String trangThai = normalize(doc.getString("trangThai"));
                        String maXe = doc.getString("maXe");
                        String maDichVu = doc.getString("maDichVu");

                        boolean pending = trangThai.equals("pending")
                                || trangThai.contains("cho xac nhan")
                                || trangThai.contains("chua xac nhan")
                                || trangThai.contains("cho duyet")
                                || trangThai.contains("chua duyet");

                        boolean completed = laLichHoanThanh(trangThai);

                        if (pending) choXacNhan++;

                        if (completed) {
                            soLichHoanThanh++;

                            if (maXe != null && !maXe.trim().isEmpty()) {
                                xeHoanThanh.add(maXe);
                            }

                            if (maDichVu != null && !maDichVu.trim().isEmpty()) {
                                maDichVuHoanThanh.add(maDichVu);
                                int soLan = soLanDungDichVu.containsKey(maDichVu)
                                        ? soLanDungDichVu.get(maDichVu) : 0;
                                soLanDungDichVu.put(maDichVu, soLan + 1);
                            }
                        }
                    }

                    final int tongChoXacNhan = choXacNhan;
                    final int tongLichHoanThanh = soLichHoanThanh;
                    final int tongXeHoanThanh = xeHoanThanh.size();
                    final int tongLoaiDichVuHoanThanh = maDichVuHoanThanh.size();
                    final int tongLuotDichVuHoanThanh = tongSoLuot(soLanDungDichVu);

                    if ("total".equals(loaiThongKe)) {
                        callback.onAnswer(
                                "Thống kê lịch đặt:\n"
                                        + "• Tổng số lịch: " + tongLich + "\n"
                                        + "• Lịch chờ xác nhận: " + tongChoXacNhan + "\n"
                                        + "• Lịch hoàn thành: " + tongLichHoanThanh
                        );
                        return;
                    }

                    if ("pending".equals(loaiThongKe)) {
                        callback.onAnswer("Hiện có " + tongChoXacNhan + " lịch đang chờ xác nhận.");
                        return;
                    }

                    if ("completed_vehicles".equals(loaiThongKe)) {
                        callback.onAnswer(
                                "Có " + tongXeHoanThanh + " xe duy nhất có lịch hoàn thành.\n"
                                        + "Tổng số lịch hoàn thành: " + tongLichHoanThanh
                        );
                        return;
                    }

                    if ("completed_appointments".equals(loaiThongKe)) {
                        callback.onAnswer("Có " + tongLichHoanThanh + " lịch đã hoàn thành.");
                        return;
                    }

                    if ("completed_services".equals(loaiThongKe)) {
                        callback.onAnswer(
                                "Thống kê dịch vụ đã hoàn thành:\n"
                                        + "• Số loại dịch vụ: " + tongLoaiDichVuHoanThanh + "\n"
                                        + "• Tổng lượt sử dụng dịch vụ: " + tongLuotDichVuHoanThanh
                        );
                        return;
                    }

                    // Doanh thu ước tính: chỉ cộng giá dịch vụ của lịch hoàn thành.
                    db.collection("services")
                            .get()
                            .addOnSuccessListener(serviceSnapshot -> {
                                Map<String, Long> bangGia = new HashMap<>();

                                for (DocumentSnapshot service : serviceSnapshot.getDocuments()) {
                                    long gia = parseGia(service.get("giá tiền"));

                                    // Hỗ trợ mã dịch vụ trùng document ID.
                                    bangGia.put(service.getId(), gia);

                                    String ma = service.getString("dịch vụ");
                                    if (ma != null && !ma.trim().isEmpty()) {
                                        bangGia.put(ma, gia);
                                    }

                                    String maKhac = service.getString("maDichVu");
                                    if (maKhac != null && !maKhac.trim().isEmpty()) {
                                        bangGia.put(maKhac, gia);
                                    }
                                }

                                long doanhThu = 0;
                                int lichThieuGia = 0;

                                for (Map.Entry<String, Integer> entry : soLanDungDichVu.entrySet()) {
                                    Long gia = bangGia.get(entry.getKey());

                                    if (gia == null || gia <= 0) {
                                        lichThieuGia += entry.getValue();
                                    } else {
                                        doanhThu += gia * entry.getValue();
                                    }
                                }

                                StringBuilder traLoi = new StringBuilder();

                                if ("revenue".equals(loaiThongKe)) {
                                    traLoi.append("Doanh thu ước tính từ các lịch đã hoàn thành: ")
                                            .append(formatTien(doanhThu)).append(" VNĐ.");
                                    traLoi.append("\nLịch hoàn thành: ").append(tongLichHoanThanh);
                                    if (lichThieuGia > 0) {
                                        traLoi.append("\nLưu ý: ").append(lichThieuGia)
                                                .append(" lịch chưa ghép được giá dịch vụ nên chưa được tính.");
                                    }
                                } else {
                                    traLoi.append("THỐNG KÊ TỔNG QUAN AUTOCARE\n\n")
                                            .append("• Tổng số lịch đặt: ").append(tongLich).append("\n")
                                            .append("• Lịch chờ xác nhận: ").append(tongChoXacNhan).append("\n")
                                            .append("• Lịch hoàn thành: ").append(tongLichHoanThanh).append("\n")
                                            .append("• Số xe duy nhất đã hoàn thành: ").append(tongXeHoanThanh).append("\n")
                                            .append("• Doanh thu ước tính: ").append(formatTien(doanhThu)).append(" VNĐ");

                                    if (lichThieuGia > 0) {
                                        traLoi.append("\n• Lịch chưa ghép được giá: ").append(lichThieuGia);
                                    }
                                }

                                callback.onAnswer(traLoi.toString());
                            })
                            .addOnFailureListener(e ->
                                    callback.onAnswer(
                                            "Đã đọc được lịch đặt nhưng chưa đọc được giá dịch vụ. "
                                                    + "Bạn hãy kiểm tra quyền đọc collection services."
                                    )
                            );
                })
                .addOnFailureListener(e ->
                        callback.onAnswer(
                                "Không thể thống kê lịch đặt. Hãy kiểm tra quyền đọc collection appointments trong Firestore."
                        )
                );
    }

    private boolean laLichHoanThanh(String trangThai) {
        return trangThai.equals("completed")
                || trangThai.equals("complete")
                || trangThai.equals("done")
                || trangThai.contains("hoan thanh")
                || trangThai.contains("da sua xong")
                || trangThai.contains("da hoan tat");
    }

    private int tongSoLuot(Map<String, Integer> soLanDungDichVu) {
        int tong = 0;
        for (Integer soLan : soLanDungDichVu.values()) {
            if (soLan != null) tong += soLan;
        }
        return tong;
    }

    // Chuyển giá như "200.000" thành 200000.
    private long parseGia(Object giaObj) {
        if (giaObj == null) return 0;
        if (giaObj instanceof Number) return ((Number) giaObj).longValue();

        String gia = giaObj.toString().trim().replaceAll("[^0-9]", "");
        if (gia.isEmpty()) return 0;

        try {
            return Long.parseLong(gia);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String formatTien(long tien) {
        return String.format(Locale.US, "%,d", tien).replace(',', '.');
    }
}
