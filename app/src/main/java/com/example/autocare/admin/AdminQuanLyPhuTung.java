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

import com.example.autocare.PhuTung;
import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminQuanLyPhuTung extends AppCompatActivity {

    private RecyclerView recyclerPhuTung;
    private TextView txtSoLuongPhuTung;
    private Button btnThemPhuTung;

    private ArrayList<PhuTung> danhSachPhuTung;

    private AdminPhuTungAdapter adapter;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_quan_ly_phu_tung
        );

        // =========================
        // FIRESTORE
        // =========================

        firestore =
                FirebaseFirestore.getInstance();


        // =========================
        // ÁNH XẠ VIEW
        // =========================

        recyclerPhuTung =
                findViewById(
                        R.id.recyclerPhuTung
                );

        txtSoLuongPhuTung =
                findViewById(
                        R.id.txtSoLuongPhuTung
                );

        btnThemPhuTung =
                findViewById(
                        R.id.btnThemPhuTung
                );


        // =========================
        // DANH SÁCH
        // =========================

        danhSachPhuTung =
                new ArrayList<>();


        // =========================
        // RECYCLERVIEW
        // =========================

        recyclerPhuTung.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new AdminPhuTungAdapter(
                        danhSachPhuTung
                );


        recyclerPhuTung.setAdapter(
                adapter
        );


        // =========================
        // NÚT THÊM
        // =========================

        btnThemPhuTung.setOnClickListener(
                v -> hienThiDialogThemPhuTung()
        );


        // =========================
        // TẢI DỮ LIỆU
        // =========================

        taiDanhSachPhuTung();
    }


    // =========================================================
    // TẢI DANH SÁCH PHỤ TÙNG
    // =========================================================

    private void taiDanhSachPhuTung() {

        firestore
                .collection("spare_parts")
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            danhSachPhuTung.clear();


                            for (var document :
                                    querySnapshot) {

                                String phuTungId =
                                        document.getString(
                                                "phuTungId"
                                        );

                                String ten =
                                        document.getString(
                                                "ten"
                                        );

                                String gia =
                                        document.getString(
                                                "gia"
                                        );

                                String hinhAnh =
                                        document.getString(
                                                "hinhAnh"
                                        );

                                String moTa =
                                        document.getString(
                                                "moTa"
                                        );

                                Long soLuongLong =
                                        document.getLong(
                                                "soLuong"
                                        );

                                Boolean conHangBoolean =
                                        document.getBoolean(
                                                "conHang"
                                        );


                                // =========================
                                // GIÁ TRỊ MẶC ĐỊNH
                                // =========================

                                if (phuTungId == null ||
                                        phuTungId.isEmpty()) {

                                    phuTungId =
                                            document.getId();
                                }

                                if (ten == null) {
                                    ten = "Chưa cập nhật";
                                }

                                if (gia == null) {
                                    gia = "0";
                                }

                                if (hinhAnh == null) {
                                    hinhAnh = "";
                                }

                                if (moTa == null) {
                                    moTa = "";
                                }

                                int soLuong = 0;

                                if (soLuongLong != null) {
                                    soLuong =
                                            soLuongLong.intValue();
                                }

                                boolean conHang = false;

                                if (conHangBoolean != null) {
                                    conHang =
                                            conHangBoolean;
                                }


                                // Nếu số lượng > 0
                                // thì tự động coi là còn hàng

                                if (soLuong > 0) {
                                    conHang = true;
                                }


                                // =========================
                                // TẠO ĐỐI TƯỢNG
                                // =========================

                                PhuTung phuTung =
                                        new PhuTung(
                                                phuTungId,
                                                ten,
                                                gia,
                                                hinhAnh,
                                                moTa,
                                                soLuong,
                                                conHang
                                        );


                                danhSachPhuTung.add(
                                        phuTung
                                );
                            }


                            // =========================
                            // SỐ LƯỢNG
                            // =========================

                            txtSoLuongPhuTung.setText(
                                    danhSachPhuTung.size()
                                            + " phụ tùng"
                            );


                            // =========================
                            // CẬP NHẬT LIST
                            // =========================

                            adapter.notifyDataSetChanged();


                            // =========================
                            // KHÔNG CÓ DỮ LIỆU
                            // =========================

                            if (danhSachPhuTung.isEmpty()) {

                                Toast.makeText(
                                        AdminQuanLyPhuTung.this,
                                        "Chưa có phụ tùng nào",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                )

                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    AdminQuanLyPhuTung.this,
                                    "Không tải được phụ tùng: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
    }


    // =========================================================
    // DIALOG THÊM PHỤ TÙNG
    // =========================================================

    private void hienThiDialogThemPhuTung() {

        View view =
                LayoutInflater.from(this)
                        .inflate(
                                R.layout.dialog_them_phu_tung,
                                null
                        );


        EditText edtTen =
                view.findViewById(
                        R.id.edtTenPhuTung
                );

        EditText edtGia =
                view.findViewById(
                        R.id.edtGiaPhuTung
                );

        EditText edtHinhAnh =
                view.findViewById(
                        R.id.edtHinhAnhPhuTung
                );

        EditText edtMoTa =
                view.findViewById(
                        R.id.edtMoTaPhuTung
                );

        EditText edtSoLuong =
                view.findViewById(
                        R.id.edtSoLuongPhuTung
                );


        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Thêm phụ tùng")
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


        dialog.setOnShowListener(
                d -> {

                    Button btnThem =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );


                    btnThem.setOnClickListener(
                            v -> {

                                String ten =
                                        edtTen
                                                .getText()
                                                .toString()
                                                .trim();

                                String gia =
                                        edtGia
                                                .getText()
                                                .toString()
                                                .trim();

                                String hinhAnh =
                                        edtHinhAnh
                                                .getText()
                                                .toString()
                                                .trim();

                                String moTa =
                                        edtMoTa
                                                .getText()
                                                .toString()
                                                .trim();

                                String soLuongText =
                                        edtSoLuong
                                                .getText()
                                                .toString()
                                                .trim();


                                // =========================
                                // KIỂM TRA
                                // =========================

                                if (ten.isEmpty()) {

                                    edtTen.setError(
                                            "Vui lòng nhập tên phụ tùng"
                                    );

                                    edtTen.requestFocus();

                                    return;
                                }


                                if (gia.isEmpty()) {

                                    edtGia.setError(
                                            "Vui lòng nhập giá"
                                    );

                                    edtGia.requestFocus();

                                    return;
                                }


                                if (soLuongText.isEmpty()) {

                                    edtSoLuong.setError(
                                            "Vui lòng nhập số lượng"
                                    );

                                    edtSoLuong.requestFocus();

                                    return;
                                }


                                int soLuong;

                                try {

                                    soLuong =
                                            Integer.parseInt(
                                                    soLuongText
                                            );

                                } catch (Exception e) {

                                    edtSoLuong.setError(
                                            "Số lượng không hợp lệ"
                                    );

                                    edtSoLuong.requestFocus();

                                    return;
                                }


                                if (soLuong < 0) {

                                    edtSoLuong.setError(
                                            "Số lượng không được âm"
                                    );

                                    edtSoLuong.requestFocus();

                                    return;
                                }


                                // =========================
                                // TRẠNG THÁI
                                // =========================

                                boolean conHang =
                                        soLuong > 0;


                                // =========================
                                // ID
                                // =========================

                                String phuTungId =
                                        firestore
                                                .collection(
                                                        "spare_parts"
                                                )
                                                .document()
                                                .getId();


                                // =========================
                                // DỮ LIỆU
                                // =========================

                                HashMap<String, Object>
                                        duLieu =
                                        new HashMap<>();


                                duLieu.put(
                                        "phuTungId",
                                        phuTungId
                                );

                                duLieu.put(
                                        "ten",
                                        ten
                                );

                                duLieu.put(
                                        "gia",
                                        gia
                                );

                                duLieu.put(
                                        "hinhAnh",
                                        hinhAnh
                                );

                                duLieu.put(
                                        "moTa",
                                        moTa
                                );

                                duLieu.put(
                                        "soLuong",
                                        soLuong
                                );

                                duLieu.put(
                                        "conHang",
                                        conHang
                                );


                                btnThem.setEnabled(false);


                                // =========================
                                // LƯU FIRESTORE
                                // =========================

                                firestore
                                        .collection(
                                                "spare_parts"
                                        )
                                        .document(
                                                phuTungId
                                        )
                                        .set(duLieu)

                                        .addOnSuccessListener(
                                                unused -> {

                                                    Toast.makeText(
                                                            AdminQuanLyPhuTung.this,
                                                            "Thêm phụ tùng thành công!",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    dialog.dismiss();

                                                    taiDanhSachPhuTung();
                                                }
                                        )

                                        .addOnFailureListener(
                                                e -> {

                                                    btnThem.setEnabled(
                                                            true
                                                    );

                                                    Toast.makeText(
                                                            AdminQuanLyPhuTung.this,
                                                            "Thêm thất bại: "
                                                                    + e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show();
                                                }
                                        );
                            }
                    );
                }
        );


        dialog.show();
    }
}