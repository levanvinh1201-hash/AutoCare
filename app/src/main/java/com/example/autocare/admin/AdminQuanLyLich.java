package com.example.autocare.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminQuanLyLich extends AppCompatActivity {

    private RecyclerView recyclerLichAdmin;
    private TextView txtSoLuongLichAdmin;

    private ArrayList<TaiKhoanLich> danhSachTaiKhoan;

    private TaiKhoanLichAdapter adapter;

    private FirebaseFirestore firestore;

    // Lưu số lịch của từng user
    private Map<String, Integer> soLuongLichTheoUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_quan_ly_lich
        );


        // =========================
        // FIRESTORE
        // =========================

        firestore =
                FirebaseFirestore.getInstance();


        // =========================
        // ÁNH XẠ VIEW
        // =========================

        recyclerLichAdmin =
                findViewById(
                        R.id.recyclerLichAdmin
                );

        txtSoLuongLichAdmin =
                findViewById(
                        R.id.txtSoLuongLichAdmin
                );


        // =========================
        // DANH SÁCH
        // =========================

        danhSachTaiKhoan =
                new ArrayList<>();

        soLuongLichTheoUser =
                new HashMap<>();


        // =========================
        // RECYCLERVIEW
        // =========================

        recyclerLichAdmin.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new TaiKhoanLichAdapter(
                        this,
                        danhSachTaiKhoan
                );


        recyclerLichAdmin.setAdapter(
                adapter
        );


        // =========================
        // TẢI DỮ LIỆU
        // =========================

        taiDanhSachTaiKhoan();
    }


    // =========================================================
    // TẢI DANH SÁCH TÀI KHOẢN KHÁCH HÀNG
    // =========================================================

    private void taiDanhSachTaiKhoan() {

        // Trước tiên tải tất cả lịch
        firestore
                .collection("appointments")
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            // Xóa dữ liệu cũ
                            soLuongLichTheoUser.clear();


                            // =========================
                            // ĐẾM SỐ LỊCH THEO USER
                            // =========================

                            for (var document :
                                    querySnapshot) {

                                String maNguoiDung =
                                        document.getString(
                                                "maNguoiDung"
                                        );

                                if (maNguoiDung != null &&
                                        !maNguoiDung.isEmpty()) {

                                    int soLuong =
                                            soLuongLichTheoUser.getOrDefault(
                                                    maNguoiDung,
                                                    0
                                            );

                                    soLuongLichTheoUser.put(
                                            maNguoiDung,
                                            soLuong + 1
                                    );
                                }
                            }


                            // Sau khi đếm xong
                            // tải danh sách tài khoản
                            taiTaiKhoanKhachHang();

                        }
                )

                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    AdminQuanLyLich.this,
                                    "Không tải được lịch: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
    }


    // =========================================================
    // TẢI TÀI KHOẢN KHÁCH HÀNG
    // =========================================================

    private void taiTaiKhoanKhachHang() {

        firestore
                .collection("users")
                .whereEqualTo(
                        "role",
                        "customer"
                )
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            danhSachTaiKhoan.clear();


                            // =========================
                            // DUYỆT TỪNG TÀI KHOẢN
                            // =========================

                            for (var document :
                                    querySnapshot) {

                                String userId =
                                        document.getString(
                                                "userId"
                                        );

                                String hoTen =
                                        document.getString(
                                                "hoTen"
                                        );

                                String email =
                                        document.getString(
                                                "email"
                                        );


                                // =========================
                                // GIÁ TRỊ MẶC ĐỊNH
                                // =========================

                                if (userId == null ||
                                        userId.isEmpty()) {

                                    userId =
                                            document.getId();
                                }

                                if (hoTen == null ||
                                        hoTen.isEmpty()) {

                                    hoTen =
                                            "Chưa có tên";
                                }

                                if (email == null) {
                                    email = "";
                                }


                                // =========================
                                // LẤY SỐ LỊCH
                                // =========================

                                int soLuongLich =
                                        soLuongLichTheoUser.getOrDefault(
                                                userId,
                                                0
                                        );


                                // =========================
                                // TẠO ĐỐI TƯỢNG
                                // =========================

                                TaiKhoanLich taiKhoan =
                                        new TaiKhoanLich(
                                                userId,
                                                hoTen,
                                                email,
                                                soLuongLich
                                        );


                                danhSachTaiKhoan.add(
                                        taiKhoan
                                );
                            }


                            // =========================
                            // CẬP NHẬT SỐ TÀI KHOẢN
                            // =========================

                            txtSoLuongLichAdmin.setText(
                                    danhSachTaiKhoan.size()
                                            + " tài khoản"
                            );


                            // =========================
                            // CẬP NHẬT RECYCLERVIEW
                            // =========================

                            adapter.notifyDataSetChanged();


                            // =========================
                            // KHÔNG CÓ KHÁCH
                            // =========================

                            if (danhSachTaiKhoan.isEmpty()) {

                                Toast.makeText(
                                        AdminQuanLyLich.this,
                                        "Chưa có tài khoản khách hàng",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                )

                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    AdminQuanLyLich.this,
                                    "Không tải được tài khoản: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
    }
}