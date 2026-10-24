package com.example.autocare;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class TatCaDichVuActivity extends AppCompatActivity {

    private LinearLayout layoutDanhSachDichVu;
    private ProgressBar progressBar;
    private TextView tvThongBao;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        taoGiaoDien();

        firestore = FirebaseFirestore.getInstance();

        taiDanhSachDichVu();
    }

    // =====================================================
    // TẠO GIAO DIỆN
    // =====================================================

    private void taoGiaoDien() {

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(245, 247, 252));
        root.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        // HEADER
        LinearLayout header = new LinearLayout(this);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setGravity(Gravity.CENTER_VERTICAL);
        header.setPadding(dp(18), dp(18), dp(18), dp(18));
        header.setBackgroundColor(Color.WHITE);

        TextView btnQuayLai = new TextView(this);
        btnQuayLai.setText("‹");
        btnQuayLai.setTextSize(36);
        btnQuayLai.setTextColor(Color.rgb(18, 88, 168));
        btnQuayLai.setGravity(Gravity.CENTER);
        btnQuayLai.setContentDescription("Quay lại");
        btnQuayLai.setLayoutParams(new LinearLayout.LayoutParams(
                dp(42), dp(48)
        ));
        btnQuayLai.setOnClickListener(v -> finish());

        LinearLayout headerText = new LinearLayout(this);
        headerText.setOrientation(LinearLayout.VERTICAL);
        headerText.setPadding(dp(8), 0, 0, 0);

        TextView tvTieuDe = new TextView(this);
        tvTieuDe.setText("Tất cả dịch vụ");
        tvTieuDe.setTextSize(23);
        tvTieuDe.setTypeface(null, Typeface.BOLD);
        tvTieuDe.setTextColor(Color.rgb(23, 33, 47));

        TextView tvPhuDe = new TextView(this);
        tvPhuDe.setText("Chăm sóc và bảo dưỡng xe");
        tvPhuDe.setTextSize(13);
        tvPhuDe.setTextColor(Color.rgb(100, 116, 139));
        tvPhuDe.setPadding(0, dp(4), 0, 0);

        headerText.addView(tvTieuDe);
        headerText.addView(tvPhuDe);

        header.addView(btnQuayLai);
        header.addView(headerText);

        root.addView(header);

        // ĐƯỜNG PHÂN CÁCH
        View divider = new View(this);
        divider.setBackgroundColor(Color.rgb(226, 232, 240));
        root.addView(divider, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(1)
        ));

        // NỘI DUNG CUỘN
        ScrollView scrollView = new ScrollView(this);
        scrollView.setFillViewport(true);
        scrollView.setClipToPadding(false);
        scrollView.setVerticalScrollBarEnabled(false);

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(18), dp(16), dp(24));

        // SỐ LƯỢNG / MÔ TẢ
        TextView tvGioiThieu = new TextView(this);
        tvGioiThieu.setText(
                "Khám phá các dịch vụ dành cho chiếc xe của bạn"
        );
        tvGioiThieu.setTextSize(14);
        tvGioiThieu.setTextColor(Color.rgb(71, 85, 105));
        tvGioiThieu.setPadding(0, 0, 0, dp(16));

        content.addView(tvGioiThieu);

        progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams progressParams =
                new LinearLayout.LayoutParams(dp(42), dp(42));
        progressParams.gravity = Gravity.CENTER;
        progressParams.setMargins(0, dp(25), 0, dp(12));

        content.addView(progressBar, progressParams);

        tvThongBao = new TextView(this);
        tvThongBao.setTextColor(Color.rgb(100, 116, 139));
        tvThongBao.setTextSize(14);
        tvThongBao.setGravity(Gravity.CENTER);
        tvThongBao.setVisibility(View.GONE);

        LinearLayout.LayoutParams thongBaoParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
        thongBaoParams.setMargins(0, dp(12), 0, dp(12));
        content.addView(tvThongBao, thongBaoParams);

        layoutDanhSachDichVu = new LinearLayout(this);
        layoutDanhSachDichVu.setOrientation(LinearLayout.VERTICAL);

        content.addView(layoutDanhSachDichVu);

        scrollView.addView(content);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1
        ));

        setContentView(root);
    }

    // =====================================================
    // TẢI DANH SÁCH DỊCH VỤ TỪ FIRESTORE
    // =====================================================

    private void taiDanhSachDichVu() {

        progressBar.setVisibility(View.VISIBLE);
        tvThongBao.setVisibility(View.GONE);
        layoutDanhSachDichVu.removeAllViews();

        firestore.collection("services")
                .whereEqualTo("hoạt động", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    progressBar.setVisibility(View.GONE);

                    if (querySnapshot.isEmpty()) {
                        tvThongBao.setText(
                                "Hiện chưa có dịch vụ nào đang hoạt động."
                        );
                        tvThongBao.setVisibility(View.VISIBLE);
                        return;
                    }

                    for (DocumentSnapshot document :
                            querySnapshot.getDocuments()) {

                        String maDichVu = document.getId();

                        String tenDichVu =
                                document.getString("name");

                        if (tenDichVu == null ||
                                tenDichVu.trim().isEmpty()) {
                            tenDichVu = "Dịch vụ chưa đặt tên";
                        }

                        Object giaObject = document.get("giá tiền");
                        String giaTien = dinhDangGia(giaObject);

                        String thoiGian =
                                document.getString("thời gian");

                        String moTa =
                                document.getString("mô tả");

                        if (thoiGian == null ||
                                thoiGian.trim().isEmpty()) {
                            thoiGian = "Đang cập nhật";
                        }

                        if (moTa == null ||
                                moTa.trim().isEmpty()) {
                            moTa = "Chưa có mô tả cho dịch vụ này.";
                        }

                        themTheDichVu(
                                maDichVu,
                                tenDichVu,
                                giaTien,
                                thoiGian,
                                moTa
                        );
                    }
                })
                .addOnFailureListener(e -> {

                    progressBar.setVisibility(View.GONE);

                    tvThongBao.setText(
                            "Không tải được danh sách dịch vụ.\n" +
                                    "Vui lòng kiểm tra kết nối và thử lại."
                    );
                    tvThongBao.setVisibility(View.VISIBLE);

                    Toast.makeText(
                            TatCaDichVuActivity.this,
                            "Lỗi tải dịch vụ: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    // =====================================================
    // TẠO THẺ DỊCH VỤ
    // =====================================================

    private void themTheDichVu(
            String maDichVu,
            String tenDichVu,
            String giaTien,
            String thoiGian,
            String moTa
    ) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(16), dp(16), dp(16));

        GradientDrawable background = new GradientDrawable();
        background.setColor(Color.WHITE);
        background.setCornerRadius(dp(18));
        background.setStroke(dp(1), Color.rgb(232, 237, 245));

        card.setBackground(background);
        card.setElevation(dp(3));
        card.setClickable(true);
        card.setFocusable(true);

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.setMargins(0, 0, 0, dp(14));
        card.setLayoutParams(cardParams);

        // ICON + TÊN DỊCH VỤ
        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        TextView icon = new TextView(this);
        icon.setText("🔧");
        icon.setTextSize(27);
        icon.setGravity(Gravity.CENTER);

        GradientDrawable iconBackground = new GradientDrawable();
        iconBackground.setColor(Color.rgb(232, 242, 255));
        iconBackground.setCornerRadius(dp(14));
        icon.setBackground(iconBackground);

        top.addView(icon, new LinearLayout.LayoutParams(
                dp(56), dp(56)
        ));

        LinearLayout info = new LinearLayout(this);
        info.setOrientation(LinearLayout.VERTICAL);
        info.setPadding(dp(12), 0, 0, 0);

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        TextView tvTen = new TextView(this);
        tvTen.setText(tenDichVu);
        tvTen.setTextColor(Color.rgb(23, 33, 47));
        tvTen.setTextSize(17);
        tvTen.setTypeface(null, Typeface.BOLD);
        tvTen.setMaxLines(2);

        TextView tvThoiGian = new TextView(this);
        tvThoiGian.setText("◷  " + thoiGian);
        tvThoiGian.setTextColor(Color.rgb(100, 116, 139));
        tvThoiGian.setTextSize(13);
        tvThoiGian.setPadding(0, dp(6), 0, 0);

        info.addView(tvTen);
        info.addView(tvThoiGian);

        top.addView(info, infoParams);

        card.addView(top);

        // GIÁ TIỀN
        TextView tvGia = new TextView(this);
        tvGia.setText(giaTien);
        tvGia.setTextSize(18);
        tvGia.setTypeface(null, Typeface.BOLD);
        tvGia.setTextColor(Color.rgb(21, 101, 192));
        tvGia.setPadding(0, dp(14), 0, dp(8));

        card.addView(tvGia);

        // MÔ TẢ
        TextView tvMoTa = new TextView(this);
        tvMoTa.setText(moTa);
        tvMoTa.setTextSize(13);
        tvMoTa.setTextColor(Color.rgb(100, 116, 139));
        tvMoTa.setMaxLines(3);

        card.addView(tvMoTa);

        // GỢI Ý THAO TÁC
        TextView tvXemChiTiet = new TextView(this);
        tvXemChiTiet.setText("Đặt lịch  ›");
        tvXemChiTiet.setTextSize(13);
        tvXemChiTiet.setTypeface(null, Typeface.BOLD);
        tvXemChiTiet.setTextColor(Color.rgb(21, 101, 192));
        tvXemChiTiet.setGravity(Gravity.END);
        tvXemChiTiet.setPadding(0, dp(14), 0, 0);

        card.addView(tvXemChiTiet);

        // BẤM VÀO DỊCH VỤ -> TRANG ĐẶT LỊCH
        card.setOnClickListener(v -> {

            Intent intent = new Intent(
                    TatCaDichVuActivity.this,
                    DatLich.class
            );

            intent.putExtra("maDichVu", maDichVu);
            intent.putExtra("tenDichVu", tenDichVu);

            startActivity(intent);
        });

        layoutDanhSachDichVu.addView(card);
    }

    // =====================================================
    // ĐỊNH DẠNG GIÁ TIỀN
    // =====================================================

    private String dinhDangGia(Object giaObject) {

        if (giaObject == null) {
            return "Giá: Liên hệ";
        }

        String giaChu = String.valueOf(giaObject).trim();

        if (giaChu.isEmpty()) {
            return "Giá: Liên hệ";
        }

        try {
            double gia;

            if (giaObject instanceof Number) {
                gia = ((Number) giaObject).doubleValue();
            } else {
                String giaSach = giaChu
                        .replace("VNĐ", "")
                        .replace("VND", "")
                        .replace("vnđ", "")
                        .replace("vnd", "")
                        .replace("đ", "")
                        .replace("Đ", "")
                        .replace("₫", "")
                        .replace(" ", "")
                        .replace(".", "")
                        .replace(",", "");

                gia = Double.parseDouble(giaSach);
            }

            if (gia < 0) {
                return "Giá: Liên hệ";
            }

            DecimalFormatSymbols symbols =
                    new DecimalFormatSymbols(new Locale("vi", "VN"));

            symbols.setGroupingSeparator('.');

            DecimalFormat formatter =
                    new DecimalFormat("#,###", symbols);

            return "Giá: " + formatter.format(gia) + " VNĐ";

        } catch (Exception e) {
            // Nếu giá được lưu dưới dạng văn bản không phải số,
            // giữ nguyên để không làm mất thông tin.
            return "Giá: " + giaChu;
        }
    }

    // =====================================================
    // DP SANG PX
    // =====================================================

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }
}
