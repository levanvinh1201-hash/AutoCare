package com.example.autocare;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class XeCuaToi extends AppCompatActivity {

    private Button btnThemXe;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firestore;

    private Uri anhXeDaChon;
    private ImageView imgAnhXe;
    private Dialog dialogDangChonAnh;

    private ActivityResultLauncher<PickVisualMediaRequest> chonAnhLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_xe_cua_toi);

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null
                            && dialogDangChonAnh != null
                            && dialogDangChonAnh.isShowing()) {

                        anhXeDaChon = uri;

                        if (imgAnhXe != null) {
                            imgAnhXe.setImageURI(uri);
                        }
                    }
                }
        );

        btnThemXe = findViewById(R.id.btnThemXe);

        btnThemXe.setOnClickListener(v -> hienThiFormXe(null));

        hienThiDanhSachXe();
    }

    // ==================================================
    // FORM THÊM XE / CHỈNH SỬA XE
    // ==================================================

    private void hienThiFormXe(DocumentSnapshot xeCu) {

        boolean dangSua = xeCu != null;

        anhXeDaChon = null;

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_them_xe);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        ImageView imgAnh = dialog.findViewById(R.id.imgAnhXe);
        Button btnChonAnh = dialog.findViewById(R.id.btnChonAnhXe);
        Button btnLuu = dialog.findViewById(R.id.btnLuuXe);

        EditText edtHangXe = dialog.findViewById(R.id.edtHangXe);
        EditText edtTenXe = dialog.findViewById(R.id.edtTenXe);
        EditText edtBienSo = dialog.findViewById(R.id.edtBienSo);
        EditText edtNam = dialog.findViewById(R.id.edtNamSanXuat);

        if (dangSua) {

            edtHangXe.setText(giaTri(xeCu.getString("brand")));
            edtTenXe.setText(giaTri(xeCu.getString("model")));
            edtBienSo.setText(giaTri(xeCu.getString("licensePlate")));
            edtNam.setText(giaTri(xeCu.getString("year")));

            btnLuu.setText("Lưu thay đổi");

            String duongDanAnh = xeCu.getString("imagePath");
            hienThiAnh(imgAnh, duongDanAnh);
        }

        btnChonAnh.setOnClickListener(v -> {
            imgAnhXe = imgAnh;
            dialogDangChonAnh = dialog;

            chonAnhLauncher.launch(
                    new PickVisualMediaRequest.Builder()
                            .setMediaType(
                                    ActivityResultContracts
                                            .PickVisualMedia
                                            .ImageOnly.INSTANCE
                            )
                            .build()
            );
        });

        btnLuu.setOnClickListener(v -> luuXe(dialog, xeCu));

        dialog.setOnDismissListener(d -> {
            if (dialogDangChonAnh == dialog) {
                dialogDangChonAnh = null;
                imgAnhXe = null;
            }
            anhXeDaChon = null;
        });

        dialog.show();
        chinhKichThuocDialog(dialog);
    }

    // ==================================================
    // LƯU XE MỚI HOẶC CẬP NHẬT XE
    // ==================================================

    private void luuXe(Dialog dialog, DocumentSnapshot xeCu) {

        EditText edtHangXe = dialog.findViewById(R.id.edtHangXe);
        EditText edtTenXe = dialog.findViewById(R.id.edtTenXe);
        EditText edtBienSo = dialog.findViewById(R.id.edtBienSo);
        EditText edtNam = dialog.findViewById(R.id.edtNamSanXuat);

        String hangXe = edtHangXe.getText().toString().trim();
        String tenXe = edtTenXe.getText().toString().trim();
        String bienSo = edtBienSo.getText().toString().trim();
        String nam = edtNam.getText().toString().trim();

        if (hangXe.isEmpty()) {
            edtHangXe.setError("Vui lòng nhập hãng xe");
            edtHangXe.requestFocus();
            return;
        }

        if (tenXe.isEmpty()) {
            edtTenXe.setError("Vui lòng nhập tên xe");
            edtTenXe.requestFocus();
            return;
        }

        if (bienSo.isEmpty()) {
            edtBienSo.setError("Vui lòng nhập biển số");
            edtBienSo.requestFocus();
            return;
        }

        if (nam.isEmpty()) {
            edtNam.setError("Vui lòng nhập năm sản xuất");
            edtNam.requestFocus();
            return;
        }

        if (firebaseAuth.getCurrentUser() == null) {
            thongBao("Vui lòng đăng nhập lại");
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        boolean dangSua = xeCu != null;

        String vehicleId = dangSua
                ? xeCu.getId()
                : UUID.randomUUID().toString();

        String duongDanAnhCu = dangSua
                ? giaTri(xeCu.getString("imagePath"))
                : "";

        String duongDanAnhMoi = duongDanAnhCu;

        // Chỉ lưu ảnh mới nếu người dùng có chọn ảnh.
        // Dùng tên file ngẫu nhiên để không ghi đè ảnh cũ
        // trước khi Firebase cập nhật thành công.
        if (anhXeDaChon != null) {

            duongDanAnhMoi = luuAnhXe(anhXeDaChon);

            if (duongDanAnhMoi == null) {
                thongBao("Không thể lưu ảnh xe. Vui lòng thử lại.");
                return;
            }
        }

        final String anhMoi = duongDanAnhMoi;
        final boolean coAnhMoi = anhXeDaChon != null;

        Map<String, Object> duLieu = new HashMap<>();

        duLieu.put("vehicleId", vehicleId);
        duLieu.put("userId", userId);
        duLieu.put("brand", hangXe);
        duLieu.put("model", tenXe);
        duLieu.put("licensePlate", bienSo);
        duLieu.put("year", nam);
        duLieu.put("imagePath", anhMoi);

        Button btnLuu = dialog.findViewById(R.id.btnLuuXe);
        btnLuu.setEnabled(false);

        // Đọc lại tài liệu khi sửa để kiểm tra quyền sở hữu.
        if (dangSua) {

            firestore.collection("vehicles")
                    .document(vehicleId)
                    .get()
                    .addOnSuccessListener(snapshot -> {

                        if (!snapshot.exists()
                                || !userId.equals(snapshot.getString("userId"))) {

                            btnLuu.setEnabled(true);

                            if (coAnhMoi) {
                                xoaAnhCucBo(anhMoi);
                            }

                            thongBao("Không có quyền chỉnh sửa xe này");
                            return;
                        }

                        capNhatFirebase(
                                dialog,
                                vehicleId,
                                duLieu,
                                true,
                                duongDanAnhCu,
                                anhMoi,
                                coAnhMoi
                        );
                    })
                    .addOnFailureListener(e -> {
                        btnLuu.setEnabled(true);

                        if (coAnhMoi) {
                            xoaAnhCucBo(anhMoi);
                        }

                        thongBao("Không thể kiểm tra thông tin xe");
                    });

        } else {

            capNhatFirebase(
                    dialog,
                    vehicleId,
                    duLieu,
                    false,
                    "",
                    anhMoi,
                    coAnhMoi
            );
        }
    }

    private void capNhatFirebase(
            Dialog dialog,
            String vehicleId,
            Map<String, Object> duLieu,
            boolean dangSua,
            String anhCu,
            String anhMoi,
            boolean coAnhMoi
    ) {

        firestore.collection("vehicles")
                .document(vehicleId)
                .set(duLieu)
                .addOnSuccessListener(unused -> {

                    // Chỉ xóa ảnh cũ sau khi cập nhật thành công.
                    if (dangSua && coAnhMoi && !anhCu.equals(anhMoi)) {
                        xoaAnhCucBo(anhCu);
                    }

                    thongBao(
                            dangSua
                                    ? "Cập nhật xe thành công!"
                                    : "Thêm xe thành công!"
                    );

                    dialog.dismiss();
                    hienThiDanhSachXe();
                })
                .addOnFailureListener(e -> {

                    Button btnLuu = dialog.findViewById(R.id.btnLuuXe);
                    btnLuu.setEnabled(true);

                    if (coAnhMoi) {
                        xoaAnhCucBo(anhMoi);
                    }

                    thongBao("Lỗi: " + e.getMessage());
                });
    }

    // ==================================================
    // HIỂN THỊ DANH SÁCH XE
    // ==================================================

    private void hienThiDanhSachXe() {

        LinearLayout layoutDanhSachXe =
                findViewById(R.id.layoutDanhSachXe);

        View layoutKhongCoXe =
                findViewById(R.id.layoutKhongCoXe);

        TextView tvSoLuongXe =
                findViewById(R.id.tvSoLuongXe);

        if (firebaseAuth.getCurrentUser() == null) {
            return;
        }

        String userId = firebaseAuth.getCurrentUser().getUid();

        firestore.collection("vehicles")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutDanhSachXe.removeAllViews();

                    int soLuong = querySnapshot.size();

                    tvSoLuongXe.setText(soLuong + " xe");

                    layoutKhongCoXe.setVisibility(
                            soLuong == 0 ? View.VISIBLE : View.GONE
                    );

                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {

                        LinearLayout theXe = new LinearLayout(this);
                        theXe.setOrientation(LinearLayout.VERTICAL);
                        theXe.setPadding(dp(14), dp(14), dp(14), dp(14));
                        theXe.setBackground(taoNenTrang(16));

                        LinearLayout.LayoutParams paramsThe =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                );

                        paramsThe.bottomMargin = dp(14);
                        theXe.setLayoutParams(paramsThe);

                        // Ảnh xe
                        ImageView anhXe = new ImageView(this);

                        LinearLayout.LayoutParams paramsAnh =
                                new LinearLayout.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        dp(170)
                                );

                        paramsAnh.bottomMargin = dp(12);
                        anhXe.setLayoutParams(paramsAnh);
                        anhXe.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        anhXe.setBackgroundColor(Color.parseColor("#EAF1FB"));

                        hienThiAnh(anhXe, document.getString("imagePath"));

                        theXe.addView(anhXe);

                        // Tên xe
                        TextView tvTen = new TextView(this);

                        tvTen.setText(
                                "🚗  "
                                        + giaTri(document.getString("brand"))
                                        + " "
                                        + giaTri(document.getString("model"))
                        );

                        tvTen.setTextSize(18);
                        tvTen.setTypeface(null, Typeface.BOLD);
                        tvTen.setTextColor(Color.parseColor("#14264B"));

                        theXe.addView(tvTen);

                        // Biển số
                        TextView tvBienSo = new TextView(this);

                        tvBienSo.setText(
                                "Biển số: "
                                        + giaTri(document.getString("licensePlate"))
                        );

                        tvBienSo.setTextSize(14);
                        tvBienSo.setTextColor(Color.parseColor("#64748B"));
                        tvBienSo.setPadding(0, dp(10), 0, 0);

                        theXe.addView(tvBienSo);

                        // Năm sản xuất
                        TextView tvNam = new TextView(this);

                        tvNam.setText(
                                "Năm sản xuất: "
                                        + giaTri(document.getString("year"))
                        );

                        tvNam.setTextSize(14);
                        tvNam.setTextColor(Color.parseColor("#64748B"));
                        tvNam.setPadding(0, dp(6), 0, dp(12));

                        theXe.addView(tvNam);

                        // Nút xem chi tiết
                        Button btnChiTiet = new Button(this);

                        btnChiTiet.setText("Xem chi tiết");
                        btnChiTiet.setAllCaps(false);
                        btnChiTiet.setTextColor(Color.WHITE);
                        btnChiTiet.setBackgroundTintList(
                                android.content.res.ColorStateList.valueOf(
                                        Color.parseColor("#1565C0")
                                )
                        );

                        btnChiTiet.setOnClickListener(
                                v -> hienThiChiTietXe(document)
                        );

                        theXe.addView(btnChiTiet);

                        // Nhấn cả thẻ cũng mở chi tiết
                        theXe.setOnClickListener(
                                v -> hienThiChiTietXe(document)
                        );

                        layoutDanhSachXe.addView(theXe);
                    }

                })
                .addOnFailureListener(e ->
                        thongBao("Không tải được danh sách xe")
                );
    }

    // ==================================================
    // XEM CHI TIẾT XE
    // ==================================================

    private void hienThiChiTietXe(DocumentSnapshot xe) {

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_chi_tiet_xe);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
        }

        ImageView imgChiTiet =
                dialog.findViewById(R.id.imgChiTietXe);

        TextView tvTen =
                dialog.findViewById(R.id.tvChiTietTenXe);

        TextView tvHang =
                dialog.findViewById(R.id.tvChiTietHangXe);

        TextView tvBienSo =
                dialog.findViewById(R.id.tvChiTietBienSo);

        TextView tvNam =
                dialog.findViewById(R.id.tvChiTietNamXe);

        Button btnSua =
                dialog.findViewById(R.id.btnSuaChiTietXe);

        Button btnXoa =
                dialog.findViewById(R.id.btnXoaChiTietXe);

        TextView btnDong =
                dialog.findViewById(R.id.btnDongChiTietXe);

        hienThiAnh(imgChiTiet, xe.getString("imagePath"));

        tvTen.setText(
                giaTri(xe.getString("brand"))
                        + " "
                        + giaTri(xe.getString("model"))
        );

        tvHang.setText(giaTri(xe.getString("brand")));
        tvBienSo.setText(giaTri(xe.getString("licensePlate")));
        tvNam.setText(giaTri(xe.getString("year")));

        btnDong.setOnClickListener(v -> dialog.dismiss());

        btnSua.setOnClickListener(v -> {
            dialog.dismiss();
            hienThiFormXe(xe);
        });

        btnXoa.setOnClickListener(v -> {
            xacNhanXoaXe(xe, dialog);
        });

        dialog.show();
        chinhKichThuocDialog(dialog);
    }

    // ==================================================
    // XÓA XE
    // ==================================================

    private void xacNhanXoaXe(DocumentSnapshot xe, Dialog dialogChiTiet) {

        new AlertDialog.Builder(this)
                .setTitle("Xóa phương tiện")
                .setMessage(
                        "Bạn có chắc chắn muốn xóa xe "
                                + giaTri(xe.getString("brand"))
                                + " "
                                + giaTri(xe.getString("model"))
                                + " không?\n\n"
                                + "Thao tác này không thể hoàn tác."
                )
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa xe", (d, which) -> {

                    if (firebaseAuth.getCurrentUser() == null) {
                        thongBao("Vui lòng đăng nhập lại");
                        return;
                    }

                    String userId = firebaseAuth.getCurrentUser().getUid();
                    String vehicleId = xe.getId();

                    firestore.collection("vehicles")
                            .document(vehicleId)
                            .get()
                            .addOnSuccessListener(snapshot -> {

                                if (!snapshot.exists()
                                        || !userId.equals(snapshot.getString("userId"))) {

                                    thongBao("Không có quyền xóa xe này");
                                    return;
                                }

                                String duongDanAnh =
                                        snapshot.getString("imagePath");

                                firestore.collection("vehicles")
                                        .document(vehicleId)
                                        .delete()
                                        .addOnSuccessListener(unused -> {

                                            xoaAnhCucBo(duongDanAnh);

                                            thongBao("Xóa xe thành công!");

                                            dialogChiTiet.dismiss();
                                            hienThiDanhSachXe();
                                        })
                                        .addOnFailureListener(e ->
                                                thongBao(
                                                        "Không thể xóa xe: "
                                                                + e.getMessage()
                                                )
                                        );
                            })
                            .addOnFailureListener(e ->
                                    thongBao("Không thể kiểm tra thông tin xe")
                            );
                })
                .show();
    }

    // ==================================================
    // LƯU ẢNH CỤC BỘ
    // ==================================================

    private String luuAnhXe(Uri uri) {

        if (uri == null) {
            return "";
        }

        try {

            File thuMuc = new File(getFilesDir(), "anh_xe");

            if (!thuMuc.exists() && !thuMuc.mkdirs()) {
                return null;
            }

            File fileAnh = new File(
                    thuMuc,
                    UUID.randomUUID().toString() + ".jpg"
            );

            Bitmap bitmap;

            try (InputStream inputStream =
                         getContentResolver().openInputStream(uri)) {

                if (inputStream == null) {
                    return null;
                }

                bitmap = BitmapFactory.decodeStream(inputStream);
            }

            if (bitmap == null) {
                return null;
            }

            int rong = bitmap.getWidth();
            int cao = bitmap.getHeight();
            int gioiHan = 1200;

            if (rong > gioiHan || cao > gioiHan) {

                float tiLe = Math.min(
                        (float) gioiHan / rong,
                        (float) gioiHan / cao
                );

                Bitmap bitmapMoi = Bitmap.createScaledBitmap(
                        bitmap,
                        Math.max(1, Math.round(rong * tiLe)),
                        Math.max(1, Math.round(cao * tiLe)),
                        true
                );

                if (bitmapMoi != bitmap) {
                    bitmap.recycle();
                }

                bitmap = bitmapMoi;
            }

            try (FileOutputStream outputStream =
                         new FileOutputStream(fileAnh)) {

                boolean thanhCong = bitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        85,
                        outputStream
                );

                bitmap.recycle();

                if (!thanhCong) {
                    fileAnh.delete();
                    return null;
                }
            }

            return fileAnh.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ==================================================
    // HIỂN THỊ / XÓA ẢNH
    // ==================================================

    private void hienThiAnh(ImageView imageView, String duongDan) {

        imageView.setImageDrawable(null);

        if (duongDan == null || duongDan.isEmpty()) {
            imageView.setBackgroundColor(Color.parseColor("#EAF1FB"));
            return;
        }

        File file = new File(duongDan);

        if (file.exists()) {
            imageView.setImageURI(Uri.fromFile(file));
        } else {
            imageView.setBackgroundColor(Color.parseColor("#EAF1FB"));
        }
    }

    private void xoaAnhCucBo(String duongDan) {

        if (duongDan == null || duongDan.isEmpty()) {
            return;
        }

        try {
            // Chỉ xóa file nằm trong thư mục ảnh riêng của AutoCare.
            File thuMuc = new File(getFilesDir(), "anh_xe");
            File file = new File(duongDan);

            if (file.getCanonicalFile().getParentFile()
                    .equals(thuMuc.getCanonicalFile())) {

                if (file.exists()) {
                    file.delete();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ==================================================
    // HÀM HỖ TRỢ
    // ==================================================

    private void chinhKichThuocDialog(Dialog dialog) {

        if (dialog.getWindow() == null) {
            return;
        }

        int rong = getResources().getDisplayMetrics().widthPixels;
        int cao = getResources().getDisplayMetrics().heightPixels;

        dialog.getWindow().setLayout(
                (int) (rong * 0.92),
                (int) (cao * 0.85)
        );
    }

    private GradientDrawable taoNenTrang(int boGoc) {

        GradientDrawable nen = new GradientDrawable();

        nen.setColor(Color.WHITE);
        nen.setCornerRadius(dp(boGoc));
        nen.setStroke(dp(1), Color.parseColor("#E3EAF4"));

        return nen;
    }

    private int dp(int value) {

        return (int) (
                value * getResources().getDisplayMetrics().density
        );
    }

    private String giaTri(String value) {
        return value == null ? "" : value;
    }

    private void thongBao(String noiDung) {
        Toast.makeText(this, noiDung, Toast.LENGTH_SHORT).show();
    }
}