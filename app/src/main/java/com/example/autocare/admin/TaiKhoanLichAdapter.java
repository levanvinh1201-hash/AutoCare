package com.example.autocare.admin;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaiKhoanLichAdapter
        extends RecyclerView.Adapter<TaiKhoanLichAdapter.ViewHolder> {

    private final Context context;
    private final ArrayList<TaiKhoanLich> danhSach;

    private final FirebaseFirestore db;

    public TaiKhoanLichAdapter(
            Context context,
            ArrayList<TaiKhoanLich> danhSach) {

        this.context = context;
        this.danhSach = danhSach;

        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_tai_khoan_lich,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        TaiKhoanLich taiKhoan = danhSach.get(position);

        holder.txtHoTen.setText(taiKhoan.getHoTen());

        holder.txtEmail.setText(taiKhoan.getEmail());

        holder.txtSoLich.setText(
                taiKhoan.getSoLuongLich() + " lịch đặt"
        );

        // Hiển thị trạng thái khi đang lấy số điện thoại
        holder.txtSoDienThoai.setText("Đang tải số điện thoại...");
        holder.txtSoDienThoai.setEnabled(false);
        holder.txtSoDienThoai.setOnClickListener(null);

        String userId = taiKhoan.getUserId();

        // Lưu userId vào ViewHolder để tránh hiển thị
        // nhầm số điện thoại khi RecyclerView tái sử dụng item
        holder.itemView.setTag(userId);

        if (userId != null && !userId.trim().isEmpty()) {

            db.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(document -> {

                        // Kiểm tra item còn đang hiển thị
                        // đúng khách hàng hay không
                        if (!userId.equals(holder.itemView.getTag())) {
                            return;
                        }

                        String sdt = document.getString("soDienThoai");

                        if (sdt != null && !sdt.trim().isEmpty()) {

                            String soDienThoai = sdt.trim();

                            holder.txtSoDienThoai.setText(
                                    "☎  " + soDienThoai
                            );

                            holder.txtSoDienThoai.setEnabled(true);

                            holder.txtSoDienThoai.setOnClickListener(
                                    v -> goiDien(soDienThoai)
                            );

                        } else {

                            holder.txtSoDienThoai.setText(
                                    "Chưa cập nhật số điện thoại"
                            );

                            holder.txtSoDienThoai.setEnabled(false);
                        }
                    })
                    .addOnFailureListener(e -> {

                        if (!userId.equals(holder.itemView.getTag())) {
                            return;
                        }

                        holder.txtSoDienThoai.setText(
                                "Không tải được số điện thoại"
                        );

                        holder.txtSoDienThoai.setEnabled(false);
                    });

        } else {

            holder.txtSoDienThoai.setText(
                    "Chưa có thông tin khách hàng"
            );
        }

        // Nhấn vào khách hàng để xem lịch đặt
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(
                    context,
                    AdminLichCuaKhach.class
            );

            intent.putExtra(
                    "userId",
                    taiKhoan.getUserId()
            );

            intent.putExtra(
                    "hoTen",
                    taiKhoan.getHoTen()
            );

            context.startActivity(intent);
        });
    }

    // Mở ứng dụng gọi điện
    private void goiDien(String soDienThoai) {

        if (soDienThoai == null
                || soDienThoai.trim().isEmpty()) {

            Toast.makeText(
                    context,
                    "Khách hàng chưa cập nhật số điện thoại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String sdt = soDienThoai.replaceAll("[^0-9+]", "");

        Intent intent = new Intent(
                Intent.ACTION_DIAL,
                Uri.parse("tel:" + sdt)
        );

        try {

            context.startActivity(intent);

        } catch (ActivityNotFoundException e) {

            Toast.makeText(
                    context,
                    "Thiết bị không có ứng dụng gọi điện",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    public int getItemCount() {
        return danhSach.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtHoTen;
        TextView txtEmail;
        TextView txtSoLich;
        TextView txtSoDienThoai;

        public ViewHolder(@NonNull View itemView) {

            super(itemView);

            txtHoTen = itemView.findViewById(
                    R.id.txtAdminTenKhach
            );

            txtEmail = itemView.findViewById(
                    R.id.txtAdminEmailKhach
            );

            txtSoLich = itemView.findViewById(
                    R.id.txtAdminSoLichKhach
            );

            txtSoDienThoai = itemView.findViewById(
                    R.id.txtAdminSoDienThoaiKhach
            );
        }
    }
}