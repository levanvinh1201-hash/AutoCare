package com.example.autocare;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import android.content.Intent;

public class PhuTungAdapter
        extends RecyclerView.Adapter<PhuTungAdapter.ViewHolder> {

    private final Context context;
    private final List<PhuTung> danhSach;

    public PhuTungAdapter(
            Context context,
            List<PhuTung> danhSach
    ) {
        this.context = context;
        this.danhSach = danhSach;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view =
                LayoutInflater
                        .from(context)
                        .inflate(
                                R.layout.item_phu_tung,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        PhuTung phuTung =
                danhSach.get(position);

        // =========================
        // TÊN
        // =========================

        String ten =
                phuTung.getTen();

        if (ten == null || ten.isEmpty()) {
            ten = "Phụ tùng";
        }

        holder.txtTen.setText(ten);

        // =========================
        // GIÁ
        // =========================

//        String gia =
//                phuTung.getGia();
//
//        if (gia == null || gia.isEmpty()) {
//            gia = "Liên hệ";
//        } else {
//            gia = gia + " VNĐ";
//        }
//
//        holder.txtGia.setText(gia);
        // =========================
// GIÁ TIỀN
// =========================

        String gia = phuTung.getGia();

        if (gia == null || gia.trim().isEmpty()) {
            holder.txtGia.setText("Liên hệ");
        } else {
            try {
                // Loại bỏ dấu phân cách nếu giá đã được định dạng
                String giaSo = gia.trim()
                        .replace(".", "")
                        .replace(",", "")
                        .replace(" ", "");

                // Chuyển giá sang số
                long giaTri = Long.parseLong(giaSo);

                // Định dạng dấu chấm hàng nghìn theo kiểu Việt Nam
                DecimalFormatSymbols symbols =
                        new DecimalFormatSymbols(Locale.US);
                symbols.setGroupingSeparator('.');

                DecimalFormat formatter =
                        new DecimalFormat("#,###", symbols);

                formatter.setGroupingUsed(true);
                formatter.setMaximumFractionDigits(0);

                holder.txtGia.setText(
                        formatter.format(giaTri) + " VNĐ"
                );

            } catch (NumberFormatException e) {
                holder.txtGia.setText(gia + " VNĐ");
            }
        }

        // =========================
        // SỐ LƯỢNG
        // =========================

        int soLuong =
                phuTung.getSoLuong();

        holder.txtSoLuong.setText(
                "Còn " + soLuong + " sản phẩm"
        );

        // =========================
        // TRẠNG THÁI
        // =========================

        if (phuTung.isConHang()
                && soLuong > 0) {

            holder.txtTrangThai.setText(
                    "Còn hàng"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(46, 125, 50)
            );

        } else {

            holder.txtTrangThai.setText(
                    "Hết hàng"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(211, 47, 47)
            );
        }

        // =========================
        // ẢNH
        // =========================

        holder.imgPhuTung.setImageResource(
                android.R.drawable.ic_menu_gallery
        );

        String hinhAnh =
                phuTung.getHinhAnh();

        if (hinhAnh != null
                && !hinhAnh.trim().isEmpty()) {

            int resourceId =
                    context.getResources()
                            .getIdentifier(
                                    hinhAnh,
                                    "drawable",
                                    context.getPackageName()
                            );

            if (resourceId != 0) {

                holder.imgPhuTung.setImageResource(
                        resourceId
                );
            }
        }
        // Nhấn vào phụ tùng để xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(
                    context,
                    ChiTietPhuTungActivity.class
            );

            intent.putExtra("phuTungId", phuTung.getPhuTungId());
            intent.putExtra("ten", phuTung.getTen());
            intent.putExtra("gia", phuTung.getGia());
            intent.putExtra("hinhAnh", phuTung.getHinhAnh());
            intent.putExtra("moTa", phuTung.getMoTa());
            intent.putExtra("soLuong", phuTung.getSoLuong());
            intent.putExtra("conHang", phuTung.isConHang());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        ImageView imgPhuTung;
        TextView txtTen;
        TextView txtGia;
        TextView txtSoLuong;
        TextView txtTrangThai;

        public ViewHolder(
                @NonNull View itemView
        ) {
            super(itemView);

            imgPhuTung =
                    itemView.findViewById(
                            R.id.imgPhuTung
                    );

            txtTen =
                    itemView.findViewById(
                            R.id.txtTenPhuTung
                    );

            txtGia =
                    itemView.findViewById(
                            R.id.txtGiaPhuTung
                    );

            txtSoLuong =
                    itemView.findViewById(
                            R.id.txtSoLuongPhuTung
                    );

            txtTrangThai =
                    itemView.findViewById(
                            R.id.txtTrangThaiPhuTung
                    );
        }
    }
}