
package com.example.autocare.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class AdminPhuTungAdapter
        extends RecyclerView.Adapter<AdminPhuTungAdapter.PhuTungViewHolder> {

    private final ArrayList<PhuTung> danhSachPhuTung;
    private final FirebaseFirestore firestore;

    public AdminPhuTungAdapter(ArrayList<PhuTung> danhSachPhuTung) {
        this.danhSachPhuTung = danhSachPhuTung;
        this.firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public PhuTungViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_phu_tung, parent, false);

        return new PhuTungViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PhuTungViewHolder holder,
            int position) {

        PhuTung phuTung = danhSachPhuTung.get(position);

        String ten = phuTung.getTen();

        if (ten == null || ten.isEmpty()) {
            ten = "Chưa cập nhật";
        }

        holder.txtAdminTenPhuTung.setText(ten);

        // ĐỊNH DẠNG GIÁ KHI HIỂN THỊ
        holder.txtAdminGiaPhuTung.setText(
                dinhDangGia(phuTung.getGia()) + "đ"
        );

        holder.txtAdminSoLuongPhuTung.setText(
                "Số lượng: " + phuTung.getSoLuong()
        );

        hienThiTrangThai(
                holder.txtAdminTrangThaiPhuTung,
                phuTung.isConHang()
        );

        hienThiAnh(
                holder.imgAdminPhuTung,
                phuTung.getHinhAnh()
        );

        holder.btnSuaPhuTung.setOnClickListener(v -> {
            int viTri = holder.getBindingAdapterPosition();

            if (viTri != RecyclerView.NO_POSITION) {
                hienThiDialogSua(
                        v.getContext(),
                        phuTung,
                        viTri
                );
            }
        });

        holder.btnXoaPhuTung.setOnClickListener(v -> {
            int viTri = holder.getBindingAdapterPosition();

            if (viTri != RecyclerView.NO_POSITION) {
                hienThiDialogXoa(
                        v.getContext(),
                        phuTung,
                        viTri
                );
            }
        });
    }

    // =========================================================
    // CHUẨN HÓA GIÁ VỀ CHỈ CÒN CHỮ SỐ
    // =========================================================

    private String layChuSo(String gia) {
        if (gia == null || gia.trim().isEmpty()) {
            return "0";
        }

        String chuSo = gia.replaceAll("[^0-9]", "");

        return chuSo.isEmpty() ? "0" : chuSo;
    }

    // =========================================================
    // ĐỊNH DẠNG GIÁ: 400000 -> 400.000
    // =========================================================

    private String dinhDangGia(String gia) {
        if (gia == null || gia.trim().isEmpty()) {
            return "0";
        }

        try {
            long soTien = Long.parseLong(layChuSo(gia));

            DecimalFormatSymbols symbols =
                    new DecimalFormatSymbols(Locale.US);

            symbols.setGroupingSeparator('.');

            DecimalFormat formatter =
                    new DecimalFormat("#,###", symbols);

            formatter.setGroupingUsed(true);

            return formatter.format(soTien);

        } catch (NumberFormatException e) {
            return gia;
        }
    }

    // =========================================================
    // TỰ ĐỘNG ĐỊNH DẠNG KHI NHẬP GIÁ
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
                if (dangSua) {
                    return;
                }

                dangSua = true;

                String chuSo = s.toString().replaceAll("[^0-9]", "");

                if (chuSo.isEmpty()) {
                    editText.setText("");
                    dangSua = false;
                    return;
                }

                try {
                    long soTien = Long.parseLong(chuSo);

                    String giaMoi = dinhDangGia(
                            String.valueOf(soTien)
                    );

                    editText.setText(giaMoi);
                    editText.setSelection(editText.length());

                } catch (NumberFormatException e) {
                    // Nếu vượt giới hạn long thì không định dạng lại.
                }

                dangSua = false;
            }
        });
    }

    // =========================================================
    // HIỂN THỊ ẢNH DRAWABLE
    // =========================================================

    private void hienThiAnh(
            ImageView imageView,
            String tenHinhAnh) {

        imageView.setImageResource(
                R.drawable.ic_launcher_foreground
        );

        if (tenHinhAnh == null || tenHinhAnh.trim().isEmpty()) {
            return;
        }

        Context context = imageView.getContext();

        int resourceId = context.getResources().getIdentifier(
                tenHinhAnh,
                "drawable",
                context.getPackageName()
        );

        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        }
    }

    // =========================================================
    // HIỂN THỊ TRẠNG THÁI
    // =========================================================

    private void hienThiTrangThai(
            TextView textView,
            boolean conHang) {

        if (conHang) {
            textView.setText("🟢 Còn hàng");
            textView.setTextColor(Color.parseColor("#2E7D32"));
        } else {
            textView.setText("🔴 Hết hàng");
            textView.setTextColor(Color.parseColor("#C62828"));
        }
    }

    // =========================================================
    // DIALOG SỬA
    // =========================================================

    private void hienThiDialogSua(
            Context context,
            PhuTung phuTung,
            int position) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_them_phu_tung, null);

        EditText edtTen = view.findViewById(R.id.edtTenPhuTung);
        EditText edtGia = view.findViewById(R.id.edtGiaPhuTung);
        EditText edtHinhAnh = view.findViewById(R.id.edtHinhAnhPhuTung);
        EditText edtMoTa = view.findViewById(R.id.edtMoTaPhuTung);
        EditText edtSoLuong = view.findViewById(R.id.edtSoLuongPhuTung);

        edtTen.setText(phuTung.getTen());

        // Đặt giá cũ trước, sau đó mới gắn TextWatcher
        edtGia.setText(dinhDangGia(phuTung.getGia()));
        tuDongDinhDangTien(edtGia);

        edtHinhAnh.setText(phuTung.getHinhAnh());
        edtMoTa.setText(phuTung.getMoTa());
        edtSoLuong.setText(String.valueOf(phuTung.getSoLuong()));

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Sửa phụ tùng")
                .setView(view)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnLuu = dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE
            );

            btnLuu.setOnClickListener(v -> {
                String ten = edtTen.getText().toString().trim();

                // Xóa dấu chấm trước khi lưu
                String gia = edtGia.getText().toString()
                        .replace(".", "")
                        .trim();

                String hinhAnh = edtHinhAnh.getText().toString().trim();
                String moTa = edtMoTa.getText().toString().trim();
                String soLuongText = edtSoLuong.getText().toString().trim();

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

                if (soLuongText.isEmpty()) {
                    edtSoLuong.setError("Vui lòng nhập số lượng");
                    edtSoLuong.requestFocus();
                    return;
                }

                int soLuong;

                try {
                    soLuong = Integer.parseInt(soLuongText);
                } catch (Exception e) {
                    edtSoLuong.setError("Số lượng không hợp lệ");
                    edtSoLuong.requestFocus();
                    return;
                }

                if (soLuong < 0) {
                    edtSoLuong.setError("Số lượng không được âm");
                    edtSoLuong.requestFocus();
                    return;
                }

                // Kiểm tra giá là số hợp lệ
                try {
                    Long.parseLong(gia);
                } catch (NumberFormatException e) {
                    edtGia.setError("Giá không hợp lệ");
                    edtGia.requestFocus();
                    return;
                }

                boolean conHang = soLuong > 0;

                btnLuu.setEnabled(false);

                HashMap<String, Object> duLieu = new HashMap<>();
                duLieu.put("ten", ten);
                duLieu.put("gia", gia);
                duLieu.put("hinhAnh", hinhAnh);
                duLieu.put("moTa", moTa);
                duLieu.put("soLuong", soLuong);
                duLieu.put("conHang", conHang);

                firestore.collection("spare_parts")
                        .document(phuTung.getPhuTungId())
                        .update(duLieu)
                        .addOnSuccessListener(unused -> {
                            phuTung.setTen(ten);
                            phuTung.setGia(gia);
                            phuTung.setHinhAnh(hinhAnh);
                            phuTung.setMoTa(moTa);
                            phuTung.setSoLuong(soLuong);
                            phuTung.setConHang(conHang);

                            if (position >= 0 &&
                                    position < danhSachPhuTung.size()) {
                                notifyItemChanged(position);
                            }

                            Toast.makeText(
                                    context,
                                    "Cập nhật phụ tùng thành công!",
                                    Toast.LENGTH_SHORT
                            ).show();

                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            btnLuu.setEnabled(true);

                            Toast.makeText(
                                    context,
                                    "Cập nhật thất bại: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        });

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
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) ->
                        xoaPhuTung(context, phuTung, position))
                .show();
    }

    // =========================================================
    // XÓA PHỤ TÙNG
    // =========================================================

    private void xoaPhuTung(
            Context context,
            PhuTung phuTung,
            int position) {

        String phuTungId = phuTung.getPhuTungId();

        if (phuTungId == null || phuTungId.isEmpty()) {
            Toast.makeText(
                    context,
                    "Không xác định được phụ tùng",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        firestore.collection("spare_parts")
                .document(phuTungId)
                .delete()
                .addOnSuccessListener(unused -> {
                    if (position >= 0 &&
                            position < danhSachPhuTung.size()) {

                        danhSachPhuTung.remove(position);
                        notifyItemRemoved(position);

                        notifyItemRangeChanged(
                                position,
                                danhSachPhuTung.size() - position
                        );
                    }

                    Toast.makeText(
                            context,
                            "Xóa phụ tùng thành công!",
                            Toast.LENGTH_SHORT
                    ).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                context,
                                "Xóa thất bại: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show());
    }

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

        public PhuTungViewHolder(@NonNull View itemView) {
            super(itemView);

            imgAdminPhuTung =
                    itemView.findViewById(R.id.imgAdminPhuTung);

            txtAdminTenPhuTung =
                    itemView.findViewById(R.id.txtAdminTenPhuTung);

            txtAdminGiaPhuTung =
                    itemView.findViewById(R.id.txtAdminGiaPhuTung);

            txtAdminSoLuongPhuTung =
                    itemView.findViewById(R.id.txtAdminSoLuongPhuTung);

            txtAdminTrangThaiPhuTung =
                    itemView.findViewById(R.id.txtAdminTrangThaiPhuTung);

            btnSuaPhuTung =
                    itemView.findViewById(R.id.btnSuaPhuTung);

            btnXoaPhuTung =
                    itemView.findViewById(R.id.btnXoaPhuTung);
        }
    }
}
