package com.example.autocare.admin;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.LichDat;
import com.example.autocare.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import android.app.AlertDialog;

public class AdminLichAdapter
        extends RecyclerView.Adapter<AdminLichAdapter.AdminLichViewHolder> {

    private ArrayList<LichDat> danhSachLich;

    private FirebaseFirestore firestore;

    public AdminLichAdapter(
            ArrayList<LichDat> danhSachLich) {

        this.danhSachLich = danhSachLich;

        firestore = FirebaseFirestore.getInstance();
    }


    @NonNull
    @Override
    public AdminLichViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_admin_lich,
                                parent,
                                false
                        );

        return new AdminLichViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull AdminLichViewHolder holder,
            int position) {

        LichDat lich =
                danhSachLich.get(position);


        // =========================
        // DỊCH VỤ - TẠM HIỂN THỊ
        // =========================

        holder.txtAdminTenDichVu.setText(
                "Dịch vụ: Đang tải..."
        );


        // =========================
        // KHÁCH HÀNG - TẠM HIỂN THỊ
        // =========================

        holder.txtAdminKhachHang.setText(
                "Khách hàng: Đang tải..."
        );


        // =========================
        // XE - TẠM HIỂN THỊ
        // =========================

        holder.txtAdminXe.setText(
                "Xe: Đang tải..."
        );


        // =========================
        // NGÀY
        // =========================

        holder.txtAdminNgay.setText(
                "📅 " + lich.getNgay()
        );


        // =========================
        // GIỜ
        // =========================

        holder.txtAdminGio.setText(
                "🕐 " + lich.getGio()
        );


        // =========================
        // GHI CHÚ
        // =========================

        String ghiChu = lich.getGhiChu();

        if (ghiChu == null ||
                ghiChu.isEmpty()) {

            holder.txtAdminGhiChu.setText(
                    "Ghi chú: Không có"
            );

        } else {

            holder.txtAdminGhiChu.setText(
                    "Ghi chú: " + ghiChu
            );
        }


        // =========================
        // TRẠNG THÁI
        // =========================

        hienThiTrangThai(
                holder.txtAdminTrangThai,
                lich.getTrangThai()
        );

// =========================
// LẤY TÊN KHÁCH HÀNG
// =========================

        String maNguoiDung =
                lich.getMaNguoiDung();

        if (maNguoiDung != null &&
                !maNguoiDung.isEmpty()) {

            firestore
                    .collection("users")
                    .whereEqualTo("userId", maNguoiDung)
                    .get()
                    .addOnSuccessListener(
                            querySnapshot -> {

                                if (!querySnapshot.isEmpty()) {

                                    var document =
                                            querySnapshot
                                                    .getDocuments()
                                                    .get(0);

                                    String hoTen =
                                            document.getString("hoTen");

                                    if (hoTen != null &&
                                            !hoTen.isEmpty()) {

                                        holder.txtAdminKhachHang
                                                .setText(
                                                        "Khách hàng: "
                                                                + hoTen
                                                );

                                    } else {

                                        holder.txtAdminKhachHang
                                                .setText(
                                                        "Khách hàng: Chưa cập nhật"
                                                );
                                    }

                                } else {

                                    holder.txtAdminKhachHang
                                            .setText(
                                                    "Khách hàng: Không tìm thấy"
                                            );
                                }
                            }
                    )
                    .addOnFailureListener(
                            e -> {

                                holder.txtAdminKhachHang
                                        .setText(
                                                "Khách hàng: Lỗi tải dữ liệu"
                                        );
                            }
                    );

        }


        // =========================
        // LẤY THÔNG TIN XE
        // =========================

        String maXe =
                lich.getMaXe();

        if (maXe != null &&
                !maXe.isEmpty()) {

            firestore
                    .collection("vehicles")
                    .document(maXe)
                    .get()
                    .addOnSuccessListener(
                            documentSnapshot -> {

                                if (documentSnapshot.exists()) {

                                    String brand =
                                            documentSnapshot
                                                    .getString("brand");

                                    String model =
                                            documentSnapshot
                                                    .getString("model");

                                    String licensePlate =
                                            documentSnapshot
                                                    .getString("licensePlate");


                                    if (brand == null) {
                                        brand = "";
                                    }

                                    if (model == null) {
                                        model = "";
                                    }

                                    if (licensePlate == null) {
                                        licensePlate = "";
                                    }


                                    String thongTinXe =
                                            brand
                                                    + " "
                                                    + model;


                                    if (!licensePlate.isEmpty()) {

                                        thongTinXe =
                                                thongTinXe
                                                        + " - "
                                                        + licensePlate;
                                    }


                                    holder.txtAdminXe.setText(
                                            "Xe: "
                                                    + thongTinXe
                                    );

                                } else {

                                    holder.txtAdminXe.setText(
                                            "Xe: Không tìm thấy"
                                    );
                                }
                            }
                    );
        }


        // =========================
        // LẤY TÊN DỊCH VỤ
        // =========================

        String maDichVu =
                lich.getMaDichVu();

        if (maDichVu != null &&
                !maDichVu.isEmpty()) {

            firestore
                    .collection("services")
                    .whereEqualTo(
                            "dịch vụ",
                            maDichVu
                    )
                    .get()
                    .addOnSuccessListener(
                            querySnapshot -> {

                                if (!querySnapshot.isEmpty()) {

                                    var document =
                                            querySnapshot
                                                    .getDocuments()
                                                    .get(0);


                                    String tenDichVu =
                                            document
                                                    .getString("name");

                                    String giaTien =
                                            document
                                                    .getString("giá tiền");


                                    if (tenDichVu == null) {

                                        tenDichVu =
                                                "Chưa cập nhật";
                                    }


                                    if (giaTien == null) {

                                        giaTien = "";
                                    }


                                    if (!giaTien.isEmpty()) {

                                        holder.txtAdminTenDichVu
                                                .setText(
                                                        "Dịch vụ: "
                                                                + tenDichVu
                                                                + " - "
                                                                + giaTien
                                                                + " VNĐ"
                                                );

                                    } else {

                                        holder.txtAdminTenDichVu
                                                .setText(
                                                        "Dịch vụ: "
                                                                + tenDichVu
                                                );
                                    }

                                } else {

                                    holder.txtAdminTenDichVu
                                            .setText(
                                                    "Dịch vụ: Không tìm thấy"
                                            );
                                }
                            }
                    );
        }


        // =========================
        // NÚT XÁC NHẬN
        // =========================

        if ("PENDING".equalsIgnoreCase(
                lich.getTrangThai())) {

            holder.btnXacNhanLich.setVisibility(
                    View.VISIBLE
            );

        } else {

            holder.btnXacNhanLich.setVisibility(
                    View.GONE
            );
        }


        // =========================
        // NÚT CẬP NHẬT
        // =========================

        holder.btnCapNhatTrangThai.setVisibility(
                View.VISIBLE
        );


        // =========================
        // CLICK XÁC NHẬN
        // =========================

        holder.btnXacNhanLich.setOnClickListener(
                v -> {

                    capNhatTrangThai(
                            lich.getMaLich(),
                            "CONFIRMED",
                            holder.getBindingAdapterPosition()
                    );

                }
        );


        // =========================
        // CLICK CẬP NHẬT
        // =========================

        holder.btnCapNhatTrangThai.setOnClickListener(
                v -> {

                    String[] danhSachTrangThai = {
                            "Đã xác nhận",
                            "Đang sửa chữa",
                            "Hoàn thành",
                            "Đã hủy"
                    };

                    String[] giaTriTrangThai = {
                            "CONFIRMED",
                            "IN_PROGRESS",
                            "COMPLETED",
                            "CANCELLED"
                    };

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(v.getContext());

                    builder.setTitle("Cập nhật trạng thái");

                    builder.setItems(
                            danhSachTrangThai,
                            (dialog, which) -> {

                                capNhatTrangThai(
                                        lich.getMaLich(),
                                        giaTriTrangThai[which],
                                        holder.getBindingAdapterPosition()
                                );

                            }
                    );

                    builder.setNegativeButton(
                            "Hủy",
                            null
                    );

                    builder.show();
                }
        );
    }


    // =========================================================
    // HIỂN THỊ TRẠNG THÁI
    // =========================================================
