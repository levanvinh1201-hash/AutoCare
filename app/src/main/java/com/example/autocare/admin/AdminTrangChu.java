package com.example.autocare.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocare.DangNhap;
import com.example.autocare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AdminTrangChu extends AppCompatActivity {

    private Button btnQuanLyLich;
    private Button btnQuanLyDichVu;
    private Button btnQuanLyTaiKhoan;
    private Button btnAdminDangXuat;

    // Dashboard
    private TextView txtTongTaiKhoan;
    private TextView txtTongXe;
    private TextView txtTongLich;
    private TextView txtLichChoXacNhan;
    private TextView txtDichVuHoatDong;
    private TextView txtDoanhThu;

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_trang_chu);

        // =============================
        // FIREBASE
        // =============================

        firebaseAuth = FirebaseAuth.getInstance();

        firestore =
                FirebaseFirestore.getInstance();


        // =============================
        // ÁNH XẠ BUTTON
        // =============================
        View btnQuanLyLich = findViewById(R.id.btnQuanLyLich);
        View btnQuanLyDichVu = findViewById(R.id.btnQuanLyDichVu);
        View btnQuanLyPhuTung = findViewById(R.id.btnQuanLyPhuTung);
        View btnQuanLyTaiKhoan = findViewById(R.id.btnQuanLyTaiKhoan);
        btnAdminDangXuat =
                findViewById(R.id.btnAdminDangXuat);


        // =============================
        // ÁNH XẠ DASHBOARD
        // =============================

        txtTongTaiKhoan =
                findViewById(R.id.txtTongTaiKhoan);

        txtTongXe =
                findViewById(R.id.txtTongXe);

        txtTongLich =
                findViewById(R.id.txtTongLich);

        txtLichChoXacNhan =
                findViewById(R.id.txtLichChoXacNhan);

        txtDichVuHoatDong =
                findViewById(R.id.txtDichVuHoatDong);

        txtDoanhThu =
                findViewById(R.id.txtDoanhThu);


        // =============================
        // TẢI DASHBOARD
        // =============================

        taiDashboard();


        // =============================
        // QUẢN LÝ TÀI KHOẢN
        // =============================

        btnQuanLyTaiKhoan.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminTrangChu.this,
                            AdminQuanLyTaiKhoan.class
                    );

            startActivity(intent);
        });


        // =============================
        // QUẢN LÝ LỊCH
        // =============================

        btnQuanLyLich.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminTrangChu.this,
                            AdminQuanLyLich.class
                    );

            startActivity(intent);
        });


        // =============================
        // QUẢN LÝ DỊCH VỤ
        // =============================

        btnQuanLyDichVu.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            AdminTrangChu.this,
                            AdminQuanLyDichVu.class
                    );

            startActivity(intent);
        });

        btnQuanLyPhuTung.setOnClickListener(v -> {
            Intent intent = new Intent(AdminTrangChu.this, AdminQuanLyPhuTung.class);
            startActivity(intent);
        });

        // =============================
        // ĐĂNG XUẤT
        // =============================

        btnAdminDangXuat.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent =
                    new Intent(
                            AdminTrangChu.this,
                            DangNhap.class
                    );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }


    // =====================================================
    // DASHBOARD
    // =====================================================

    private void taiDashboard() {


        // =============================
        // 1. TỔNG TÀI KHOẢN
        // =============================

        firestore
                .collection("users")
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int soLuong =
                                    querySnapshot.size();

                            txtTongTaiKhoan.setText(
                                    String.valueOf(soLuong)
                            );
                        }
                );


        // =============================
        // 2. TỔNG XE
        // =============================

        firestore
                .collection("vehicles")
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int soLuong =
                                    querySnapshot.size();

                            txtTongXe.setText(
                                    String.valueOf(soLuong)
                            );
                        }
                );


        // =============================
        // 3. TỔNG LỊCH ĐẶT
        // =============================

        firestore
                .collection("appointments")
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int soLuong =
                                    querySnapshot.size();

                            txtTongLich.setText(
                                    String.valueOf(soLuong)
                            );
                        }
                );


        // =============================
        // 4. LỊCH CHỜ XÁC NHẬN
        // =============================

        firestore
                .collection("appointments")
                .whereEqualTo(
                        "trangThai",
                        "PENDING"
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int soLuong =
                                    querySnapshot.size();

                            txtLichChoXacNhan.setText(
                                    String.valueOf(soLuong)
                            );
                        }
                );


        // =============================
        // 5. DỊCH VỤ ĐANG HOẠT ĐỘNG
        // =============================

        firestore
                .collection("services")
                .whereEqualTo(
                        "hoạt động",
                        true
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            int soLuong =
                                    querySnapshot.size();

                            txtDichVuHoatDong.setText(
                                    String.valueOf(soLuong)
                            );
                        }
                );


        // =============================
        // 6. TÍNH DOANH THU
        // =============================

        tinhDoanhThu();
    }


    // =====================================================
    // TÍNH DOANH THU
    // =====================================================

    private void tinhDoanhThu() {

        // Lấy tất cả dịch vụ trước
        firestore
                .collection("services")
                .get()
                .addOnSuccessListener(serviceSnapshot -> {

                    // Tạo danh sách:
                    // mã dịch vụ -> giá tiền
                    Map<String, Long> bangGia =
                            new HashMap<>();

                    for (var document : serviceSnapshot.getDocuments()) {

                        String maDichVu =
                                document.getString("dịch vụ");

                        String giaTien =
                                document.getString("giá tiền");

                        if (maDichVu == null ||
                                giaTien == null) {
                            continue;
                        }

                        try {

                            // Ví dụ:
                            // "100.000" -> 100000
                            // "1.500.000" -> 1500000

                            String giaKhongDau =
                                    giaTien
                                            .replace(".", "")
                                            .replace(",", "")
                                            .trim();

                            long gia =
                                    Long.parseLong(giaKhongDau);

                            bangGia.put(
                                    maDichVu,
                                    gia
                            );

                        } catch (NumberFormatException e) {

                            // Bỏ qua giá không hợp lệ

                        }
                    }


                    // =====================================
                    // LẤY CÁC LỊCH ĐÃ HOÀN THÀNH
                    // =====================================

                    firestore
                            .collection("appointments")
                            .whereEqualTo(
                                    "trangThai",
                                    "COMPLETED"
                            )
                            .get()
                            .addOnSuccessListener(
                                    appointmentSnapshot -> {

                                        long tongDoanhThu = 0;


                                        for (var document :
                                                appointmentSnapshot
                                                        .getDocuments()) {

                                            String maDichVu =
                                                    document.getString(
                                                            "maDichVu"
                                                    );

                                            if (maDichVu == null) {
                                                continue;
                                            }


                                            Long gia =
                                                    bangGia.get(
                                                            maDichVu
                                                    );

                                            if (gia != null) {

                                                tongDoanhThu += gia;

                                            }
                                        }


                                        // =================================
                                        // HIỂN THỊ DOANH THU
                                        // =================================

                                        txtDoanhThu.setText(
                                                dinhDangTien(
                                                        tongDoanhThu
                                                )
                                        );
                                    }
                            );
                });
    }


    // =====================================================
    // ĐỊNH DẠNG TIỀN
    // =====================================================

    private String dinhDangTien(long soTien) {

        String tien =
                String.format(
                        "%,d",
                        soTien
                );

        // Đổi dấu phẩy thành dấu chấm
        // Ví dụ:
        // 1500000 -> 1.500.000

        tien =
                tien.replace(",", ".");

        return tien + " VNĐ";
    }


    // =====================================================
    // CẬP NHẬT DASHBOARD
    // MỖI KHI QUAY LẠI TRANG ADMIN
    // =====================================================

    @Override
    protected void onResume() {

        super.onResume();

        taiDashboard();
    }
}