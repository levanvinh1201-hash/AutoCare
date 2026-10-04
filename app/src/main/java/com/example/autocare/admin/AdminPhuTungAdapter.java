package com.example.autocare.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.PhuTung;
import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminPhuTungAdapter
        extends RecyclerView.Adapter<AdminPhuTungAdapter.PhuTungViewHolder> {

    private final ArrayList<PhuTung> danhSachPhuTung;
    private final FirebaseFirestore firestore;
    private final Context context;

    public AdminPhuTungAdapter(
            ArrayList<PhuTung> danhSachPhuTung) {

        this.danhSachPhuTung = danhSachPhuTung;
        this.firestore = FirebaseFirestore.getInstance();
        this.context = null;
    }

    @NonNull
    @Override
    public PhuTungViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_phu_tung,
                                parent,
                                false
                        );

        return new PhuTungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PhuTungViewHolder holder,
            int position) {

        PhuTung phuTung =
                danhSachPhuTung.get(position);

        // =====================================================
        // TÊN
        // =====================================================

        String ten = phuTung.getTen();

        if (ten == null || ten.isEmpty()) {
            ten = "Chưa cập nhật";
        }

        holder.txtAdminTenPhuTung.setText(ten);

        // =====================================================
        // GIÁ
        // =====================================================

        String gia = phuTung.getGia();

        if (gia == null || gia.isEmpty()) {
            gia = "0";
        }

        holder.txtAdminGiaPhuTung.setText(
                gia + "đ"
        );

        // =====================================================
        // SỐ LƯỢNG
        // =====================================================

        holder.txtAdminSoLuongPhuTung.setText(
                "Số lượng: " + phuTung.getSoLuong()
        );

        // =====================================================
        // TRẠNG THÁI
        // =====================================================

        hienThiTrangThai(
                holder.txtAdminTrangThaiPhuTung,
                phuTung.isConHang()
        );

        // =====================================================
        // ẢNH
        // =====================================================

        hienThiAnh(
                holder.imgAdminPhuTung,
                phuTung.getHinhAnh()
        );

        // =====================================================
        // NÚT SỬA
        // =====================================================

        holder.btnSuaPhuTung.setOnClickListener(
                v -> hienThiDialogSua(
                        v.getContext(),
                        phuTung,
                        holder.getBindingAdapterPosition()
                )
        );

        // =====================================================
        // NÚT XÓA
        // =====================================================

        holder.btnXoaPhuTung.setOnClickListener(
                v -> hienThiDialogXoa(
                        v.getContext(),
                        phuTung,
                        holder.getBindingAdapterPosition()
                )
        );
    }

    // =========================================================
    // HIỂN THỊ ẢNH DRAWABLE
    // =========================================================

    private void hienThiAnh(
            ImageView imageView,
            String tenHinhAnh) {

        // Reset ảnh trước khi bind item mới
        imageView.setImageResource(
                R.drawable.ic_launcher_foreground
        );

        if (tenHinhAnh == null ||
                tenHinhAnh.trim().isEmpty()) {

            return;
        }

        Context context =
                imageView.getContext();

        int resourceId =
                context.getResources()
                        .getIdentifier(
                                tenHinhAnh,
                                "drawable",
                                context.getPackageName()
                        );

        if (resourceId != 0) {

            imageView.setImageResource(
                    resourceId
            );
        }
    }

    // =========================================================
    // HIỂN THỊ TRẠNG THÁI
    // =========================================================

    private void hienThiTrangThai(
            TextView textView,
            boolean conHang) {

        if (conHang) {

            textView.setText(
                    "🟢 Còn hàng"
            );

            textView.setTextColor(
                    Color.parseColor("#2E7D32")
            );

        } else {

            textView.setText(
                    "🔴 Hết hàng"
            );

            textView.setTextColor(
                    Color.parseColor("#C62828")
            );
        }
    }

    // =========================================================
    // DIALOG SỬA
    // =========================================================

    private void hienThiDialogSua(
            Context context,
            PhuTung phuTung,
            int position) {

        View view =
                LayoutInflater.from(context)
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

        // =====================================================
        // ĐIỀN DỮ LIỆU CŨ
        // =====================================================

        edtTen.setText(
                phuTung.getTen()
        );

        edtGia.setText(
                phuTung.getGia()
        );

        edtHinhAnh.setText(
                phuTung.getHinhAnh()
        );

        edtMoTa.setText(
                phuTung.getMoTa()
        );

        edtSoLuong.setText(
                String.valueOf(
                        phuTung.getSoLuong()
                )
        );

        // =====================================================
        // DIALOG
        // =====================================================

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle("Sửa phụ tùng")
                        .setView(view)
                        .setNegativeButton(
                                "Hủy",
                                null
                        )
                        .setPositiveButton(
                                "Lưu",
                                null
                        )
                        .create();

        dialog.setOnShowListener(
                d -> {

                    Button btnLuu =
                            dialog.getButton(
                                    AlertDialog.BUTTON_POSITIVE
                            );

                    btnLuu.setOnClickListener(
                            v -> {

                                String ten =
                                        edtTen.getText()
                                                .toString()
                                                .trim();

                                String gia =
                                        edtGia.getText()
                                                .toString()
                                                .trim();

                                String hinhAnh =
                                        edtHinhAnh.getText()
                                                .toString()
                                                .trim();

                                String moTa =
                                        edtMoTa.getText()
                                                .toString()
                                                .trim();

                                String soLuongText =
                                        edtSoLuong.getText()
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
                                // TỰ XÁC ĐỊNH TRẠNG THÁI
                                // =========================

                                boolean conHang =
                                        soLuong > 0;

                                btnLuu.setEnabled(false);

                                // =========================
                                // DỮ LIỆU CẬP NHẬT
                                // =========================

                                HashMap<String, Object>
                                        duLieu =
                                        new HashMap<>();

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

                                // =========================
                                // UPDATE FIRESTORE
                                // =========================

                                firestore
                                        .collection(
                                                "spare_parts"
                                        )
                                        .document(
                                                phuTung.getPhuTungId()
                                        )
                                        .update(
                                                duLieu
                                        )
                                        .addOnSuccessListener(
                                                unused -> {

                                                    // Cập nhật
                                                    // object hiện tại

                                                    phuTung.setTen(
                                                            ten
                                                    );

                                                    phuTung.setGia(
                                                            gia
                                                    );

                                                    phuTung.setHinhAnh(
                                                            hinhAnh
                                                    );

                                                    phuTung.setMoTa(
                                                            moTa
                                                    );

                                                    phuTung.setSoLuong(
                                                            soLuong
                                                    );

                                                    phuTung.setConHang(
                                                            conHang
                                                    );

                                                    if (position >= 0 &&
                                                            position < danhSachPhuTung.size()) {

                                                        notifyItemChanged(
                                                                position
                                                        );
                                                    }

                                                    Toast.makeText(
                                                            context,
                                                            "Cập nhật phụ tùng thành công!",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    dialog.dismiss();
                                                }
                                        )
                                        .addOnFailureListener(
                                                e -> {

                                                    btnLuu.setEnabled(
                                                            true
                                                    );

                                                    Toast.makeText(
                                                            context,
                                                            "Cập nhật thất bại: "
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
    // DIALOG XÓA
    // =========================================================

    private void hienThiDialogXoa(
            Context context,
            PhuTung phuTung,
            int position) {

        new AlertDialog.Builder(context)
                .setTitle("Xóa phụ tùng")
                .setMessage(
                        "Bạn có chắc muốn xóa \""
                                + phuTung.getTen()
                                + "\" không?"
                )
                .setNegativeButton(
                        "Hủy",
                        null
                )
                .setPositiveButton(
                        "Xóa",
                        (dialog, which) -> {

                            xoaPhuTung(
                                    context,
                                    phuTung,
                                    position
                            );
                        }
                )
                .show();
    }

    // =========================================================
    // XÓA PHỤ TÙNG
    // =========================================================

    private void xoaPhuTung(
            Context context,
            PhuTung phuTung,
            int position) {

        String phuTungId =
                phuTung.getPhuTungId();

        if (phuTungId == null ||
                phuTungId.isEmpty()) {

            Toast.makeText(
                    context,
                    "Không xác định được phụ tùng",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        firestore
                .collection("spare_parts")
                .document(phuTungId)
                .delete()
                .addOnSuccessListener(
                        unused -> {

                            if (position >= 0 &&
                                    position < danhSachPhuTung.size()) {

                                danhSachPhuTung.remove(
                                        position
                                );

                                notifyItemRemoved(
                                        position
                                );

                                notifyItemRangeChanged(
                                        position,
                                        danhSachPhuTung.size()
                                                - position
                                );
                            }

                            Toast.makeText(
                                    context,
                                    "Xóa phụ tùng thành công!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )
                .addOnFailureListener(
                        e -> {

                            Toast.makeText(
                                    context,
                                    "Xóa thất bại: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }

    // =========================================================
    // ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return danhSachPhuTung.size();
    }

    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class PhuTungViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgAdminPhuTung;

        TextView txtAdminTenPhuTung;
        TextView txtAdminGiaPhuTung;
        TextView txtAdminSoLuongPhuTung;
        TextView txtAdminTrangThaiPhuTung;

        Button btnSuaPhuTung;
        Button btnXoaPhuTung;

        public PhuTungViewHolder(
                @NonNull View itemView) {

            super(itemView);

            imgAdminPhuTung =
                    itemView.findViewById(
                            R.id.imgAdminPhuTung
                    );

            txtAdminTenPhuTung =
                    itemView.findViewById(
                            R.id.txtAdminTenPhuTung
                    );

            txtAdminGiaPhuTung =
                    itemView.findViewById(
                            R.id.txtAdminGiaPhuTung
                    );

            txtAdminSoLuongPhuTung =
                    itemView.findViewById(
                            R.id.txtAdminSoLuongPhuTung
                    );

            txtAdminTrangThaiPhuTung =
                    itemView.findViewById(
                            R.id.txtAdminTrangThaiPhuTung
                    );

            btnSuaPhuTung =
                    itemView.findViewById(
                            R.id.btnSuaPhuTung
                    );

            btnXoaPhuTung =
                    itemView.findViewById(
                            R.id.btnXoaPhuTung
                    );
        }
    }
}