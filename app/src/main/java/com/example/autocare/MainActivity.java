package com.example.autocare;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        new android.os.Handler().postDelayed(() -> {

            // Kiểm tra người dùng đã đăng nhập chưa
            if (firebaseAuth.getCurrentUser() != null) {

                // Đã đăng nhập → vào thẳng trang chủ
                Intent intent =
                        new Intent(MainActivity.this, TrangChu.class);

                startActivity(intent);

            } else {

                // Chưa đăng nhập → đi đến đăng nhập
                Intent intent =
                        new Intent(MainActivity.this, DangNhap.class);

                startActivity(intent);
            }

            finish();

        }, 1500);
    }
}