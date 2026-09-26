package com.example.autocare;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LichDatAdapter extends RecyclerView.Adapter<LichDatAdapter.LichDatViewHolder> {

    private Context context;
    private ArrayList<LichDat> danhSachLich;
    private FirebaseFirestore firestore;
    private FirebaseAuth firebaseAuth;

    public LichDatAdapter(
            Context context,
            ArrayList<LichDat> danhSachLich) {

        this.context = context;
        this.danhSachLich = danhSachLich;

        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public LichDatViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.item_lich_su,
                        parent,
                        false
                );

        return new LichDatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull LichDatViewHolder holder,
            int position) {

        LichDat lich = danhSachLich.get(position);

        // ================= XEM CHI TIẾT LỊCH =================

        holder.itemView.setOnClickListener(v -> {

            String maLich = lich.getMaLich();

            Intent intent = new Intent(
                    context,
                    ChiTietLich.class
            );

            intent.putExtra(
                    "maLich",
                    maLich
            );

            context.startActivity(intent);
        });

        // ================= NGÀY =================

        holder.txtNgay.setText(
                lich.getNgay()
        );

        // ================= GIỜ =================

        holder.txtGio.setText(
                lich.getGio()
        );

        // ================= GHI CHÚ =================

        String ghiChu = lich.getGhiChu();

        if (ghiChu == null || ghiChu.isEmpty()) {

            holder.txtGhiChu.setText(
                    "Không có ghi chú"
            );

        } else {

            holder.txtGhiChu.setText(
                    ghiChu
            );
        }

        // ================= TRẠNG THÁI =================

        String trangThai = lich.getTrangThai();

        if ("PENDING".equals(trangThai)) {

            holder.txtTrangThai.setText(
                    "Chờ xác nhận"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(245, 158, 11)
            );

            holder.txtTrangThai.setBackgroundColor(
                    Color.rgb(255, 244, 214)
            );

        } else if ("CONFIRMED".equals(trangThai)) {

            holder.txtTrangThai.setText(
                    "Đã xác nhận"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(37, 99, 235)
            );

            holder.txtTrangThai.setBackgroundColor(
                    Color.rgb(219, 234, 254)
            );

        } else if ("IN_PROGRESS".equals(trangThai)) {

            holder.txtTrangThai.setText(
                    "Đang sửa chữa"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(124, 58, 237)
            );

            holder.txtTrangThai.setBackgroundColor(
                    Color.rgb(237, 233, 254)
            );

        } else if ("COMPLETED".equals(trangThai)) {

            holder.txtTrangThai.setText(
                    "Hoàn thành"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(22, 163, 74)
            );

            holder.txtTrangThai.setBackgroundColor(
                    Color.rgb(220, 252, 231)
            );

        } else if ("CANCELLED".equals(trangThai)) {

            holder.txtTrangThai.setText(
                    "Đã hủy"
            );

            holder.txtTrangThai.setTextColor(
                    Color.rgb(220, 38, 38)
            );

            holder.txtTrangThai.setBackgroundColor(
                    Color.rgb(254, 226, 226)
            );

        } else {

            holder.txtTrangThai.setText(
                    trangThai
            );
        }

        // ================= NÚT HỦY LỊCH =================

        if ("PENDING".equals(trangThai)) {

            holder.btnHuyLich.setVisibility(View.VISIBLE);

            holder.btnHuyLich.setOnClickListener(v -> {

                new AlertDialog.Builder(context)
                        .setTitle("Hủy lịch đặt")
                        .setMessage(
                                "Bạn có chắc chắn muốn hủy lịch này không?"
                        )
                        .setNegativeButton(
                                "Không",
                                null
                        )
                        .setPositiveButton(
                                "Hủy lịch",
                                (dialog, which) -> {

                                    String maLich =
                                            lich.getMaLich();

                                    firestore.collection("appointments")
                                            .document(maLich)
                                            .update(
                                                    "trangThai",
                                                    "CANCELLED"
                                            )
                                            .addOnSuccessListener(unused -> {

                                                // Cập nhật trạng thái trong danh sách
                                                lich.setTrangThai(
                                                        "CANCELLED"
                                                );

                                                // Cập nhật lại card
                                                int viTri =
                                                        holder.getBindingAdapterPosition();

                                                if (viTri != RecyclerView.NO_POSITION) {

                                                    notifyItemChanged(
                                                            viTri
                                                    );
                                                }

                                                Toast.makeText(
                                                        context,
                                                        "Đã hủy lịch",
                                                        Toast.LENGTH_SHORT
                                                ).show();

                                            })
                                            .addOnFailureListener(e -> {

                                                Toast.makeText(
                                                        context,
                                                        "Hủy lịch thất bại: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            });
                                }
                        )
                        .show();
            });

        } else {

            holder.btnHuyLich.setVisibility(
                    View.GONE
            );

            // Xóa listener cũ tránh lỗi khi RecyclerView tái sử dụng item
            holder.btnHuyLich.setOnClickListener(null);
        }

        // ================= LẤY THÔNG TIN XE =================

        String maXe = lich.getMaXe();

        // Hiển thị tạm thời trong lúc tải dữ liệu
        holder.txtThongTinXe.setText(
                "Đang tải thông tin xe..."
        );

        if (maXe != null && !maXe.isEmpty()) {

            firestore.collection("vehicles")
                    .document(maXe)
                    .get()
                    .addOnSuccessListener(document -> {

                        if (document.exists()) {

                            String hangXe =
                                    document.getString("brand");

                            String tenXe =
                                    document.getString("model");

                            String bienSo =
                                    document.getString("licensePlate");

                            if (hangXe == null) {
                                hangXe = "";
                            }

                            if (tenXe == null) {
                                tenXe = "";
                            }

                            if (bienSo == null) {
                                bienSo = "";
                            }

                            String thongTinXe =
                                    hangXe
                                            + " "
                                            + tenXe
                                            + " - "
                                            + bienSo;

                            holder.txtThongTinXe.setText(
                                    thongTinXe
                            );

                        } else {

                            holder.txtThongTinXe.setText(
                                    "Không tìm thấy thông tin xe"
                            );
                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.txtThongTinXe.setText(
                                "Không tải được thông tin xe"
                        );
                    });

        } else {

            holder.txtThongTinXe.setText(
                    "Chưa có thông tin xe"
            );
        }

        // ================= LẤY TÊN DỊCH VỤ =================

        String maDichVu = lich.getMaDichVu();

        // Hiển thị tạm thời trong lúc tải dữ liệu
        holder.txtTenDichVu.setText(
                "Đang tải dịch vụ..."
        );

        if (maDichVu != null && !maDichVu.isEmpty()) {

            firestore.collection("services")
                    .whereEqualTo(
                            "dịch vụ",
                            maDichVu
                    )
                    .get()
                    .addOnSuccessListener(querySnapshot -> {

                        if (!querySnapshot.isEmpty()) {

                            String tenDichVu =
                                    querySnapshot
                                            .getDocuments()
                                            .get(0)
                                            .getString("name");

                            if (tenDichVu != null) {

                                holder.txtTenDichVu.setText(
                                        tenDichVu
                                );

                            } else {

                                holder.txtTenDichVu.setText(
                                        "Dịch vụ"
                                );
                            }

                        } else {

                            holder.txtTenDichVu.setText(
                                    "Dịch vụ: " + maDichVu
                            );
                        }
                    })
                    .addOnFailureListener(e -> {

                        holder.txtTenDichVu.setText(
                                "Dịch vụ: " + maDichVu
                        );
                    });

        } else {

            holder.txtTenDichVu.setText(
                    "Chưa có dịch vụ"
            );
        }
    }

    @Override
    public int getItemCount() {

        return danhSachLich.size();
    }

    // ================= VIEW HOLDER =================

    public static class LichDatViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtTenDichVu;
        TextView txtTrangThai;
        TextView txtThongTinXe;
        TextView txtNgay;
        TextView txtGio;
        TextView txtGhiChu;

        Button btnHuyLich;

        public LichDatViewHolder(
                @NonNull View itemView) {

            super(itemView);

            txtTenDichVu =
                    itemView.findViewById(
                            R.id.txtTenDichVu
                    );

            txtTrangThai =
                    itemView.findViewById(
                            R.id.txtTrangThai
                    );

            txtThongTinXe =
                    itemView.findViewById(
                            R.id.txtThongTinXe
                    );

            txtNgay =
                    itemView.findViewById(
                            R.id.txtNgay
                    );

            txtGio =
                    itemView.findViewById(
                            R.id.txtGio
                    );

            txtGhiChu =
                    itemView.findViewById(
                            R.id.txtGhiChu
                    );

            btnHuyLich =
                    itemView.findViewById(
                            R.id.btnHuyLich
                    );
        }
    }
}