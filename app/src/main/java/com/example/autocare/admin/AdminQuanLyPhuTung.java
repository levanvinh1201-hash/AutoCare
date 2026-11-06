
package com.example.autocare.admin;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.PhuTung;
import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
public class AdminQuanLyPhuTung extends AppCompatActivity {

    private RecyclerView recyclerPhuTung;
    private TextView txtSoLuongPhuTung;
    private FloatingActionButton btnThemPhuTung;

    private ArrayList<PhuTung> danhSachPhuTung;
    private AdminPhuTungAdapter adapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_quan_ly_phu_tung);

        firestore = FirebaseFirestore.getInstance();

        recyclerPhuTung = findViewById(R.id.recyclerPhuTung);
        txtSoLuongPhuTung = findViewById(R.id.txtSoLuongPhuTung);
        btnThemPhuTung = findViewById(R.id.btnThemPhuTung);

        danhSachPhuTung = new ArrayList<>();

        recyclerPhuTung.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new AdminPhuTungAdapter(danhSachPhuTung);
        recyclerPhuTung.setAdapter(adapter);

        btnThemPhuTung.setOnClickListener(
                v -> hienThiDialogThemPhuTung()
        );

        taiDanhSachPhuTung();
    }

    // =========================================================
    // TẢI DANH SÁCH PHỤ TÙNG
    // ========================================================
    private void taiDanhSachPhuTung() {

        firestore.collection("spare_parts")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachPhuTung.clear();

                    for (var document : querySnapshot) {

                        String phuTungId =
                                document.getString("phuTungId");

                        String ten =
                                document.getString("ten");

                        Object giaObject = document.get("gia");

                        String gia = giaObject != null
                                ? String.valueOf(giaObject)
                                : "0";

                        String hinhAnh =
                                document.getString("hinhAnh");

                        String moTa =
                                document.getString("moTa");

                        Long soLuongLong =
                                document.getLong("soLuong");

                        Boolean conHangBoolean =
                                document.getBoolean("conHang");

                        if (phuTungId == null ||
                                phuTungId.isEmpty()) {
                            phuTungId = document.getId();
                        }

                        if (ten == null) ten = "Chưa cập nhật";
                        if (hinhAnh == null) hinhAnh = "";
                        if (moTa == null) moTa = "";

                        int soLuong = soLuongLong != null
                                ? soLuongLong.intValue()
                                : 0;

                        boolean conHang = conHangBoolean != null
                                ? conHangBoolean
                                : false;

                        if (soLuong > 0) {
                            conHang = true;
                        }

                        PhuTung phuTung = new PhuTung(
                                phuTungId,
                                ten,
                                gia,
                                hinhAnh,
                                moTa,
                                soLuong,
                                conHang
                        );

                        danhSachPhuTung.add(phuTung);
                    }

                    txtSoLuongPhuTung.setText(
                            danhSachPhuTung.size() + " phụ tùng"
                    );

                    adapter.notifyDataSetChanged();

                    if (danhSachPhuTung.isEmpty()) {
                        Toast.makeText(
                                AdminQuanLyPhuTung.this,
                                "Chưa có phụ tùng nào",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                AdminQuanLyPhuTung.this,
                                "Không tải được phụ tùng: "
                                        + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    // =========================================================
    // ĐỊNH DẠNG GIÁ: 400000 -> 400.000
    // =========================================================

    private String dinhDangTien(String gia) {

        if (gia == null || gia.trim().isEmpty()) {
            return "";
        }

        String chuSo = gia.replaceAll("[^0-9]", "");

        if (chuSo.isEmpty()) {
            return "";
        }

        try {
            long soTien = Long.parseLong(chuSo);

            DecimalFormatSymbols symbols =
                    new DecimalFormatSymbols(Locale.US);

            symbols.setGroupingSeparator('.');

            DecimalFormat formatter =
                    new DecimalFormat("#,###", symbols);

            formatter.setGroupingUsed(true);

            return formatter.format(soTien);

        } catch (NumberFormatException e) {
            return chuSo;
        }
    }

    // =========================================================
    // TỰ ĐỘNG ĐỊNH DẠNG GIÁ KHI NHẬP
    // =========================================================

    private void tuDongDinhDangTien(EditText editText) {

        editText.addTextChangedListener(new TextWatcher() {

            private boolean dangSua = false;

            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (dangSua) return;

                dangSua = true;

                String chuSo = s.toString()
                        .replaceAll("[^0-9]", "");

                if (chuSo.isEmpty()) {
                    editText.setText("");
                    dangSua = false;
                    return;
                }

                try {
                    long soTien = Long.parseLong(chuSo);

                    String giaMoi = dinhDangTien(
                            String.valueOf(soTien)
                    );

                    editText.setText(giaMoi);
                    editText.setSelection(editText.length());

                } catch (NumberFormatException e) {
                    // Không thay đổi nếu giá trị vượt giới hạn long.
                }

                dangSua = false;
            }
        });
    }

    // =========================================================
    // DIALOG THÊM PHỤ TÙNG
    // =========================================================

    private void hienThiDialogThemPhuTung() {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_them_phu_tung, null);

        EditText edtTen =
                view.findViewById(R.id.edtTenPhuTung);

        EditText edtGia =
                view.findViewById(R.id.edtGiaPhuTung);

        EditText edtHinhAnh =
                view.findViewById(R.id.edtHinhAnhPhuTung);

        EditText edtMoTa =
                view.findViewById(R.id.edtMoTaPhuTung);

        EditText edtSoLuong =
                view.findViewById(R.id.edtSoLuongPhuTung);

        // BỔ SUNG ĐỊNH DẠNG GIÁ TỰ ĐỘNG
        tuDongDinhDangTien(edtGia);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Thêm phụ tùng")
                .setView(view)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Thêm", null)
                .create();

        dialog.setOnShowListener(d -> {

            Button btnThem = dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnThem.setOnClickListener(v -> {

                String ten = edtTen.getText()
                        .toString().trim();

                // Loại bỏ dấu chấm trước khi lưu
                String gia = edtGia.getText()
                        .toString()
                        .replace(".", "")
                        .trim();

                String hinhAnh = edtHinhAnh.getText()
                        .toString().trim();

                String moTa = edtMoTa.getText()
                        .toString().trim();

                String soLuongText = edtSoLuong.getText()
                        .toString().trim();

                if (ten.isEmpty()) {
                    edtTen.setError("Vui lòng nhập tên phụ tùng");
                    edtTen.requestFocus();
                    return;
                }

                if (gia.isEmpty()) {
                    edtGia.setError("Vui lòng nhập giá");
                    edtGia.requestFocus();
                    return;
                }

                try {
                    Long.parseLong(gia);
                } catch (NumberFormatException e) {
                    edtGia.setError("Giá không hợp lệ");
                    edtGia.requestFocus();
                    return;
                }

                if (soLuongText.isEmpty()) {
                    edtSoLuong.setError("Vui lòng nhập số lượng");
                    edtSoLuong.requestFocus();
                    return;
                }

                int soLuong;

                try {
                    soLuong = Integer.parseInt(soLuongText);
                } catch (NumberFormatException e) {
                    edtSoLuong.setError("Số lượng không hợp lệ");
                    edtSoLuong.requestFocus();
                    return;
                }

                if (soLuong < 0) {
                    edtSoLuong.setError("Số lượng không được âm");
                    edtSoLuong.requestFocus();
                    return;
                }

                boolean conHang = soLuong > 0;

                String phuTungId = firestore
                        .collection("spare_parts")
                        .document()
                        .getId();

                HashMap<String, Object> duLieu = new HashMap<>();

                duLieu.put("phuTungId", phuTungId);
                duLieu.put("ten", ten);
                duLieu.put("gia", gia);
                duLieu.put("hinhAnh", hinhAnh);
                duLieu.put("moTa", moTa);
                duLieu.put("soLuong", soLuong);
                duLieu.put("conHang", conHang);

                btnThem.setEnabled(false);

                firestore.collection("spare_parts")
                        .document(phuTungId)
                        .set(duLieu)
                        .addOnSuccessListener(unused -> {

                            Toast.makeText(
                                    AdminQuanLyPhuTung.this,
                                    "Thêm phụ tùng thành công!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();
                            taiDanhSachPhuTung();
                        })
                        .addOnFailureListener(e -> {

                            btnThem.setEnabled(true);

                            Toast.makeText(
                                    AdminQuanLyPhuTung.this,
                                    "Thêm thất bại: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        });

        dialog.show();
    }
}
