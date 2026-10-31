package com.example.autocare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class KiemTraSoDienThoai {

    public static void kiemTra(Activity activity) {

        FirebaseUser user =
                FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            return;
        }

        FirebaseFirestore db =
                FirebaseFirestore.getInstance();

        db.collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(document -> {

                    if (!document.exists()) {
                        Toast.makeText(
                                activity,
                                "Chưa tìm thấy hồ sơ người dùng",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    String role = document.getString("role");

                    // Chỉ yêu cầu số điện thoại của khách hàng
                    if (!"customer".equals(role)) {
                        return;
                    }

                    String soDienThoai =
                            document.getString("soDienThoai");

                    if (TextUtils.isEmpty(soDienThoai)) {
                        hienThiDialog(activity, user.getUid());
                    }
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            activity,
                            "Không kiểm tra được số điện thoại: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private static void hienThiDialog(
            Activity activity,
            String userId) {

        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        View view = activity.getLayoutInflater()
                .inflate(R.layout.dialog_so_dien_thoai, null);

        EditText edtSoDienThoai =
                view.findViewById(R.id.edtSoDienThoai);

        Button btnLuu =
                view.findViewById(R.id.btnLuuSoDienThoai);

        AlertDialog dialog =
                new AlertDialog.Builder(activity)
                        .setView(view)
                        .setCancelable(false)
                        .create();

        btnLuu.setOnClickListener(v -> {

            String sdt = edtSoDienThoai
                    .getText()
                    .toString()
                    .trim();

            // Kiểm tra số điện thoại Việt Nam
            if (!sdt.matches("^0[0-9]{9}$")) {

                edtSoDienThoai.setError(
                        "Vui lòng nhập số điện thoại gồm 10 chữ số, bắt đầu bằng 0"
                );

                edtSoDienThoai.requestFocus();
                return;
            }

            btnLuu.setEnabled(false);

            Map<String, Object> data = new HashMap<>();

            data.put("soDienThoai", sdt);

            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(userId)
                    .update(data)
                    .addOnSuccessListener(unused -> {

                        if (activity.isFinishing()
                                || activity.isDestroyed()) {
                            return;
                        }

                        Toast.makeText(
                                activity,
                                "Đã lưu số điện thoại thành công!",
                                Toast.LENGTH_SHORT
                        ).show();

                        dialog.dismiss();
                    })
                    .addOnFailureListener(e -> {

                        if (activity.isFinishing()
                                || activity.isDestroyed()) {
                            return;
                        }

                        btnLuu.setEnabled(true);

                        Toast.makeText(
                                activity,
                                "Lưu thất bại: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    });
        });

        dialog.show();
    }
}