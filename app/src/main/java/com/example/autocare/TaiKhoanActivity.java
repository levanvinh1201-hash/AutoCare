package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaiKhoanActivity extends AppCompatActivity {
    private TextView tvTenTaiKhoan, tvEmailTaiKhoan;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private String hoTen = "Thành viên AutoCare";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tai_khoan);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        tvTenTaiKhoan = findViewById(R.id.tvTenTaiKhoan);
        tvEmailTaiKhoan = findViewById(R.id.tvEmailTaiKhoan);
        taiThongTinNguoiDung();
        findViewById(R.id.btnThongTinCaNhan).setOnClickListener(v -> hienThongTinCaNhan());
        findViewById(R.id.btnLichSuTaiKhoan).setOnClickListener(v ->
                startActivity(new Intent(this, LichSuDatLich.class)));
        findViewById(R.id.btnCaiDatTaiKhoan).setOnClickListener(v ->
                new AlertDialog.Builder(this).setTitle("Cài đặt AutoCare")
                        .setMessage("Các tùy chọn cài đặt sẽ được bổ sung sau.")
                        .setPositiveButton("Đóng", null).show());
        findViewById(R.id.btnDangXuatTaiKhoan).setOnClickListener(v -> xacNhanDangXuat());
        findViewById(R.id.btnNavTrangChu).setOnClickListener(v -> moManHinh(TrangChu.class));
        findViewById(R.id.btnNavLich).setOnClickListener(v -> moManHinh(DatLich.class));
        findViewById(R.id.btnNavXeCuaToi).setOnClickListener(v -> moManHinh(XeCuaToi.class));
        findViewById(R.id.btnNavDaiLy).setOnClickListener(v -> moManHinh(DaiLyActivity.class));
    }

    private void taiThongTinNguoiDung() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            tvTenTaiKhoan.setText("Chưa đăng nhập");
            tvEmailTaiKhoan.setText("");
            return;
        }
        tvTenTaiKhoan.setText(hoTen);
        tvEmailTaiKhoan.setText(user.getEmail() != null ? user.getEmail() : "");
        db.collection("users").document(user.getUid()).get()
                .addOnSuccessListener(doc -> {
                    String ten = doc.getString("hoTen");
                    if (ten != null && !ten.trim().isEmpty()) hoTen = ten.trim();
                    tvTenTaiKhoan.setText(hoTen);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Không tải được tên người dùng", Toast.LENGTH_SHORT).show());
    }

    private void hienThongTinCaNhan() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = user.getEmail() != null ? user.getEmail() : "Chưa cập nhật";
        new AlertDialog.Builder(this).setTitle("Thông tin cá nhân")
                .setMessage("Họ tên: " + hoTen + "\nEmail: " + email)
                .setPositiveButton("Đóng", null).show();
    }

    private void xacNhanDangXuat() {
        new AlertDialog.Builder(this).setTitle("Đăng xuất")
                .setMessage("Bạn muốn đăng xuất khỏi AutoCare?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    auth.signOut();
                    Intent intent = new Intent(this, DangNhap.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }).show();
    }

    private void moManHinh(Class<?> activityClass) {
        startActivity(new Intent(this, activityClass));
    }
}
