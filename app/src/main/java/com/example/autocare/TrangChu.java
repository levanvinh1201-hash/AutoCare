package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class TrangChu extends AppCompatActivity {

    TextView tvXinChao;

    Button btnDatLich;
    Button btnXeCuaToi;
    Button btnLichSu;
    Button btnChatBot;
    Button btnDangXuat;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trang_chu);

        // Ánh xạ giao diện
        tvXinChao = findViewById(R.id.tvXinChao);

        btnDatLich = findViewById(R.id.btnDatLich);
        btnXeCuaToi = findViewById(R.id.btnXeCuaToi);
        btnLichSu = findViewById(R.id.btnLichSu);
        btnChatBot = findViewById(R.id.btnChatBot);
        btnDangXuat = findViewById(R.id.btnDangXuat);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Lấy tên người dùng
        hienThiTenNguoiDung();

        // Nút Đặt lịch
        btnDatLich.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TrangChu.this,
                    DatLich.class
            );

            startActivity(intent);
        });

        // Nút Xe của tôi
        btnXeCuaToi.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TrangChu.this,
                    XeCuaToi.class
            );

            startActivity(intent);
        });

        // Nút Lịch sử
        btnLichSu.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChu.this, LichSuDatLich.class);
            startActivity(intent);
        });

        // Nút Chatbot
        btnChatBot.setOnClickListener(v -> {
            Toast.makeText(
                    TrangChu.this,
                    "Trợ lý AI đang phát triển",
                    Toast.LENGTH_SHORT
            ).show();
        });

        // Đăng xuất
        btnDangXuat.setOnClickListener(v -> {

            firebaseAuth.signOut();

            Intent intent = new Intent(
                    TrangChu.this,
                    DangNhap.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);

            finish();
        });
    }

    private void hienThiTenNguoiDung() {

        if (firebaseAuth.getCurrentUser() == null) {
            tvXinChao.setText("Xin chào!");
            return;
        }

        String userId = firebaseAuth
                .getCurrentUser()
                .getUid();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String hoTen =
                                documentSnapshot.getString("hoTen");

                        if (hoTen != null && !hoTen.isEmpty()) {

                            tvXinChao.setText(
                                    "Xin chào, " + hoTen + "!"
                            );

                        } else {
                            tvXinChao.setText("Xin chào!");
                        }

                    } else {
                        tvXinChao.setText("Xin chào!");
                    }
                })
                .addOnFailureListener(e -> {

                    tvXinChao.setText("Xin chào!");

                    Toast.makeText(
                            TrangChu.this,
                            "Không tải được thông tin người dùng",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }
}