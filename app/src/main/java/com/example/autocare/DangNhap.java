package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.autocare.admin.AdminTrangChu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class DangNhap extends AppCompatActivity {

    EditText edtEmail, edtMatKhau;
    Button btnDangNhap;
    TextView tvDangKy;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dang_nhap);

        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        btnDangNhap = findViewById(R.id.btnDangNhap);
        tvDangKy = findViewById(R.id.tvDangKy);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnDangNhap.setOnClickListener(v -> dangNhap());

        tvDangKy.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DangNhap.this,
                    DangKy.class
            );

            startActivity(intent);
        });
    }

    private void dangNhap() {

        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        if (matKhau.isEmpty()) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        btnDangNhap.setEnabled(false);

        firebaseAuth
                .signInWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {

                    if (task.isSuccessful()) {

                        // Lấy UID của tài khoản vừa đăng nhập
                        String userId = firebaseAuth
                                .getCurrentUser()
                                .getUid();

                        // Đọc thông tin người dùng trong Firestore
                        firestore.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {

                                    btnDangNhap.setEnabled(true);

                                    if (!documentSnapshot.exists()) {

                                        Toast.makeText(
                                                DangNhap.this,
                                                "Không tìm thấy thông tin tài khoản",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        firebaseAuth.signOut();
                                        return;
                                    }

                                    String role = documentSnapshot
                                            .getString("role");

                                    // Nếu là Admin
                                    if ("admin".equalsIgnoreCase(role)) {

                                        Toast.makeText(
                                                DangNhap.this,
                                                "Đăng nhập Admin thành công!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent = new Intent(
                                                DangNhap.this,
                                                AdminTrangChu.class
                                        );

                                        intent.setFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        );

                                        startActivity(intent);
                                        finish();

                                    } else {

                                        // Nếu là Customer
                                        Toast.makeText(
                                                DangNhap.this,
                                                "Đăng nhập thành công!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        Intent intent = new Intent(
                                                DangNhap.this,
                                                TrangChu.class
                                        );

                                        intent.setFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        );

                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e -> {

                                    btnDangNhap.setEnabled(true);

                                    Toast.makeText(
                                            DangNhap.this,
                                            "Không thể tải thông tin tài khoản",
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        btnDangNhap.setEnabled(true);

                        String loi = task.getException() != null
                                ? task.getException().getMessage()
                                : "Đăng nhập thất bại";

                        Toast.makeText(
                                DangNhap.this,
                                loi,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}