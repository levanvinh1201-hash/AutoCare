package com.example.autocare.admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;

import java.util.ArrayList;

public class TaiKhoanLichAdapter
        extends RecyclerView.Adapter<TaiKhoanLichAdapter.ViewHolder> {

    private Context context;
    private ArrayList<TaiKhoanLich> danhSach;

    public TaiKhoanLichAdapter(
            Context context,
            ArrayList<TaiKhoanLich> danhSach) {

        this.context = context;
        this.danhSach = danhSach;
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

        TaiKhoanLich taiKhoan =
                danhSach.get(position);

        holder.txtHoTen.setText(
                taiKhoan.getHoTen()
        );

        holder.txtEmail.setText(
                taiKhoan.getEmail()
        );

        holder.txtSoLich.setText(
                taiKhoan.getSoLuongLich()
                        + " lịch đặt"
        );

        holder.itemView.setOnClickListener(
                v -> {

                    Intent intent =
                            new Intent(
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
                }
        );
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

        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtHoTen =
                    itemView.findViewById(
                            R.id.txtAdminTenKhach
                    );

            txtEmail =
                    itemView.findViewById(
                            R.id.txtAdminEmailKhach
                    );

            txtSoLich =
                    itemView.findViewById(
                            R.id.txtAdminSoLichKhach
                    );
        }
    }
}