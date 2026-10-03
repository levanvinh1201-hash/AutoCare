package com.example.autocare.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocare.DangNhap;
import com.example.autocare.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        btnQuanLyLich =
                findViewById(R.id.btnQuanLyLich);

        btnQuanLyDichVu =
                findViewById(R.id.btnQuanLyDichVu);

        btnQuanLyTaiKhoan =
                findViewById(R.id.btnQuanLyTaiKhoan);

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
    }


    // =====================================================
    // CẬP NHẬT DASHBOARD MỖI KHI QUAY LẠI TRANG ADMIN
    // =====================================================

    @Override
    protected void onResume() {
        super.onResume();

        taiDashboard();
    }
}