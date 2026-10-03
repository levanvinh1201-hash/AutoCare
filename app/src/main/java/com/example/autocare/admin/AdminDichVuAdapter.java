package com.example.autocare.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdminDichVuAdapter
        extends RecyclerView.Adapter<AdminDichVuAdapter.DichVuViewHolder> {

    private ArrayList<DocumentSnapshot> danhSachDichVu;

    private FirebaseFirestore firestore;


    public AdminDichVuAdapter(
            ArrayList<DocumentSnapshot> danhSachDichVu) {

        this.danhSachDichVu = danhSachDichVu;

        firestore =
                FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public DichVuViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_dich_vu,
                                parent,
                                false
                        );

        return new DichVuViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull DichVuViewHolder holder,
            int position) {

        DocumentSnapshot document =
                danhSachDichVu.get(position);


        // =========================
        // LẤY DỮ LIỆU
        // =========================

        String ten =
                document.getString("name");

        String maDichVu =
                document.getString("dịch vụ");

        String giaTien =
                document.getString("giá tiền");

        String thoiGian =
                document.getString("thời gian");

        String moTa =
                document.getString("mô tả");

        Boolean hoatDongValue =
                document.getBoolean("hoạt động");


        // =========================
        // GIÁ TRỊ MẶC ĐỊNH
        // =========================

        if (ten == null || ten.isEmpty()) {
            ten = "Chưa có tên";
        }

        if (maDichVu == null || maDichVu.isEmpty()) {
            maDichVu = document.getId();
        }

        if (giaTien == null || giaTien.isEmpty()) {
            giaTien = "Chưa cập nhật";
        }

        if (thoiGian == null || thoiGian.isEmpty()) {
            thoiGian = "Chưa cập nhật";
        }

        if (moTa == null || moTa.isEmpty()) {
            moTa = "Không có mô tả";
        }


        // =========================
        // HIỂN THỊ
        // =========================

        holder.txtAdminTenDichVu.setText(
                ten
        );

        holder.txtAdminMaDichVu.setText(
                "Mã dịch vụ: " + maDichVu
        );

        holder.txtAdminGiaDichVu.setText(
                "Giá: " + giaTien + " VNĐ"
        );

        holder.txtAdminThoiGianDichVu.setText(
                "Thời gian: " + thoiGian
        );

        holder.txtAdminMoTaDichVu.setText(
                moTa
        );


        // =========================
        // TRẠNG THÁI
        // =========================

        if (hoatDongValue == null) {

            holder.txtAdminHoatDongDichVu.setText(
                    "CHƯA CẬP NHẬT"
            );

        } else if (hoatDongValue) {

            holder.txtAdminHoatDongDichVu.setText(
                    "ĐANG HOẠT ĐỘNG"
            );

        } else {

            holder.txtAdminHoatDongDichVu.setText(
                    "NGỪNG HOẠT ĐỘNG"
            );
        }


        // =========================
        // NÚT SỬA
        // =========================

        holder.btnSuaDichVu.setOnClickListener(
                v -> {

                    hienThiDialogSuaDichVu(
                            v.getContext(),
                            document
                    );

                }
        );


        // =========================
        // NÚT XÓA
        // =========================

        holder.btnXoaDichVu.setOnClickListener(
                v -> {

                    hienThiDialogXoaDichVu(
                            v.getContext(),
                            document,
                            holder.getBindingAdapterPosition()
                    );

                }
        );
    }


    // =========================================================
    // DIALOG SỬA DỊCH VỤ
    // =========================================================

    private void hienThiDialogSuaDichVu(
            Context context,
            DocumentSnapshot document) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.dialog_them_dich_vu,
                                null
                        );


        EditText edtTenDichVu =
                view.findViewById(
                        R.id.edtTenDichVu
                );

        EditText edtMaDichVu =
                view.findViewById(
                        R.id.edtMaDichVu
                );

        EditText edtGiaTien =
                view.findViewById(
                        R.id.edtGiaTien
                );

        EditText edtThoiGian =
                view.findViewById(
                        R.id.edtThoiGian
                );

        EditText edtMoTa =
                view.findViewById(
                        R.id.edtMoTa
                );

        CheckBox cbHoatDong =
                view.findViewById(
                        R.id.cbHoatDong
                );


        // =========================
        // ĐIỀN DỮ LIỆU CŨ
        // =========================

        edtTenDichVu.setText(
                document.getString("name")
        );

        edtMaDichVu.setText(
                document.getString("dịch vụ")
        );

        edtGiaTien.setText(
                document.getString("giá tiền")
        );

        edtThoiGian.setText(
                document.getString("thời gian")
        );

        edtMoTa.setText(
                document.getString("mô tả")
        );


        Boolean hoatDong =
                document.getBoolean("hoạt động");

        if (hoatDong != null) {

            cbHoatDong.setChecked(
                    hoatDong
            );
        }


        // =========================
        // DIALOG
        // =========================

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle("Sửa dịch vụ")
                        .setView(view)
                        .setPositiveButton(
                                "Lưu",
                                null
                        )
                        .setNegativeButton(
                                "Hủy",
                                null
                        )
                        .create();


        dialog.setOnShowListener(
                dialogInterface -> {

                    Button btnLuu =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );


                    btnLuu.setOnClickListener(
                            v -> {

                                String ten =
                                        edtTenDichVu
                                                .getText()
                                                .toString()
                                                .trim();

                                String maDichVu =
                                        edtMaDichVu
                                                .getText()
                                                .toString()
                                                .trim();

                                String giaTien =
                                        edtGiaTien
                                                .getText()
                                                .toString()
                                                .trim();

                                String thoiGian =
                                        edtThoiGian
                                                .getText()
                                                .toString()
                                                .trim();

                                String moTa =
                                        edtMoTa
                                                .getText()
                                                .toString()
                                                .trim();

                                boolean trangThai =
                                        cbHoatDong
                                                .isChecked();


                                // =========================
                                // KIỂM TRA
                                // =========================

                                if (ten.isEmpty()) {

                                    edtTenDichVu.setError(
                                            "Vui lòng nhập tên dịch vụ"
                                    );

                                    return;
                                }

                                if (maDichVu.isEmpty()) {

                                    edtMaDichVu.setError(
                                            "Vui lòng nhập mã dịch vụ"
                                    );

                                    return;
                                }

                                if (giaTien.isEmpty()) {

                                    edtGiaTien.setError(
                                            "Vui lòng nhập giá tiền"
                                    );

                                    return;
                                }

                                if (thoiGian.isEmpty()) {

                                    edtThoiGian.setError(
                                            "Vui lòng nhập thời gian"
                                    );

                                    return;
                                }


                                // =========================
                                // DỮ LIỆU MỚI
                                // =========================

                                java.util.Map<String, Object>
                                        dichVu =
                                        new java.util.HashMap<>();


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
                                        trangThai
                                );


                                // =========================
                                // CẬP NHẬT FIRESTORE
                                // =========================

                                firestore
                                        .collection("services")
                                        .document(
                                                document.getId()
                                        )
                                        .update(
                                                dichVu
                                        )

                                        .addOnSuccessListener(
                                                unused -> {

                                                    Toast.makeText(
                                                            context,
                                                            "Cập nhật dịch vụ thành công",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    int position =
                                                            timViTri(
                                                                    document.getId()
                                                            );

                                                    if (position != -1) {

                                                        // Load lại dữ liệu
                                                        firestore
                                                                .collection("services")
                                                                .document(
                                                                        document.getId()
                                                                )
                                                                .get()
                                                                .addOnSuccessListener(
                                                                        newDocument -> {

                                                                            danhSachDichVu.set(
                                                                                    position,
                                                                                    newDocument
                                                                            );

                                                                            notifyItemChanged(
                                                                                    position
                                                                            );

                                                                        }
                                                                );
                                                    }

                                                    dialog.dismiss();
                                                }
                                        )

                                        .addOnFailureListener(
                                                e -> {

                                                    Toast.makeText(
                                                            context,
                                                            "Lỗi: "
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


    // =========================================================
    // TÌM VỊ TRÍ DỊCH VỤ
    // =========================================================

    private int timViTri(
            String documentId) {

        for (int i = 0;
             i < danhSachDichVu.size();
             i++) {

            if (danhSachDichVu
                    .get(i)
                    .getId()
                    .equals(documentId)) {

                return i;
            }
        }

        return -1;
    }


    // =========================================================
    // DIALOG XÓA DỊCH VỤ
    // =========================================================

    private void hienThiDialogXoaDichVu(
            Context context,
            DocumentSnapshot document,
            int position) {

        String ten =
                document.getString("name");

        if (ten == null || ten.isEmpty()) {
            ten = "dịch vụ này";
        }


        new AlertDialog.Builder(context)

                .setTitle("Xóa dịch vụ")

                .setMessage(
                        "Bạn có chắc muốn xóa \""
                                + ten
                                + "\" không?"
                )

                .setPositiveButton(
                        "Xóa",
                        (dialog, which) -> {

                            firestore
                                    .collection("services")
                                    .document(
                                            document.getId()
                                    )
                                    .delete()

                                    .addOnSuccessListener(
                                            unused -> {

                                                Toast.makeText(
                                                        context,
                                                        "Xóa dịch vụ thành công",
                                                        Toast.LENGTH_SHORT
                                                ).show();


                                                if (position >= 0
                                                        && position
                                                        < danhSachDichVu.size()) {

                                                    danhSachDichVu.remove(
                                                            position
                                                    );

                                                    notifyItemRemoved(
                                                            position
                                                    );

                                                    notifyItemRangeChanged(
                                                            position,
                                                            danhSachDichVu.size()
                                                                    - position
                                                    );
                                                }
                                            }
                                    )

                                    .addOnFailureListener(
                                            e -> {

                                                Toast.makeText(
                                                        context,
                                                        "Không thể xóa: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();

                                            }
                                    );
                        }
                )

                .setNegativeButton(
                        "Hủy",
                        null
                )

                .show();
    }


    // =========================================================
    // GET ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return danhSachDichVu.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class DichVuViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAdminTenDichVu;
        TextView txtAdminMaDichVu;
        TextView txtAdminGiaDichVu;
        TextView txtAdminThoiGianDichVu;
        TextView txtAdminMoTaDichVu;
        TextView txtAdminHoatDongDichVu;

        Button btnSuaDichVu;
        Button btnXoaDichVu;


        public DichVuViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtAdminTenDichVu =
                    itemView.findViewById(
                            R.id.txtAdminTenDichVu
                    );

            txtAdminMaDichVu =
                    itemView.findViewById(
                            R.id.txtAdminMaDichVu
                    );

            txtAdminGiaDichVu =
                    itemView.findViewById(
                            R.id.txtAdminGiaDichVu
                    );

            txtAdminThoiGianDichVu =
                    itemView.findViewById(
                            R.id.txtAdminThoiGianDichVu
                    );

            txtAdminMoTaDichVu =
                    itemView.findViewById(
                            R.id.txtAdminMoTaDichVu
                    );

            txtAdminHoatDongDichVu =
                    itemView.findViewById(
                            R.id.txtAdminHoatDongDichVu
                    );

            btnSuaDichVu =
                    itemView.findViewById(
                            R.id.btnSuaDichVu
                    );

            btnXoaDichVu =
                    itemView.findViewById(
                            R.id.btnXoaDichVu
                    );
        }
    }
}