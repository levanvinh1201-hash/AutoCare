package com.example.autocare;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;

import androidx.appcompat.app.AppCompatActivity;

public class XeCuaToi extends AppCompatActivity {

    Button btnThemXe;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xe_cua_toi);
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        hienThiDanhSachXe();

        btnThemXe = findViewById(R.id.btnThemXe);

        btnThemXe.setOnClickListener(v -> {
            hienThiFormThemXe();
        });
    }
    private void luuXe(Dialog dialog) {

        EditText edtHangXe = dialog.findViewById(R.id.edtHangXe);
        EditText edtTenXe = dialog.findViewById(R.id.edtTenXe);
        EditText edtBienSo = dialog.findViewById(R.id.edtBienSo);
        EditText edtNamSanXuat = dialog.findViewById(R.id.edtNamSanXuat);

        String hangXe = edtHangXe.getText().toString().trim();
        String tenXe = edtTenXe.getText().toString().trim();
        String bienSo = edtBienSo.getText().toString().trim();
        String namSanXuat = edtNamSanXuat.getText().toString().trim();

        if (hangXe.isEmpty()) {
            edtHangXe.setError("Vui lòng nhập hãng xe");
            edtHangXe.requestFocus();
            return;
        }

        if (tenXe.isEmpty()) {
            edtTenXe.setError("Vui lòng nhập tên xe");
            edtTenXe.requestFocus();
            return;
        }

        if (bienSo.isEmpty()) {
            edtBienSo.setError("Vui lòng nhập biển số xe");
            edtBienSo.requestFocus();
            return;
        }

        if (namSanXuat.isEmpty()) {
            edtNamSanXuat.setError("Vui lòng nhập năm sản xuất");
            edtNamSanXuat.requestFocus();
            return;
        }

        if (firebaseAuth.getCurrentUser() == null) {
            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        String userId = firebaseAuth
                .getCurrentUser()
                .getUid();

        String vehicleId = UUID.randomUUID().toString();

        Map<String, Object> xe = new HashMap<>();

        xe.put("vehicleId", vehicleId);
        xe.put("userId", userId);
        xe.put("brand", hangXe);
        xe.put("model", tenXe);
        xe.put("licensePlate", bienSo);
        xe.put("year", namSanXuat);

        firestore.collection("vehicles")
                .document(vehicleId)
                .set(xe)
                .addOnSuccessListener(unused -> {

                    Toast.makeText(
                            this,
                            "Thêm xe thành công!",
                            Toast.LENGTH_SHORT
                    ).show();

                    dialog.dismiss();

                    hienThiDanhSachXe();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });

    }
    private void hienThiDanhSachXe() {

        LinearLayout layoutDanhSachXe =
                findViewById(R.id.layoutDanhSachXe);

        TextView tvKhongCoXe =
                findViewById(R.id.tvKhongCoXe);

        TextView tvSoLuongXe =
                findViewById(R.id.tvSoLuongXe);

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String userId = firebaseAuth
                .getCurrentUser()
                .getUid();

        firestore.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutDanhSachXe.removeAllViews();

                    int soLuong = querySnapshot.size();

                    tvSoLuongXe.setText(soLuong + " xe");

                    if (soLuong == 0) {

                        tvKhongCoXe.setVisibility(View.VISIBLE);

                        return;
                    }

                    tvKhongCoXe.setVisibility(View.GONE);

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        String hangXe =
                                document.getString("brand");

                        String tenXe =
                                document.getString("model");

                        String bienSo =
                                document.getString("licensePlate");

                        String nam =
                                document.getString("year");

                        TextView xe = new TextView(this);

                        xe.setText(
                                "🚗  " + hangXe + " " + tenXe
                                        + "\n\n"
                                        + "🪪 Biển số: " + bienSo
                                        + "\n"
                                        + "📅 Năm sản xuất: " + nam
                        );

                        xe.setTextSize(16);
                        xe.setTextColor(Color.DKGRAY);
                        xe.setPadding(24, 24, 24, 24);

                        layoutDanhSachXe.addView(xe);

                        ViewGroup.MarginLayoutParams params =
                                (ViewGroup.MarginLayoutParams)
                                        xe.getLayoutParams();

                        if (params != null) {
                            params.setMargins(0, 0, 0, 16);
                            xe.setLayoutParams(params);
                        }
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Không tải được danh sách xe",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    private void hienThiFormThemXe() {

        Dialog dialog = new Dialog(this);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_them_xe);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        Button btnLuuXe = dialog.findViewById(R.id.btnLuuXe);

        btnLuuXe.setOnClickListener(v -> {

            luuXe(dialog);

        });
        dialog.show();
    }
}