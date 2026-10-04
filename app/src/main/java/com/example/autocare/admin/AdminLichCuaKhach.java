package com.example.autocare.admin;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.LichDat;
import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminLichCuaKhach extends AppCompatActivity {

    private RecyclerView recyclerLichKhach;
    private TextView txtTenKhachHang;
    private TextView txtSoLuongLichKhach;

    private ArrayList<LichDat> danhSachLich;
    private AdminLichAdapter adapter;

    private FirebaseFirestore firestore;

    private String userId;
    private String hoTen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_lich_cua_khach);

        // Nhận thông tin khách hàng từ màn hình trước
        userId = getIntent().getStringExtra("userId");
        hoTen = getIntent().getStringExtra("hoTen");

        // Ánh xạ View
        recyclerLichKhach = findViewById(R.id.recyclerLichKhach);
        txtTenKhachHang = findViewById(R.id.txtTenKhachHang);
        txtSoLuongLichKhach = findViewById(R.id.txtSoLuongLichKhach);

        firestore = FirebaseFirestore.getInstance();

        // Hiển thị tên khách hàng
        if (hoTen != null && !hoTen.isEmpty()) {
            txtTenKhachHang.setText(hoTen);
        } else {
            txtTenKhachHang.setText("Khách hàng");
        }

        // RecyclerView
        danhSachLich = new ArrayList<>();

        recyclerLichKhach.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AdminLichAdapter(danhSachLich);
        recyclerLichKhach.setAdapter(adapter);

        // Tải lịch của khách hàng
        taiLichCuaKhach();
    }

    private void taiLichCuaKhach() {

        if (userId == null || userId.isEmpty()) {
            Toast.makeText(
                    this,
                    "Không xác định được tài khoản khách hàng",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        firestore.collection("appointments")
                .whereEqualTo("maNguoiDung", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachLich.clear();

                    for (var document : querySnapshot) {

                        String maLich = document.getString("maLich");
                        String maNguoiDung = document.getString("maNguoiDung");
                        String maXe = document.getString("maXe");
                        String maDichVu = document.getString("maDichVu");
                        String ngay = document.getString("ngay");
                        String gio = document.getString("gio");
                        String ghiChu = document.getString("ghiChu");
                        String trangThai = document.getString("trangThai");

                        // Nếu maLich không có thì dùng ID document
                        if (maLich == null || maLich.isEmpty()) {
                            maLich = document.getId();
                        }

                        if (maNguoiDung == null) maNguoiDung = "";
                        if (maXe == null) maXe = "";
                        if (maDichVu == null) maDichVu = "";
                        if (ngay == null) ngay = "";
                        if (gio == null) gio = "";
                        if (ghiChu == null) ghiChu = "";
                        if (trangThai == null) trangThai = "PENDING";

                        LichDat lich = new LichDat(
                                maLich,
                                maNguoiDung,
                                maXe,
                                maDichVu,
                                ngay,
                                gio,
                                ghiChu,
                                trangThai
                        );

                        danhSachLich.add(lich);
                    }

                    // Cập nhật số lượng
                    txtSoLuongLichKhach.setText(
                            danhSachLich.size() + " lịch"
                    );

                    // Cập nhật RecyclerView
                    adapter.notifyDataSetChanged();

                    if (danhSachLich.isEmpty()) {
                        Toast.makeText(
                                AdminLichCuaKhach.this,
                                "Khách hàng chưa có lịch đặt nào",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminLichCuaKhach.this,
                            "Không tải được lịch: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }
}