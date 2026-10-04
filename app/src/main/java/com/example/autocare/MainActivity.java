package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        new android.os.Handler().postDelayed(() -> {

            // Kiểm tra người dùng đã đăng nhập chưa
            if (firebaseAuth.getCurrentUser() != null) {

                String uid =
                        firebaseAuth.getCurrentUser().getUid();

                // Lấy thông tin người dùng từ Firestore
                firestore
                        .collection("users")
                        .document(uid)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {

                            if (documentSnapshot.exists()) {

                                String role =
                                        documentSnapshot.getString("role");

                                // =============================
                                // TÀI KHOẢN ADMIN
                                // =============================

                                if ("admin".equals(role)) {

                                    Intent intent =
                                            new Intent(
                                                    MainActivity.this,
                                                    com.example.autocare.admin.AdminTrangChu.class
                                            );

                                    startActivity(intent);

                                }

                                // =============================
                                // TÀI KHOẢN CUSTOMER
                                // =============================

                                else {

                                    Intent intent =
                                            new Intent(
                                                    MainActivity.this,
                                                    TrangChu.class
                                            );

                                    startActivity(intent);
                                }

                            } else {

                                // Không tìm thấy thông tin tài khoản
                                Intent intent =
                                        new Intent(
                                                MainActivity.this,
                                                DangNhap.class
                                        );

                                startActivity(intent);
                            }

                            finish();
                        })

                        .addOnFailureListener(e -> {

                            // Lỗi đọc Firestore
                            Intent intent =
                                    new Intent(
                                            MainActivity.this,
                                            DangNhap.class
                                    );

                            startActivity(intent);

                            finish();
                        });

            } else {

                // =============================
                // CHƯA ĐĂNG NHẬP
                // =============================

                Intent intent =
                        new Intent(
                                MainActivity.this,
                                DangNhap.class
                        );

                startActivity(intent);

                finish();
            }

        }, 1500);
    }
}