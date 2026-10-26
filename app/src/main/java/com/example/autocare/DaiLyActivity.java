
package com.example.autocare;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DaiLyActivity extends FragmentActivity
        implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FirebaseFirestore db;

    private EditText edtTimGara;
    private LinearLayout layoutDanhSachGara;
    private TextView tvSoLuongGara;

    private final List<Gara> danhSachGara = new ArrayList<>();

    // Chỉ là tâm bản đồ mặc định khi chưa có dữ liệu
    private static final LatLng VI_TRI_MAC_DINH =
            new LatLng(21.0285, 105.8542);

    private static class Gara {
        String id;
        String ten;
        String diaChi;
        String soDienThoai;
        String gioMoCua;
        double latitude;
        double longitude;

        Gara(String id, String ten, String diaChi,
             String soDienThoai, String gioMoCua,
             double latitude, double longitude) {
            this.id = id;
            this.ten = ten;
            this.diaChi = diaChi;
            this.soDienThoai = soDienThoai;
            this.gioMoCua = gioMoCua;
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_dai_ly);
        BottomNavHelper.setup(this);

        db = FirebaseFirestore.getInstance();

        edtTimGara = findViewById(R.id.edtTimGara);
        layoutDanhSachGara =
                findViewById(R.id.layoutDanhSachGara);
        tvSoLuongGara = findViewById(R.id.tvSoLuongGara);

        findViewById(R.id.btnQuayLaiDaiLy)
                .setOnClickListener(v -> finish());


        edtTimGara.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start,
                    int count, int after) {}

            @Override
            public void onTextChanged(
                    CharSequence s, int start,
                    int before, int count) {
                hienThiGara(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        taiDanhSachGara();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.getUiSettings()
                .setZoomControlsEnabled(true);

        googleMap.getUiSettings()
                .setMapToolbarEnabled(true);

        googleMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                        VI_TRI_MAC_DINH, 11f
                )
        );

        hienThiGara(edtTimGara.getText().toString());
    }

    private void taiDanhSachGara() {
        db.collection("garages")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachGara.clear();

                    for (DocumentSnapshot doc : querySnapshot) {

                        Boolean hoatDong =
                                doc.getBoolean("hoatDong");

                        if (Boolean.FALSE.equals(hoatDong)) {
                            continue;
                        }

                        Double lat = doc.getDouble("latitude");
                        Double lng = doc.getDouble("longitude");

                        // Bỏ qua gara chưa có tọa độ hợp lệ
                        if (lat == null || lng == null
                                || lat < -90 || lat > 90
                                || lng < -180 || lng > 180) {
                            continue;
                        }

                        Gara gara = new Gara(
                                doc.getId(),
                                layChuoi(doc, "ten"),
                                layChuoi(doc, "diaChi"),
                                layChuoi(doc, "soDienThoai"),
                                layChuoi(doc, "gioMoCua"),
                                lat,
                                lng
                        );

                        danhSachGara.add(gara);
                    }

                    hienThiGara(
                            edtTimGara.getText().toString()
                    );
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(
                            this,
                            "Không tải được gara: " + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private String layChuoi(
            DocumentSnapshot doc, String tenTruong) {

        Object value = doc.get(tenTruong);
        return value == null ? "" : String.valueOf(value);
    }

    private void hienThiGara(String tuKhoa) {

        layoutDanhSachGara.removeAllViews();

        if (googleMap != null) {
            googleMap.clear();
        }

        String keyword = tuKhoa.trim().toLowerCase(
                Locale.ROOT
        );

        int soLuong = 0;
        LatLng viTriDauTien = null;

        for (Gara gara : danhSachGara) {

            String ten = gara.ten.toLowerCase(Locale.ROOT);
            String diaChi =
                    gara.diaChi.toLowerCase(Locale.ROOT);

            if (!ten.contains(keyword)
                    && !diaChi.contains(keyword)) {
                continue;
            }

            soLuong++;

            LatLng viTri = new LatLng(
                    gara.latitude,
                    gara.longitude
            );

            if (viTriDauTien == null) {
                viTriDauTien = viTri;
            }

            if (googleMap != null) {
                Marker marker = googleMap.addMarker(
                        new MarkerOptions()
                                .position(viTri)
                                .title(gara.ten)
                                .snippet(gara.diaChi)
                );
            }

            themTheGara(gara);
        }

        tvSoLuongGara.setText(
                "Gara AutoCare (" + soLuong + ")"
        );

        if (soLuong == 0) {
            TextView tvRong = new TextView(this);
            tvRong.setText(
                    danhSachGara.isEmpty()
                            ? "Chưa có dữ liệu gara."
                            : "Không tìm thấy gara phù hợp."
            );
            tvRong.setTextSize(14);
            tvRong.setPadding(8, 20, 8, 20);
            layoutDanhSachGara.addView(tvRong);
        }

        if (googleMap != null && viTriDauTien != null) {
            googleMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(
                            viTriDauTien, 14f
                    )
            );
        }
    }

    private void themTheGara(Gara gara) {

        View item = LayoutInflater.from(this)
                .inflate(
                        R.layout.item_gara,
                        layoutDanhSachGara,
                        false
                );

        TextView tvTen =
                item.findViewById(R.id.tvTenGara);
        TextView tvDiaChi =
                item.findViewById(R.id.tvDiaChiGara);
        TextView tvGio =
                item.findViewById(R.id.tvGioMoCuaGara);

        Button btnGoi =
                item.findViewById(R.id.btnGoiGara);
        Button btnChiDuong =
                item.findViewById(R.id.btnChiDuongGara);

        tvTen.setText(gara.ten);
        tvDiaChi.setText("📍 " + gara.diaChi);
        tvGio.setText("🕒 " + gara.gioMoCua);

        btnGoi.setOnClickListener(v -> {
            if (gara.soDienThoai.isEmpty()) {
                Toast.makeText(
                        this,
                        "0358143068",
                        Toast.LENGTH_SHORT
                ).show();
                return;
            }

            Intent intent = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.parse("tel:" + gara.soDienThoai)
            );
            startActivity(intent);
        });

        btnChiDuong.setOnClickListener(v ->
                moChiDuong(gara)
        );

        // Chạm vào thẻ sẽ di chuyển bản đồ tới gara
        item.setOnClickListener(v -> {
            if (googleMap != null) {
                LatLng viTri = new LatLng(
                        gara.latitude,
                        gara.longitude
                );

                googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                                viTri, 16f
                        )
                );
            }
        });

        layoutDanhSachGara.addView(item);
    }

    private void moChiDuong(Gara gara) {

        String toaDo = String.format(
                Locale.US,
                "%f,%f",
                gara.latitude,
                gara.longitude
        );

        Uri uri = Uri.parse(
                "https://www.google.com/maps/dir/?api=1"
                        + "&destination=" + toaDo
                        + "&travelmode=driving"
        );

        Intent intent = new Intent(
                Intent.ACTION_VIEW, uri
        );

        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Không thể mở bản đồ chỉ đường",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
}
