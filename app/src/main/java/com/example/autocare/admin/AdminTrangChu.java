package com.example.autocare.admin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocare.DangNhap;
import com.example.autocare.R;
import com.google.firebase.auth.FirebaseAuth;

public class AdminTrangChu extends AppCompatActivity {

    private Button btnQuanLyLich;
    private Button btnQuanLyDichVu;
    private Button btnQuanLyTaiKhoan;
    private Button btnAdminDangXuat;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_admin_trang_chu);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();

        // Ánh xạ Button
        btnQuanLyLich =
                findViewById(R.id.btnQuanLyLich);

        btnQuanLyDichVu =
                findViewById(R.id.btnQuanLyDichVu);

        btnQuanLyTaiKhoan =
                findViewById(R.id.btnQuanLyTaiKhoan);

        btnAdminDangXuat =
                findViewById(R.id.btnAdminDangXuat);


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

            // Sẽ làm ở bước tiếp theo

        });


        // =============================
        // QUẢN LÝ DỊCH VỤ
        // =============================

        btnQuanLyDichVu.setOnClickListener(v -> {

            // Sẽ làm sau

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
}