package com.example.autocare.admin;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.example.autocare.TaiKhoan;

import java.util.ArrayList;

public class TaiKhoanAdapter
        extends RecyclerView.Adapter<TaiKhoanAdapter.TaiKhoanViewHolder> {

    private ArrayList<TaiKhoan> danhSachTaiKhoan;

    public TaiKhoanAdapter(ArrayList<TaiKhoan> danhSachTaiKhoan) {
        this.danhSachTaiKhoan = danhSachTaiKhoan;
    }

    @NonNull
    @Override
    public TaiKhoanViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_admin_tai_khoan,
                        parent,
                        false
                );

        return new TaiKhoanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull TaiKhoanViewHolder holder,
            int position) {

        TaiKhoan taiKhoan =
                danhSachTaiKhoan.get(position);

        // HỌ TÊN
        holder.txtAdminHoTen.setText(
                taiKhoan.getHoTen()
        );

        // EMAIL
        holder.txtAdminEmail.setText(
                taiKhoan.getEmail()
        );

        // VAI TRÒ
        String role = taiKhoan.getRole();

        if (role == null || role.isEmpty()) {

            holder.txtAdminRole.setText(
                    "CHƯA XÁC ĐỊNH"
            );

        } else if ("admin".equalsIgnoreCase(role)) {

            holder.txtAdminRole.setText(
                    "ADMIN"
            );

        } else {

            holder.txtAdminRole.setText(
                    "CUSTOMER"
            );
        }
    }

    @Override
    public int getItemCount() {
        return danhSachTaiKhoan.size();
    }


    public static class TaiKhoanViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAdminHoTen;
        TextView txtAdminEmail;
        TextView txtAdminRole;

        public TaiKhoanViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtAdminHoTen =
                    itemView.findViewById(
                            R.id.txtAdminHoTen
                    );

            txtAdminEmail =
                    itemView.findViewById(
                            R.id.txtAdminEmail
                    );

            txtAdminRole =
                    itemView.findViewById(
                            R.id.txtAdminRole
                    );
        }
    }
}