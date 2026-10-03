package com.example.autocare.admin;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.R;
import com.example.autocare.TaiKhoan;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class TaiKhoanAdapter
        extends RecyclerView.Adapter<TaiKhoanAdapter.TaiKhoanViewHolder> {

    private ArrayList<TaiKhoan> danhSachTaiKhoan;

    private FirebaseFirestore firestore;

    public TaiKhoanAdapter(ArrayList<TaiKhoan> danhSachTaiKhoan) {

        this.danhSachTaiKhoan = danhSachTaiKhoan;

        firestore = FirebaseFirestore.getInstance();
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

        // =========================
        // HỌ TÊN
        // =========================

        holder.txtAdminHoTen.setText(
                taiKhoan.getHoTen()
        );


        // =========================
        // EMAIL
        // =========================

        holder.txtAdminEmail.setText(
                taiKhoan.getEmail()
        );


        // =========================
        // VAI TRÒ
        // =========================

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


        // =========================
        // NÚT THÊM
        // =========================

        holder.btnThemTaiKhoanItem.setOnClickListener(v -> {

            Toast.makeText(
                    v.getContext(),
                    "Vui lòng sử dụng nút + Thêm tài khoản ở phía trên",
                    Toast.LENGTH_SHORT
            ).show();

        });


        // =========================
        // NÚT SỬA
        // =========================

        holder.btnSuaTaiKhoan.setOnClickListener(v -> {

            hienThiDialogSuaTaiKhoan(
                    v.getContext(),
                    taiKhoan
            );

        });


        // =========================
        // NÚT XÓA
        // =========================

        holder.btnXoaTaiKhoan.setOnClickListener(v -> {

            hienThiDialogXoaTaiKhoan(
                    v.getContext(),
                    taiKhoan,
                    holder.getAdapterPosition()
            );

        });
    }


    // =========================================================
    // DIALOG SỬA TÀI KHOẢN
    // =========================================================

    private void hienThiDialogSuaTaiKhoan(
            Context context,
            TaiKhoan taiKhoan) {

        View view =
                LayoutInflater.from(context)
                        .inflate(
                                R.layout.dialog_sua_tai_khoan,
                                null
                        );


        EditText edtHoTen =
                view.findViewById(
                        R.id.edtSuaHoTen
                );


        Spinner spinnerRole =
                view.findViewById(
                        R.id.spinnerSuaRole
                );


        // Hiển thị dữ liệu hiện tại

        edtHoTen.setText(
                taiKhoan.getHoTen()
        );


        // Danh sách quyền

        String[] danhSachRole = {
                "customer",
                "admin"
        };


        ArrayAdapter<String> roleAdapter =
                new ArrayAdapter<>(
                        context,
                        android.R.layout.simple_spinner_item,
                        danhSachRole
                );


        roleAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );


        spinnerRole.setAdapter(
                roleAdapter
        );


        // Chọn role hiện tại

        if ("admin".equalsIgnoreCase(
                taiKhoan.getRole())) {

            spinnerRole.setSelection(1);

        } else {

            spinnerRole.setSelection(0);
        }


        // Tạo Dialog

        AlertDialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle("Sửa tài khoản")
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


        dialog.setOnShowListener(d -> {

            Button btnLuu =
                    dialog.getButton(
                            AlertDialog.BUTTON_POSITIVE
                    );


            btnLuu.setOnClickListener(v -> {

                String hoTen =
                        edtHoTen
                                .getText()
                                .toString()
                                .trim();


                String role =
                        spinnerRole
                                .getSelectedItem()
                                .toString();


                // Kiểm tra họ tên

                if (hoTen.isEmpty()) {

                    edtHoTen.setError(
                            "Vui lòng nhập họ tên"
                    );

                    edtHoTen.requestFocus();

                    return;
                }


                String userId =
                        taiKhoan.getUserId();


                if (userId == null ||
                        userId.isEmpty()) {

                    Toast.makeText(
                            context,
                            "Không xác định được tài khoản",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }


                // Dữ liệu cần cập nhật

                HashMap<String, Object> duLieuCapNhat =
                        new HashMap<>();


                duLieuCapNhat.put(
                        "hoTen",
                        hoTen
                );


                duLieuCapNhat.put(
                        "role",
                        role
                );


                btnLuu.setEnabled(false);


                // Cập nhật Firestore

                firestore
                        .collection("users")
                        .document(userId)
                        .update(duLieuCapNhat)
                        .addOnSuccessListener(unused -> {

                            // Cập nhật dữ liệu trên danh sách

                            danhSachTaiKhoan
                                    .get(
                                            danhSachTaiKhoan
                                                    .indexOf(taiKhoan)
                                    );

                            Toast.makeText(
                                    context,
                                    "Cập nhật tài khoản thành công!",
                                    Toast.LENGTH_SHORT
                            ).show();


                            dialog.dismiss();


                            // Refresh item

                            notifyDataSetChanged();

                        })
                        .addOnFailureListener(e -> {

                            btnLuu.setEnabled(true);

                            Toast.makeText(
                                    context,
                                    "Cập nhật thất bại: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();

                        });
            });

        });


        dialog.show();
    }


    // =========================================================
    // DIALOG XÓA TÀI KHOẢN
    // =========================================================

    private void hienThiDialogXoaTaiKhoan(
            Context context,
            TaiKhoan taiKhoan,
            int position) {


        new AlertDialog.Builder(context)

                .setTitle("Xóa tài khoản")

                .setMessage(
                        "Bạn có chắc muốn xóa tài khoản:\n\n"
                                + taiKhoan.getHoTen()
                                + "\n"
                                + taiKhoan.getEmail()
                )

                .setNegativeButton(
                        "Hủy",
                        null
                )

                .setPositiveButton(
                        "Xóa",
                        (dialog, which) -> {

                            xoaTaiKhoan(
                                    context,
                                    taiKhoan,
                                    position
                            );

                        })

                .show();
    }


    // =========================================================
    // XÓA TÀI KHOẢN TRONG FIRESTORE
    // =========================================================

    private void xoaTaiKhoan(
            Context context,
            TaiKhoan taiKhoan,
            int position) {


        String userId =
                taiKhoan.getUserId();


        if (userId == null ||
                userId.isEmpty()) {

            Toast.makeText(
                    context,
                    "Không xác định được tài khoản",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        firestore
                .collection("users")
                .document(userId)
                .delete()

                .addOnSuccessListener(unused -> {

                    int viTri =
                            danhSachTaiKhoan
                                    .indexOf(taiKhoan);


                    if (viTri != -1) {

                        danhSachTaiKhoan.remove(
                                viTri
                        );

                        notifyItemRemoved(
                                viTri
                        );

                        notifyItemRangeChanged(
                                viTri,
                                danhSachTaiKhoan.size()
                        );
                    }


                    Toast.makeText(
                            context,
                            "Đã xóa dữ liệu tài khoản!",
                            Toast.LENGTH_SHORT
                    ).show();

                })

                .addOnFailureListener(e -> {

                    Toast.makeText(
                            context,
                            "Xóa thất bại: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }


    // =========================================================
    // SỐ LƯỢNG ITEM
    // =========================================================

    @Override
    public int getItemCount() {

        return danhSachTaiKhoan.size();
    }


    // =========================================================
    // VIEW HOLDER
    // =========================================================

    public static class TaiKhoanViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtAdminHoTen;
        TextView txtAdminEmail;
        TextView txtAdminRole;

        Button btnThemTaiKhoanItem;
        Button btnSuaTaiKhoan;
        Button btnXoaTaiKhoan;


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


            btnThemTaiKhoanItem =
                    itemView.findViewById(
                            R.id.btnThemTaiKhoan
                    );


            btnSuaTaiKhoan =
                    itemView.findViewById(
                            R.id.btnSuaTaiKhoan
                    );


            btnXoaTaiKhoan =
                    itemView.findViewById(
                            R.id.btnXoaTaiKhoan
                    );
        }
    }
}