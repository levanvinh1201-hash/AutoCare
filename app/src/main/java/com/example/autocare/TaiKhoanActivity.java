package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TaiKhoanActivity extends AppCompatActivity {

    private TextView tvTenTaiKhoan;
    private TextView tvEmailTaiKhoan;
    private TextView tvSoDienThoaiTaiKhoan;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private String hoTen = "Thành viên AutoCare";
    private String soDienThoai = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BottomNavHelper.setup(this);

        setContentView(R.layout.activity_tai_khoan);

        // Khởi tạo Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Ánh xạ giao diện
        tvTenTaiKhoan = findViewById(R.id.tvTenTaiKhoan);
        tvEmailTaiKhoan = findViewById(R.id.tvEmailTaiKhoan);
        tvSoDienThoaiTaiKhoan =
                findViewById(R.id.tvSoDienThoaiTaiKhoan);

        // Tải thông tin tài khoản
        taiThongTinNguoiDung();

        // Xem thông tin cá nhân
        findViewById(R.id.btnThongTinCaNhan)
                .setOnClickListener(v -> hienThongTinCaNhan());

        // Chỉnh sửa số điện thoại
        findViewById(R.id.btnSoDienThoaiTaiKhoan)
                .setOnClickListener(v -> chinhSuaSoDienThoai());

        // Lịch sử đặt lịch
        findViewById(R.id.btnLichSuTaiKhoan)
                .setOnClickListener(v ->
                        startActivity(
                                new Intent(this, LichSuDatLich.class)
                        ));

        // Đăng xuất
        findViewById(R.id.btnDangXuatTaiKhoan)
                .setOnClickListener(v -> xacNhanDangXuat());

        // Thanh điều hướng
        findViewById(R.id.btnNavTrangChu)
                .setOnClickListener(v -> moManHinh(TrangChu.class));

        findViewById(R.id.btnNavLich)
                .setOnClickListener(v -> moManHinh(DatLich.class));

        findViewById(R.id.btnNavXeCuaToi)
                .setOnClickListener(v -> moManHinh(XeCuaToi.class));

        findViewById(R.id.btnNavDaiLy)
                .setOnClickListener(v -> moManHinh(DaiLyActivity.class));
    }

    // =====================================================
    // 1. TẢI THÔNG TIN NGƯỜI DÙNG TỪ FIREBASE
    // =====================================================

    private void taiThongTinNguoiDung() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {
            tvTenTaiKhoan.setText("Chưa đăng nhập");
            tvEmailTaiKhoan.setText("");
            tvSoDienThoaiTaiKhoan.setText("Chưa cập nhật");
            return;
        }

        tvTenTaiKhoan.setText(hoTen);

        tvEmailTaiKhoan.setText(
                user.getEmail() != null
                        ? user.getEmail()
                        : ""
        );

        tvSoDienThoaiTaiKhoan.setText("Đang tải...");

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        tvSoDienThoaiTaiKhoan.setText(
                                "Chưa cập nhật"
                        );
                        return;
                    }

                    // Lấy họ tên
                    String ten = doc.getString("hoTen");

                    if (ten != null && !ten.trim().isEmpty()) {
                        hoTen = ten.trim();
                    }

                    tvTenTaiKhoan.setText(hoTen);

                    // Lấy số điện thoại
                    String sdt = doc.getString("soDienThoai");

                    if (sdt != null && !sdt.trim().isEmpty()) {

                        soDienThoai = sdt.trim();

                        tvSoDienThoaiTaiKhoan.setText(
                                soDienThoai
                        );

                    } else {

                        soDienThoai = "";

                        tvSoDienThoaiTaiKhoan.setText(
                                "Chưa cập nhật"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    tvSoDienThoaiTaiKhoan.setText(
                            "Không tải được"
                    );

                    Toast.makeText(
                            this,
                            "Không tải được thông tin tài khoản",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =====================================================
    // 2. HIỂN THỊ THÔNG TIN CÁ NHÂN
    // =====================================================

    private void hienThongTinCaNhan() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String email = user.getEmail() != null
                ? user.getEmail()
                : "Chưa cập nhật";

        String sdt = soDienThoai.isEmpty()
                ? "Chưa cập nhật"
                : soDienThoai;

        new AlertDialog.Builder(this)
                .setTitle("Thông tin cá nhân")
                .setMessage(
                        "Họ tên: " + hoTen
                                + "\n\nEmail: " + email
                                + "\n\nSố điện thoại: " + sdt
                )
                .setPositiveButton("Đóng", null)
                .show();
    }

    // =====================================================
    // 3. CHỈNH SỬA SỐ ĐIỆN THOẠI
    // =====================================================

    private void chinhSuaSoDienThoai() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // Tạo ô nhập số điện thoại
        EditText edtSoDienThoai = new EditText(this);

        edtSoDienThoai.setHint(
                "Nhập số điện thoại của bạn"
        );

        edtSoDienThoai.setInputType(
                InputType.TYPE_CLASS_PHONE
        );

        edtSoDienThoai.setSingleLine(true);

        edtSoDienThoai.setPadding(
                40, 30, 40, 30
        );

        // Hiển thị số điện thoại hiện tại
        edtSoDienThoai.setText(soDienThoai);

        edtSoDienThoai.setSelection(
                edtSoDienThoai.getText().length()
        );

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Cập nhật số điện thoại")
                        .setMessage(
                                "Số điện thoại giúp gara liên hệ "
                                        + "với bạn để xác nhận lịch hẹn."
                        )
                        .setView(edtSoDienThoai)
                        .setNegativeButton("Hủy", null)
                        .setPositiveButton("Lưu", null)
                        .create();

        dialog.setOnShowListener(unused -> {

            dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            ).setOnClickListener(v -> {

                String sdt = edtSoDienThoai
                        .getText()
                        .toString()
                        .trim();

                // Kiểm tra số điện thoại Việt Nam
                if (!sdt.matches("^0[0-9]{9}$")) {

                    edtSoDienThoai.setError(
                            "Số điện thoại phải có 10 chữ số "
                                    + "và bắt đầu bằng số 0"
                    );

                    return;
                }

                // Khóa nút lưu để tránh nhấn nhiều lần
                dialog.getButton(
                        AlertDialog.BUTTON_POSITIVE
                ).setEnabled(false);

                // Cập nhật Firebase Firestore
                db.collection("users")
                        .document(user.getUid())
                        .update("soDienThoai", sdt)
                        .addOnSuccessListener(unusedResult -> {

                            // Cập nhật biến
                            soDienThoai = sdt;

                            // Cập nhật giao diện
                            tvSoDienThoaiTaiKhoan.setText(
                                    soDienThoai
                            );

                            Toast.makeText(
                                    this,
                                    "Cập nhật số điện thoại thành công!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {

                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            ).setEnabled(true);

                            Toast.makeText(
                                    this,
                                    "Cập nhật thất bại: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        });

        dialog.show();
    }

    // =====================================================
    // 4. XÁC NHẬN ĐĂNG XUẤT
    // =====================================================

    private void xacNhanDangXuat() {

        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage(
                        "Bạn muốn đăng xuất khỏi AutoCare?"
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton(
                        "Đăng xuất",
                        (dialog, which) -> {

                            auth.signOut();

                            Intent intent =
                                    new Intent(
                                            this,
                                            DangNhap.class
                                    );

                            intent.setFlags(
                                    Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
                            );

                            startActivity(intent);
                            finish();
                        }
                )
                .show();
    }

    private void moManHinh(Class<?> activityClass) {

        startActivity(
                new Intent(this, activityClass)
        );
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (auth != null && db != null
                && tvSoDienThoaiTaiKhoan != null) {
            taiThongTinNguoiDung();
        }
    }
}