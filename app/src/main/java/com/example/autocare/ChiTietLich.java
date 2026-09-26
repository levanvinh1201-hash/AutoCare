package com.example.autocare;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class ChiTietLich extends AppCompatActivity {

    private TextView txtChiTietDichVu;
    private TextView txtChiTietGia;
    private TextView txtChiTietTrangThai;
    private TextView txtChiTietXe;
    private TextView txtChiTietBienSo;
    private TextView txtChiTietNgay;
    private TextView txtChiTietGio;
    private TextView txtChiTietGhiChu;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_lich);

        // Kết nối Firestore
        firestore = FirebaseFirestore.getInstance();

        // Ánh xạ giao diện
        txtChiTietDichVu = findViewById(R.id.txtChiTietDichVu);
        txtChiTietGia = findViewById(R.id.txtChiTietGia);
        txtChiTietTrangThai = findViewById(R.id.txtChiTietTrangThai);
        txtChiTietXe = findViewById(R.id.txtChiTietXe);
        txtChiTietBienSo = findViewById(R.id.txtChiTietBienSo);
        txtChiTietNgay = findViewById(R.id.txtChiTietNgay);
        txtChiTietGio = findViewById(R.id.txtChiTietGio);
        txtChiTietGhiChu = findViewById(R.id.txtChiTietGhiChu);

        // Nhận mã lịch từ màn hình trước
        String maLich = getIntent().getStringExtra("maLich");

        if (maLich == null || maLich.isEmpty()) {
            return;
        }

        // Lấy thông tin lịch
        firestore.collection("appointments")
                .document(maLich)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        return;
                    }

                    String maXe = documentSnapshot.getString("maXe");
                    String maDichVu = documentSnapshot.getString("maDichVu");

                    String ngay = documentSnapshot.getString("ngay");
                    String gio = documentSnapshot.getString("gio");
                    String ghiChu = documentSnapshot.getString("ghiChu");
                    String trangThai = documentSnapshot.getString("trangThai");

                    // Hiển thị thông tin cơ bản
                    txtChiTietNgay.setText(ngay);
                    txtChiTietGio.setText(gio);

                    if (ghiChu == null || ghiChu.isEmpty()) {
                        txtChiTietGhiChu.setText("Không có ghi chú");
                    } else {
                        txtChiTietGhiChu.setText(ghiChu);
                    }

                    // Hiển thị trạng thái
                    if ("PENDING".equals(trangThai)) {
                        txtChiTietTrangThai.setText("Chờ xác nhận");
                        txtChiTietTrangThai.setTextColor(
                                getColor(android.R.color.holo_orange_dark)
                        );

                    } else if ("CONFIRMED".equals(trangThai)) {
                        txtChiTietTrangThai.setText("Đã xác nhận");
                        txtChiTietTrangThai.setTextColor(
                                getColor(android.R.color.holo_green_dark)
                        );

                    } else if ("CANCELLED".equals(trangThai)) {
                        txtChiTietTrangThai.setText("Đã hủy");
                        txtChiTietTrangThai.setTextColor(
                                getColor(android.R.color.holo_red_dark)
                        );

                    } else if ("COMPLETED".equals(trangThai)) {
                        txtChiTietTrangThai.setText("Đã hoàn thành");
                        txtChiTietTrangThai.setTextColor(
                                getColor(android.R.color.holo_green_dark)
                        );

                    } else {
                        txtChiTietTrangThai.setText(trangThai);
                    }

                    // Lấy thông tin xe
                    if (maXe != null) {
                        firestore.collection("vehicles")
                                .document(maXe)
                                .get()
                                .addOnSuccessListener(xe -> {

                                    if (xe.exists()) {

                                        String hangXe = xe.getString("brand");
                                        String mauXe = xe.getString("model");
                                        String bienSo = xe.getString("licensePlate");

                                        txtChiTietXe.setText(
                                                hangXe + " " + mauXe
                                        );

                                        txtChiTietBienSo.setText(
                                                "Biển số: " + bienSo
                                        );
                                    }
                                });
                    }

                    // Lấy thông tin dịch vụ
                    if (maDichVu != null) {

                        firestore.collection("services")
                                .whereEqualTo("dịch vụ", maDichVu)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {

                                    if (!querySnapshot.isEmpty()) {

                                        String tenDichVu =
                                                querySnapshot.getDocuments()
                                                        .get(0)
                                                        .getString("name");

                                        String gia =
                                                querySnapshot.getDocuments()
                                                        .get(0)
                                                        .getString("giá tiền");

                                        txtChiTietDichVu.setText(tenDichVu);
                                        txtChiTietGia.setText(
                                                gia + " VNĐ"
                                        );
                                    }
                                });
                    }

                });
    }
}