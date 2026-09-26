package com.example.autocare;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;



public class DangKy extends AppCompatActivity {

    EditText edtHoTen, edtEmail, edtMatKhau, edtXacNhanMatKhau;
    Button btnDangKy;
    TextView tvDangNhap;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dang_ky);

        edtHoTen = findViewById(R.id.edtHoTen);
        edtEmail = findViewById(R.id.edtEmail);
        edtMatKhau = findViewById(R.id.edtMatKhau);
        edtXacNhanMatKhau = findViewById(R.id.edtXacNhanMatKhau);

        btnDangKy = findViewById(R.id.btnDangKy);
        tvDangNhap = findViewById(R.id.tvDangNhap);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnDangKy.setOnClickListener(v -> dangKyTaiKhoan());

        tvDangNhap.setOnClickListener(v -> {
            finish();
        });
    }

    private void dangKyTaiKhoan() {

        String hoTen = edtHoTen.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String matKhau = edtMatKhau.getText().toString().trim();
        String xacNhanMatKhau =
                edtXacNhanMatKhau.getText().toString().trim();

        // Kiểm tra họ tên
        if (TextUtils.isEmpty(hoTen)) {
            edtHoTen.setError("Vui lòng nhập họ tên");
            edtHoTen.requestFocus();
            return;
        }

        // Kiểm tra email
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }

        // Kiểm tra mật khẩu
        if (TextUtils.isEmpty(matKhau)) {
            edtMatKhau.setError("Vui lòng nhập mật khẩu");
            edtMatKhau.requestFocus();
            return;
        }

        if (matKhau.length() < 6) {
            edtMatKhau.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtMatKhau.requestFocus();
            return;
        }

        // Kiểm tra xác nhận mật khẩu
        if (TextUtils.isEmpty(xacNhanMatKhau)) {
            edtXacNhanMatKhau.setError(
                    "Vui lòng xác nhận mật khẩu"
            );
            edtXacNhanMatKhau.requestFocus();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            edtXacNhanMatKhau.setError(
                    "Mật khẩu không khớp"
            );
            edtXacNhanMatKhau.requestFocus();
            return;
        }

        // Đăng ký Firebase
        btnDangKy.setEnabled(false);

        firebaseAuth
                .createUserWithEmailAndPassword(email, matKhau)
                .addOnCompleteListener(this, task -> {

                    btnDangKy.setEnabled(true);
                    if (task.isSuccessful()) {

                        String userId = firebaseAuth.getCurrentUser().getUid();

                        Map<String, Object> nguoiDung = new HashMap<>();
                        nguoiDung.put("userId", userId);
                        nguoiDung.put("hoTen", hoTen);
                        nguoiDung.put("email", email);
                        nguoiDung.put("role", "customer");

                        firestore.collection("users")
                                .document(userId)
                                .set(nguoiDung)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(
                                            DangKy.this,
                                            "Đăng ký tài khoản thành công!",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    finish();
                                })
                                .addOnFailureListener(e -> {

                                    Toast.makeText(
                                            DangKy.this,
                                            "Lưu thông tin thất bại: "
                                                    + e.getMessage(),
                                            Toast.LENGTH_LONG
                                    ).show();
                                });

                    } else {

                        String loi = task.getException() != null
                                ? task.getException().getMessage()
                                : "Đăng ký thất bại";

                        Toast.makeText(
                                DangKy.this,
                                loi,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }
}