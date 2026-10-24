package com.example.autocare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autocare.chatbox.ChatbotActivity;
import com.example.autocare.chatbox.FloatingChatButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TrangChu extends AppCompatActivity {

    TextView tvXinChao;

    Button btnDatLich;
    Button btnXeCuaToi;
    Button btnLichSu;
    Button btnChatBot;
    Button btnDangXuat;

    LinearLayout layoutDichVu;
    RecyclerView recyclerPhuTung;

    View btnNavLich;
    View btnNavTrangChu;
    View btnNavXeCuaToi;
    View btnNavDaiLy;
    View btnNavTaiKhoan;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    PhuTungAdapter phuTungAdapter;
    ArrayList<PhuTung> danhSachPhuTung;

    LinearLayout sectionDichVu;
    LinearLayout sectionPhuTung;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_trang_chu);

        TextView tvXemTatCaPhuTung =
                findViewById(R.id.tvXemTatCaPhuTung);

        tvXemTatCaPhuTung.setOnClickListener(v -> {
            Intent intent = new Intent(
                    TrangChu.this,
                    TatCaPhuTungActivity.class
            );
            startActivity(intent);
        });
        TextView btnXemTatCaDichVu =
                findViewById(R.id.btnXemTatCaDichVu);

        btnXemTatCaDichVu.setOnClickListener(v -> {
            Intent intent = new Intent(
                    TrangChu.this,
                    TatCaDichVuActivity.class
            );
            startActivity(intent);
        });


        // =========================
        // ÁNH XẠ GIAO DIỆN
        // =========================

        tvXinChao = findViewById(R.id.tvXinChao);

        btnDatLich = findViewById(R.id.btnDatLich);
        btnXeCuaToi = findViewById(R.id.btnXeCuaToi);
        btnLichSu = findViewById(R.id.btnLichSu);
        btnDangXuat = findViewById(R.id.btnDangXuat);

        layoutDichVu = findViewById(R.id.layoutDichVu);
        recyclerPhuTung = findViewById(R.id.recyclerPhuTung);

        sectionDichVu = findViewById(R.id.sectionDichVu);
        sectionPhuTung = findViewById(R.id.sectionPhuTung);

        btnNavLich = findViewById(R.id.btnNavLich);
        btnNavTrangChu = findViewById(R.id.btnNavTrangChu);
        btnNavXeCuaToi = findViewById(R.id.btnNavXeCuaToi);
        btnNavDaiLy = findViewById(R.id.btnNavDaiLy);
        btnNavTaiKhoan = findViewById(R.id.btnNavTaiKhoan);
        ScrollView scrollTrangChu = findViewById(R.id.scrollTrangChu);
        // =========================
        // FIREBASE
        // =========================

        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // =========================
        // HIỂN THỊ TÊN
        // =========================

        hienThiTenNguoiDung();

        // =========================
        // DANH SÁCH PHỤ TÙNG
        // =========================

        danhSachPhuTung = new ArrayList<>();

        phuTungAdapter = new PhuTungAdapter(
                this,
                danhSachPhuTung
        );

        recyclerPhuTung.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerPhuTung.setAdapter(phuTungAdapter);

        taiDanhSachPhuTung();

        // =========================
        // DANH SÁCH DỊCH VỤ
        // =========================

        taiDanhSachDichVu();

        // =========================
        // NÚT ĐẶT LỊCH
        // =========================

        btnDatLich.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TrangChu.this,
                            DatLich.class
                    );

            startActivity(intent);
        });

        // =========================
        // XE CỦA TÔI
        // =========================

        btnXeCuaToi.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TrangChu.this,
                            XeCuaToi.class
                    );

            startActivity(intent);
        });

        // =========================
        // LỊCH SỬ
        // =========================

        btnLichSu.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TrangChu.this,
                            LichSuDatLich.class
                    );

            startActivity(intent);
        });

        // =========================
        // CHATBOT
        // =========================

        FloatingChatButton.attach(this, () -> {
            android.content.Intent intent =
                    new android.content.Intent(
                            TrangChu.this,
                            ChatbotActivity.class
                    );

            startActivity(intent);
        });

        // =========================
        // ĐĂNG XUẤT
        // =========================

        btnDangXuat.setOnClickListener(v ->
                dangXuat()
        );

        // =========================
        // TASKBAR - LỊCH
        // =========================

        btnNavLich.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TrangChu.this,
                            DatLich.class
                    );

            startActivity(intent);
        });

        // =========================
        // TASKBAR - TRANG CHỦ
        // =========================
        btnNavTrangChu.setOnClickListener(v ->
                scrollTrangChu.smoothScrollTo(0, 0)
        );

        // =========================
        // TASKBAR - XE CỦA TÔI
        // =========================
        btnNavXeCuaToi.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChu.this, XeCuaToi.class);
            startActivity(intent);
        });

        // =========================
        // TASKBAR - ĐẠI LÝ
        // =========================
        btnNavDaiLy.setOnClickListener(v -> {
            Intent intent = new Intent(TrangChu.this, DaiLyActivity.class);
            startActivity(intent);
        });

        // =========================
        // TASKBAR - TÀI KHOẢN (SẼ LÀM SAU)
        // =========================
        btnNavTaiKhoan.setOnClickListener(v -> {
            Intent intent = new Intent(
                    TrangChu.this,
                    TaiKhoanActivity.class
            );
            startActivity(intent);
        });
    }

    // =========================================================
    // HIỂN THỊ TÊN NGƯỜI DÙNG
    // =========================================================

    private void hienThiTenNguoiDung() {

        if (firebaseAuth.getCurrentUser() == null) {

            tvXinChao.setText("Xin chào!");

            return;
        }

        String userId =
                firebaseAuth
                        .getCurrentUser()
                        .getUid();

        firestore
                .collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (documentSnapshot.exists()) {

                        String hoTen =
                                documentSnapshot.getString("hoTen");

                        if (hoTen != null
                                && !hoTen.trim().isEmpty()) {

                            tvXinChao.setText(
                                    "Xin chào, " + hoTen + "!"
                            );

                        } else {

                            tvXinChao.setText(
                                    "Xin chào!"
                            );
                        }

                    } else {

                        tvXinChao.setText(
                                "Xin chào!"
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    tvXinChao.setText(
                            "Xin chào!"
                    );

                    Toast.makeText(
                            TrangChu.this,
                            "Không tải được thông tin người dùng",
                            Toast.LENGTH_SHORT
                    ).show();
                });
    }

    // =========================================================
    // TẢI DỊCH VỤ
    // =========================================================

    private void taiDanhSachDichVu() {

        firestore
                .collection("services")
                .whereEqualTo("hoạt động", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    layoutDichVu.removeAllViews();

                    if (querySnapshot.isEmpty()) {

                        TextView txtRong =
                                new TextView(this);

                        txtRong.setText(
                                "Chưa có dịch vụ đang hoạt động"
                        );

                        txtRong.setTextSize(15);
                        txtRong.setTextColor(
                                Color.DKGRAY
                        );

                        layoutDichVu.addView(
                                txtRong
                        );

                        return;
                    }

                    querySnapshot.getDocuments()
                            .forEach(document -> {

                                String maDichVu =
                                        document.getId();

                                String tenDichVu =
                                        document.getString("name");

                                Object giaObject = document.get("giá tiền");

                                String gia = giaObject == null
                                        ? null : String.valueOf(giaObject);

                                if (tenDichVu == null) {
                                    tenDichVu =
                                            "Dịch vụ";
                                }

                                if (gia == null) {
                                    gia = "Liên hệ";
                                }

                                themTheDichVu(
                                        maDichVu,
                                        tenDichVu,
                                        gia
                                );
                            });
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            TrangChu.this,
                            "Không tải được danh sách dịch vụ: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // TẠO CARD DỊCH VỤ
    // =========================================================

    private void themTheDichVu(
            String maDichVu,
            String tenDichVu,
            String gia
    ) {

        LinearLayout card =
                new LinearLayout(this);

        card.setOrientation(
                LinearLayout.VERTICAL
        );

        card.setGravity(
                Gravity.CENTER
        );

        int padding =
                dpToPx(14);

        card.setPadding(
                padding,
                padding,
                padding,
                padding
        );

        GradientDrawable background =
                new GradientDrawable();

        background.setColor(
                Color.WHITE
        );

        background.setCornerRadius(
                dpToPx(14)
        );

        card.setBackground(
                background
        );

        card.setElevation(
                dpToPx(3)
        );

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        dpToPx(190),
                        dpToPx(130)
                );

        cardParams.setMargins(
                0,
                0,
                dpToPx(12),
                0
        );

        card.setLayoutParams(
                cardParams
        );

        TextView icon =
                new TextView(this);

        icon.setText("🔧");
        icon.setTextSize(30);
        icon.setGravity(Gravity.CENTER);

        card.addView(
                icon,
                new LinearLayout.LayoutParams(
                        -1,
                        dpToPx(45)
                )
        );

        TextView ten =
                new TextView(this);

        ten.setText(
                tenDichVu
        );

        ten.setTextSize(16);
        ten.setTextColor(
                Color.rgb(25, 118, 210)
        );

        ten.setGravity(
                Gravity.CENTER
        );

        ten.setMaxLines(2);

        card.addView(
                ten,
                new LinearLayout.LayoutParams(
                        -1,
                        dpToPx(45)
                )
        );

        TextView txtGia =
                new TextView(this);

        txtGia.setText(
                gia + " VNĐ"
        );

        txtGia.setTextSize(13);
        txtGia.setTextColor(
                Color.DKGRAY
        );

        txtGia.setGravity(
                Gravity.CENTER
        );

        card.addView(
                txtGia,
                new LinearLayout.LayoutParams(
                        -1,
                        dpToPx(25)
                )
        );

        card.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            TrangChu.this,
                            DatLich.class
                    );

            intent.putExtra(
                    "maDichVu",
                    maDichVu
            );

            startActivity(intent);
        });

        layoutDichVu.addView(card);
    }

    // =========================================================
    // TẢI PHỤ TÙNG
    // =========================================================

    private void taiDanhSachPhuTung() {

        firestore
                .collection("spare_parts")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachPhuTung.clear();

                    querySnapshot.getDocuments()
                            .forEach(document -> {


                                PhuTung phuTung = new PhuTung();

                                phuTung.setPhuTungId(document.getId());

                                phuTung.setTen(
                                        document.getString("ten") != null
                                                ? document.getString("ten")
                                                : ""
                                );

                                Object giaObject = document.get("gia");

                                phuTung.setGia(
                                        giaObject != null
                                                ? String.valueOf(giaObject)
                                                : "0"
                                );

                                phuTung.setHinhAnh(
                                        document.getString("hinhAnh") != null
                                                ? document.getString("hinhAnh")
                                                : ""
                                );

                                phuTung.setMoTa(
                                        document.getString("moTa") != null
                                                ? document.getString("moTa")
                                                : ""
                                );

                                Object soLuongObject = document.get("soLuong");

                                int soLuong = 0;

                                if (soLuongObject instanceof Number) {
                                    soLuong = ((Number) soLuongObject).intValue();
                                } else if (soLuongObject instanceof String) {
                                    try {
                                        soLuong = Integer.parseInt(
                                                ((String) soLuongObject).trim()
                                        );
                                    } catch (NumberFormatException e) {
                                        soLuong = 0;
                                    }
                                }

                                phuTung.setSoLuong(soLuong);

                                Object conHangObject = document.get("conHang");

                                boolean conHang = false;

                                if (conHangObject instanceof Boolean) {
                                    conHang = (Boolean) conHangObject;
                                } else if (conHangObject instanceof String) {
                                    conHang = Boolean.parseBoolean(
                                            (String) conHangObject
                                    );
                                }

                                phuTung.setConHang(conHang);


                                if (phuTung != null) {

                                    if (phuTung.getPhuTungId() == null
                                            || phuTung.getPhuTungId().isEmpty()) {

                                        phuTung.setPhuTungId(
                                                document.getId()
                                        );
                                    }

                                    danhSachPhuTung.add(
                                            phuTung
                                    );
                                }
                            });

                    phuTungAdapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            TrangChu.this,
                            "Không tải được phụ tùng: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =========================================================
    // ĐĂNG XUẤT
    // =========================================================

    private void dangXuat() {

        firebaseAuth.signOut();

        Intent intent =
                new Intent(
                        TrangChu.this,
                        DangNhap.class
                );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);

        finish();
    }

    // =========================================================
    // DP → PX
    // =========================================================

    private int dpToPx(int dp) {

        float density =
                getResources()
                        .getDisplayMetrics()
                        .density;

        return (int) (
                dp * density + 0.5f
        );
    }
}
