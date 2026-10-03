package com.example.autocare.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import java.util.HashMap;
import java.util.Map;
import android.text.Editable;
import android.text.TextWatcher;

public class AdminQuanLyDichVu extends AppCompatActivity {

    private RecyclerView recyclerDichVuAdmin;
    private TextView txtSoLuongDichVuAdmin;
    private Button btnThemDichVu;

    private ArrayList<DocumentSnapshot> danhSachDichVu;

    private AdminDichVuAdapter adapter;

    private FirebaseFirestore firestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_admin_quan_ly_dich_vu
        );


        // =========================
        // FIRESTORE
        // =========================

        firestore =
                FirebaseFirestore.getInstance();


        // =========================
        // ÁNH XẠ VIEW
        // =========================

        recyclerDichVuAdmin =
                findViewById(
                        R.id.recyclerDichVuAdmin
                );

        txtSoLuongDichVuAdmin =
                findViewById(
                        R.id.txtSoLuongDichVuAdmin
                );

        btnThemDichVu =
                findViewById(
                        R.id.btnThemDichVu
                );


        // =========================
        // DANH SÁCH
        // =========================

        danhSachDichVu =
                new ArrayList<>();


        recyclerDichVuAdmin.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new AdminDichVuAdapter(
                        danhSachDichVu
                );


        recyclerDichVuAdmin.setAdapter(
                adapter
        );


        // =========================
        // NÚT THÊM
        // =========================
        btnThemDichVu.setOnClickListener(
                v -> hienThiDialogThemDichVu()
        );


        // =========================
        // TẢI DỊCH VỤ
        // =========================

        taiDanhSachDichVu();
    }


    // =========================================================
    // TẢI TẤT CẢ DỊCH VỤ
    // =========================================================

    private void taiDanhSachDichVu() {

        firestore
                .collection("services")
                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            danhSachDichVu.clear();


                            for (DocumentSnapshot document :
                                    querySnapshot.getDocuments()) {

                                danhSachDichVu.add(
                                        document
                                );
                            }


                            // =========================
                            // SỐ LƯỢNG
                            // =========================

                            txtSoLuongDichVuAdmin.setText(
                                    danhSachDichVu.size()
                                            + " dịch vụ"
                            );


                            // =========================
                            // CẬP NHẬT DANH SÁCH
                            // =========================

                            adapter.notifyDataSetChanged();


                            // =========================
                            // KHÔNG CÓ DỮ LIỆU
                            // =========================

                            if (danhSachDichVu.isEmpty()) {

                                Toast.makeText(
                                        AdminQuanLyDichVu.this,
                                        "Chưa có dịch vụ nào",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }

                        }
                )

                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    AdminQuanLyDichVu.this,
                                    "Không tải được dịch vụ: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        }
                );
    }
    private void hienThiDialogThemDichVu() {

        View view = getLayoutInflater().inflate(
                R.layout.dialog_them_dich_vu,
                null
        );

        EditText edtTenDichVu =
                view.findViewById(R.id.edtTenDichVu);

        EditText edtMaDichVu =
                view.findViewById(R.id.edtMaDichVu);

        EditText edtGiaTien =
                view.findViewById(R.id.edtGiaTien);
        edtGiaTien.addTextChangedListener(new TextWatcher() {

            private boolean dangSua = false;

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (dangSua) {
                    return;
                }

                dangSua = true;

                String text =
                        s.toString().replace(".", "");

                if (!text.isEmpty()) {

                    try {

                        long soTien =
                                Long.parseLong(text);

                        String dinhDang =
                                String.format(
                                        java.util.Locale.US,
                                        "%,d",
                                        soTien
                                ).replace(",", ".");

                        edtGiaTien.setText(
                                dinhDang
                        );

                        edtGiaTien.setSelection(
                                dinhDang.length()
                        );

                    } catch (NumberFormatException e) {

                        // Không làm gì nếu dữ liệu không hợp lệ
                    }
                }

                dangSua = false;
            }
        });

        EditText edtThoiGian =
                view.findViewById(R.id.edtThoiGian);

        EditText edtMoTa =
                view.findViewById(R.id.edtMoTa);

        CheckBox cbHoatDong =
                view.findViewById(R.id.cbHoatDong);


        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setTitle("Thêm dịch vụ")
                        .setView(view)
                        .setPositiveButton(
                                "Thêm",
                                null
                        )
                        .setNegativeButton(
                                "Hủy",
                                null
                        )
                        .create();


        dialog.setOnShowListener(
                dialogInterface -> {

                    Button btnThem =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    btnThem.setOnClickListener(
                            v -> {

                                String ten =
                                        edtTenDichVu.getText()
                                                .toString()
                                                .trim();

                                String maDichVu =
                                        edtMaDichVu.getText()
                                                .toString()
                                                .trim();

                                String giaTien =
                                        edtGiaTien.getText()
                                                .toString()
                                                .trim();

                                String thoiGian =
                                        edtThoiGian.getText()
                                                .toString()
                                                .trim();

                                String moTa =
                                        edtMoTa.getText()
                                                .toString()
                                                .trim();

                                boolean hoatDong =
                                        cbHoatDong.isChecked();


                                // =========================
                                // KIỂM TRA DỮ LIỆU
                                // =========================

                                if (ten.isEmpty()) {

                                    edtTenDichVu.setError(
                                            "Vui lòng nhập tên dịch vụ"
                                    );

                                    edtTenDichVu.requestFocus();

                                    return;
                                }

                                if (maDichVu.isEmpty()) {

                                    edtMaDichVu.setError(
                                            "Vui lòng nhập mã dịch vụ"
                                    );

                                    edtMaDichVu.requestFocus();

                                    return;
                                }

                                if (giaTien.isEmpty()) {

                                    edtGiaTien.setError(
                                            "Vui lòng nhập giá tiền"
                                    );

                                    edtGiaTien.requestFocus();

                                    return;
                                }

                                if (thoiGian.isEmpty()) {

                                    edtThoiGian.setError(
                                            "Vui lòng nhập thời gian"
                                    );

                                    edtThoiGian.requestFocus();

                                    return;
                                }


                                // =========================
                                // TẠO DỮ LIỆU
                                // =========================

                                Map<String, Object> dichVu =
                                        new HashMap<>();

                                dichVu.put(
                                        "name",
                                        ten
                                );

                                dichVu.put(
                                        "dịch vụ",
                                        maDichVu
                                );

                                dichVu.put(
                                        "giá tiền",
                                        giaTien
                                );

                                dichVu.put(
                                        "thời gian",
                                        thoiGian
                                );

                                dichVu.put(
                                        "mô tả",
                                        moTa
                                );

                                dichVu.put(
                                        "hoạt động",
                                        hoatDong
                                );


                                // =========================
                                // LƯU FIRESTORE
                                // =========================

                                firestore
                                        .collection("services")
                                        .document(maDichVu)
                                        .set(dichVu)

                                        .addOnSuccessListener(
                                                documentReference -> {

                                                    Toast.makeText(
                                                            AdminQuanLyDichVu.this,
                                                            "Thêm dịch vụ thành công",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    dialog.dismiss();

                                                    taiDanhSachDichVu();
                                                }
                                        )

                                        .addOnFailureListener(
                                                e -> {

                                                    Toast.makeText(
                                                            AdminQuanLyDichVu.this,
                                                            "Lỗi: "
                                                                    + e.getMessage(),
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                }
                                        );
                            }
                    );
                });


        dialog.show();
    }
}