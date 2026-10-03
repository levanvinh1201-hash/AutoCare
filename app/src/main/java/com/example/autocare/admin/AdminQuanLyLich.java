package com.example.autocare.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.example.autocare.LichDat;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminQuanLyLich extends AppCompatActivity {

    private RecyclerView recyclerLichAdmin;
    private TextView txtSoLuongLichAdmin;

    private ArrayList<LichDat> danhSachLich;

    private AdminLichAdapter adapter;

    private FirebaseFirestore firestore;


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

        danhSachLich =
                new ArrayList<>();


        recyclerLichAdmin.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new AdminLichAdapter(
                        danhSachLich
                );


        recyclerLichAdmin.setAdapter(
                adapter
        );


        // =========================
        // TẢI LỊCH
        // =========================

        taiDanhSachLich();
    }


    // =========================================================
    // TẢI TẤT CẢ LỊCH ĐẶT
    // =========================================================

    private void taiDanhSachLich() {

        firestore
                .collection("appointments")
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            danhSachLich.clear();


                            for (var document :
                                    querySnapshot) {

                                // =========================
                                // LẤY DỮ LIỆU
                                // =========================

                                String maLich =
                                        document.getString(
                                                "maLich"
                                        );

                                String maNguoiDung =
                                        document.getString(
                                                "maNguoiDung"
                                        );

                                String maXe =
                                        document.getString(
                                                "maXe"
                                        );

                                String maDichVu =
                                        document.getString(
                                                "maDichVu"
                                        );

                                String ngay =
                                        document.getString(
                                                "ngay"
                                        );

                                String gio =
                                        document.getString(
                                                "gio"
                                        );

                                String ghiChu =
                                        document.getString(
                                                "ghiChu"
                                        );

                                String trangThai =
                                        document.getString(
                                                "trangThai"
                                        );


                                // =========================
                                // GIÁ TRỊ MẶC ĐỊNH
                                // =========================

                                if (maLich == null ||
                                        maLich.isEmpty()) {

                                    maLich =
                                            document.getId();
                                }

                                if (maNguoiDung == null) {
                                    maNguoiDung = "";
                                }

                                if (maXe == null) {
                                    maXe = "";
                                }

                                if (maDichVu == null) {
                                    maDichVu = "";
                                }

                                if (ngay == null) {
                                    ngay = "";
                                }

                                if (gio == null) {
                                    gio = "";
                                }

                                if (ghiChu == null) {
                                    ghiChu = "";
                                }

                                if (trangThai == null) {
                                    trangThai = "PENDING";
                                }


                                // =========================
                                // TẠO ĐỐI TƯỢNG LỊCH
                                // =========================

                                LichDat lich =
                                        new LichDat(
                                                maLich,
                                                maNguoiDung,
                                                maXe,
                                                maDichVu,
                                                ngay,
                                                gio,
                                                ghiChu,
                                                trangThai
                                        );


                                danhSachLich.add(
                                        lich
                                );
                            }


                            // =========================
                            // CẬP NHẬT SỐ LƯỢNG
                            // =========================

                            txtSoLuongLichAdmin.setText(
                                    danhSachLich.size()
                                            + " lịch"
                            );


                            // =========================
                            // CẬP NHẬT RECYCLERVIEW
                            // =========================

                            adapter.notifyDataSetChanged();


                            // =========================
                            // KHÔNG CÓ LỊCH
                            // =========================

                            if (danhSachLich.isEmpty()) {

                                Toast.makeText(
                                        AdminQuanLyLich.this,
                                        "Chưa có lịch đặt nào",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

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
}