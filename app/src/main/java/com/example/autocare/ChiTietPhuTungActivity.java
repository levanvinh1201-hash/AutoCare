package com.example.autocare;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ChiTietPhuTungActivity extends AppCompatActivity {

    private ImageView imgChiTiet;
    private TextView txtTen;
    private TextView txtGia;
    private TextView txtTrangThai;
    private TextView txtSoLuong;
    private TextView txtMoTa;

    private String phuTungId;
    private String ten;
    private String gia;
    private String hinhAnh;
    private String moTa;

    private int soLuong;
    private boolean conHang;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet_phu_tung);
        Button btnDatLichPhuTung = findViewById(R.id.btnDatLichPhuTung);

        btnDatLichPhuTung.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ChiTietPhuTungActivity.this,
                    DatLich.class
            );
            startActivity(intent);
        });
        anhXa();
        db = FirebaseFirestore.getInstance();

        nhanDuLieu();
        hienThiChiTiet();

        findViewById(R.id.btnQuayLaiChiTiet)
                .setOnClickListener(v -> finish());


    }

    private void anhXa() {
        imgChiTiet = findViewById(R.id.imgChiTietPhuTung);
        txtTen = findViewById(R.id.txtChiTietTen);
        txtGia = findViewById(R.id.txtChiTietGia);
        txtTrangThai = findViewById(R.id.txtChiTietTrangThai);
        txtSoLuong = findViewById(R.id.txtChiTietSoLuong);
        txtMoTa = findViewById(R.id.txtChiTietMoTa);

    }

    private void nhanDuLieu() {
        phuTungId = getIntent().getStringExtra("phuTungId");
        ten = getIntent().getStringExtra("ten");
        gia = getIntent().getStringExtra("gia");
        hinhAnh = getIntent().getStringExtra("hinhAnh");
        moTa = getIntent().getStringExtra("moTa");

        soLuong = getIntent().getIntExtra("soLuong", 0);
        conHang = getIntent().getBooleanExtra("conHang", false);
    }

    private void hienThiChiTiet() {
        txtTen.setText(
                ten == null || ten.isEmpty() ? "Phụ tùng" : ten
        );

        txtGia.setText(dinhDangGia(gia));

        txtSoLuong.setText("Còn " + soLuong + " sản phẩm");

        txtMoTa.setText(
                moTa == null || moTa.trim().isEmpty()
                        ? "Chưa có mô tả cho sản phẩm này."
                        : moTa
        );

        if (conHang && soLuong > 0) {
            txtTrangThai.setText("Còn hàng");
            txtTrangThai.setTextColor(0xFF2E7D32);

        } else {
            txtTrangThai.setText("Hết hàng");
            txtTrangThai.setTextColor(0xFFD32F2F);

        }

        // Hiển thị ảnh lưu trong res/drawable
        if (hinhAnh != null && !hinhAnh.trim().isEmpty()) {
            int resourceId = getResources().getIdentifier(
                    hinhAnh,
                    "drawable",
                    getPackageName()
            );

            if (resourceId != 0) {
                imgChiTiet.setImageResource(resourceId);
            } else {
                imgChiTiet.setImageResource(
                        android.R.drawable.ic_menu_gallery
                );
            }
        }
    }

    private String dinhDangGia(String giaGoc) {
        if (giaGoc == null || giaGoc.trim().isEmpty()) {
            return "Liên hệ";
        }

        try {
            String giaSo = giaGoc.trim()
                    .replace(".", "")
                    .replace(",", "")
                    .replace(" ", "");

            long giaTri = Long.parseLong(giaSo);

            DecimalFormatSymbols symbols =
                    new DecimalFormatSymbols(Locale.US);
            symbols.setGroupingSeparator('.');

            DecimalFormat formatter =
                    new DecimalFormat("#,###", symbols);

            return formatter.format(giaTri) + " VNĐ";

        } catch (NumberFormatException e) {
            return giaGoc + " VNĐ";
        }
    }






}
