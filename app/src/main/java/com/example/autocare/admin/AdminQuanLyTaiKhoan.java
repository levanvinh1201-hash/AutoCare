package com.example.autocare.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.example.autocare.TaiKhoan;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminQuanLyTaiKhoan extends AppCompatActivity {

    private RecyclerView recyclerTaiKhoan;
    private TextView txtSoLuongTaiKhoan;
    private Button btnThemTaiKhoan;

    private ArrayList<TaiKhoan> danhSachTaiKhoan;

    private TaiKhoanAdapter adapter;

    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_quan_ly_tai_khoan
        );

        // ================= FIREBASE =================

        firestore =
                FirebaseFirestore.getInstance();

        firebaseAuth =
                FirebaseAuth.getInstance();

        // ================= ÁNH XẠ VIEW =================

        recyclerTaiKhoan =
                findViewById(
                        R.id.recyclerTaiKhoan
                );

        txtSoLuongTaiKhoan =
                findViewById(
                        R.id.txtSoLuongTaiKhoan
                );

        btnThemTaiKhoan =
                findViewById(
                        R.id.btnThemTaiKhoan
                );

        btnThemTaiKhoan.setOnClickListener(
                v -> hienThiDialogThemTaiKhoan()
        );

        // ================= DANH SÁCH =================

        danhSachTaiKhoan =
                new ArrayList<>();

        // ================= RECYCLER VIEW =================

        recyclerTaiKhoan.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter =
                new TaiKhoanAdapter(
                        danhSachTaiKhoan
                );

        recyclerTaiKhoan.setAdapter(
                adapter
        );

        // ================= LOAD TÀI KHOẢN =================

        taiDanhSachTaiKhoan();
    }

    private void taiDanhSachTaiKhoan() {

        firestore
                .collection("users")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachTaiKhoan.clear();

                    for (var document : querySnapshot) {

                        String userId =
                                document.getString("userId");

                        String hoTen =
                                document.getString("hoTen");

                        String email =
                                document.getString("email");

                        String role =
                                document.getString("role");

                        if (userId == null) {
                            userId = document.getId();
                        }

                        if (hoTen == null) {
                            hoTen = "Chưa cập nhật";
                        }

                        if (email == null) {
                            email = "Chưa có email";
                        }

                        if (role == null) {
                            role = "customer";
                        }

                        TaiKhoan taiKhoan =
                                new TaiKhoan(
                                        userId,
                                        hoTen,
                                        email,
                                        role
                                );

                        danhSachTaiKhoan.add(
                                taiKhoan
                        );
                    }

                    // Cập nhật số lượng

                    txtSoLuongTaiKhoan.setText(
                            danhSachTaiKhoan.size()
                                    + " tài khoản"
                    );

                    // Cập nhật RecyclerView

                    adapter.notifyDataSetChanged();

                    // Không có tài khoản

                    if (danhSachTaiKhoan.isEmpty()) {

                        Toast.makeText(
                                AdminQuanLyTaiKhoan.this,
                                "Chưa có tài khoản nào",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            AdminQuanLyTaiKhoan.this,
                            "Không tải được tài khoản: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void hienThiDialogThemTaiKhoan() {

        View view =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_them_tai_khoan,
                                null
                        );

        EditText edtHoTen =
                view.findViewById(
                        R.id.edtThemHoTen
                );

        EditText edtEmail =
                view.findViewById(
                        R.id.edtThemEmail
                );

        EditText edtMatKhau =
                view.findViewById(
                        R.id.edtThemMatKhau
                );

        Spinner spinnerRole =
                view.findViewById(
                        R.id.spinnerRole
                );

        // ================= QUYỀN TÀI KHOẢN =================

        String[] danhSachRole = {
                "customer",
                "admin"
        };

        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        danhSachRole
                );

        roleAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );

        spinnerRole.setAdapter(
                roleAdapter
        );

        // ================= DIALOG =================

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setNegativeButton(
                                "Hủy",
                                null
                        )
                        .setPositiveButton(
                                "Thêm",
                                null
                        )
                        .create();

        dialog.setOnShowListener(d -> {

            Button btnThem =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );

            btnThem.setOnClickListener(v -> {

                String hoTen =
                        edtHoTen
                                .getText()
                                .toString()
                                .trim();

                String email =
                        edtEmail
                                .getText()
                                .toString()
                                .trim();

                String matKhau =
                        edtMatKhau
                                .getText()
                                .toString()
                                .trim();

                String role =
                        spinnerRole
                                .getSelectedItem()
                                .toString();

                // ================= KIỂM TRA DỮ LIỆU =================

                if (hoTen.isEmpty()) {

                    edtHoTen.setError(
                            "Vui lòng nhập họ tên"
                    );

                    edtHoTen.requestFocus();

                    return;
                }

                if (email.isEmpty()) {

                    edtEmail.setError(
                            "Vui lòng nhập email"
                    );

                    edtEmail.requestFocus();

                    return;
                }

                if (matKhau.isEmpty()) {

                    edtMatKhau.setError(
                            "Vui lòng nhập mật khẩu"
                    );

                    edtMatKhau.requestFocus();

                    return;
                }

                if (matKhau.length() < 6) {

                    edtMatKhau.setError(
                            "Mật khẩu phải có ít nhất 6 ký tự"
                    );

                    edtMatKhau.requestFocus();

                    return;
                }

                // ================= TẠO FIREBASE AUTH =================

                btnThem.setEnabled(false);

                firebaseAuth
                        .createUserWithEmailAndPassword(
                                email,
                                matKhau
                        )
                        .addOnSuccessListener(
                                authResult -> {

                                    if (authResult.getUser() == null) {

                                        btnThem.setEnabled(true);

                                        Toast.makeText(
                                                AdminQuanLyTaiKhoan.this,
                                                "Không tạo được tài khoản",
                                                Toast.LENGTH_LONG
                                        ).show();

                                        return;
                                    }

                                    String userId =
                                            authResult
                                                    .getUser()
                                                    .getUid();

                                    // ================= TẠO DỮ LIỆU FIRESTORE =================

                                    HashMap<String, Object> taiKhoan =
                                            new HashMap<>();

                                    taiKhoan.put(
                                            "userId",
                                            userId
                                    );

                                    taiKhoan.put(
                                            "hoTen",
                                            hoTen
                                    );

                                    taiKhoan.put(
                                            "email",
                                            email
                                    );

                                    taiKhoan.put(
                                            "role",
                                            role
                                    );

                                    firestore
                                            .collection("users")
                                            .document(userId)
                                            .set(taiKhoan)
                                            .addOnSuccessListener(
                                                    unused -> {

                                                        btnThem.setEnabled(
                                                                true
                                                        );

                                                        Toast.makeText(
                                                                AdminQuanLyTaiKhoan.this,
                                                                "Tạo tài khoản thành công!",
                                                                Toast.LENGTH_SHORT
                                                        ).show();

                                                        dialog.dismiss();

                                                        taiDanhSachTaiKhoan();
                                                    }
                                            )
                                            .addOnFailureListener(
                                                    e -> {

                                                        btnThem.setEnabled(
                                                                true
                                                        );

                                                        Toast.makeText(
                                                                AdminQuanLyTaiKhoan.this,
                                                                "Tạo tài khoản Firebase thành công nhưng lưu Firestore thất bại: "
                                                                        + e.getMessage(),
                                                                Toast.LENGTH_LONG
                                                        ).show();
                                                    }
                                            );
                                }
                        )
                        .addOnFailureListener(
                                e -> {

                                    btnThem.setEnabled(true);

                                    String loi =
                                            e.getMessage();

                                    if (loi == null) {
                                        loi =
                                                "Không thể tạo tài khoản";
                                    }

                                    Toast.makeText(
                                            AdminQuanLyTaiKhoan.this,
                                            loi,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                        );
            });
        });

        dialog.show();
    }
}