// =========================================================
// CẬP NHẬT TRẠNG THÁI LỊCH
// =========================================================

    private void capNhatTrangThai(
            String maLich,
            String trangThaiMoi,
            int position) {

        if (maLich == null ||
                maLich.isEmpty()) {

            return;
        }

        firestore
                .collection("appointments")
                .whereEqualTo(
                        "maLich",
                        maLich
                )
                .get()
                .addOnSuccessListener(
                        querySnapshot -> {

                            if (!querySnapshot.isEmpty()) {

                                var document =
                                        querySnapshot
                                                .getDocuments()
                                                .get(0);

                                document
                                        .getReference()
                                        .update(
                                                "trangThai",
                                                trangThaiMoi
                                        )
                                        .addOnSuccessListener(
                                                unused -> {

                                                    // Cập nhật dữ liệu
                                                    // trong danh sách

                                                    if (position >= 0 &&
                                                            position < danhSachLich.size()) {

                                                        danhSachLich
                                                                .get(position)
                                                                .setTrangThai(
                                                                        trangThaiMoi
                                                                );

                                                        notifyItemChanged(
                                                                position
                                                        );
                                                    }
                                                }
                                        )
                                        .addOnFailureListener(
                                                e -> {

                                                    // Không cập nhật được
                                                }
                                        );

                            }
                        }
                );
    }
    private void hienThiTrangThai(
            TextView textView,
            String trangThai) {

        if (trangThai == null) {

            trangThai = "PENDING";
        }


        switch (trangThai.toUpperCase()) {

            case "CONFIRMED":

                textView.setText(
                        "ĐÃ XÁC NHẬN"
                );

                textView.setTextColor(
                        Color.parseColor("#1565C0")
                );

                textView.setBackgroundColor(
                        Color.parseColor("#E3F2FD")
                );

                break;


            case "IN_PROGRESS":

                textView.setText(
                        "ĐANG SỬA CHỮA"
                );

                textView.setTextColor(
                        Color.parseColor("#7B1FA2")
                );

                textView.setBackgroundColor(
                        Color.parseColor("#F3E5F5")
                );

                break;


            case "COMPLETED":

                textView.setText(
                        "HOÀN THÀNH"
                );

                textView.setTextColor(
                        Color.parseColor("#2E7D32")
                );

                textView.setBackgroundColor(
                        Color.parseColor("#E8F5E9")
                );

                break;


            case "CANCELLED":

                textView.setText(
                        "ĐÃ HỦY"
                );

                textView.setTextColor(
                        Color.parseColor("#C62828")
                );

                textView.setBackgroundColor(
                        Color.parseColor("#FFEBEE")
                );

                break;


            case "PENDING":

            default:

                textView.setText(
                        "CHỜ XÁC NHẬN"
                );

                textView.setTextColor(
                        Color.parseColor("#F57C00")
                );

                textView.setBackgroundColor(
                        Color.parseColor("#FFF3E0")
                );

                break;
        }
    }


    // =========================================================
    // GET ITEM COUNT
    // =========================================================

    @Override
    public int getItemCount() {

        return danhSachLich.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class AdminLichViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAdminTenDichVu;
        TextView txtAdminKhachHang;
        TextView txtAdminXe;
        TextView txtAdminNgay;
        TextView txtAdminGio;
        TextView txtAdminGhiChu;
        TextView txtAdminTrangThai;

        Button btnXacNhanLich;
        Button btnCapNhatTrangThai;


        public AdminLichViewHolder(
                @NonNull View itemView) {

            super(itemView);


            txtAdminTenDichVu =
                    itemView.findViewById(
                            R.id.txtAdminTenDichVu
                    );


            txtAdminKhachHang =
                    itemView.findViewById(
                            R.id.txtAdminKhachHang
                    );


            txtAdminXe =
                    itemView.findViewById(
                            R.id.txtAdminXe
                    );


            txtAdminNgay =
                    itemView.findViewById(
                            R.id.txtAdminNgay
                    );


            txtAdminGio =
                    itemView.findViewById(
                            R.id.txtAdminGio
                    );


            txtAdminGhiChu =
                    itemView.findViewById(
                            R.id.txtAdminGhiChu
                    );


            txtAdminTrangThai =
                    itemView.findViewById(
                            R.id.txtAdminTrangThai
                    );


            btnXacNhanLich =
                    itemView.findViewById(
                            R.id.btnXacNhanLich
                    );


            btnCapNhatTrangThai =
                    itemView.findViewById(
                            R.id.btnCapNhatTrangThai
                    );
        }
    }
}