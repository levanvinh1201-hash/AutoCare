
package com.example.autocare;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import androidx.recyclerview.widget.GridLayoutManager;

public class TatCaPhuTungActivity extends AppCompatActivity {

    private TextView btnQuayLai;
    private RecyclerView recyclerView;

    private PhuTungAdapter adapter;
    private final ArrayList<PhuTung> danhSachPhuTung =
            new ArrayList<>();

    private FirebaseFirestore firestore;

    // Phải dùng cùng collection với TrangChu.java
    private static final String TEN_COLLECTION = "spare_parts";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tatcaphutung);

        btnQuayLai = findViewById(R.id.btnQuayLai);
        recyclerView = findViewById(R.id.recyclerTatCaPhuTung);

        btnQuayLai.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(
                new GridLayoutManager(this, 2)
        );

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(10);

        recyclerView.setPadding(
                dpToPx(8),
                dpToPx(4),
                dpToPx(8),
                dpToPx(20)
        );

        recyclerView.setClipToPadding(false);

        recyclerView.setHasFixedSize(true);

        adapter = new PhuTungAdapter(
                TatCaPhuTungActivity.this,
                danhSachPhuTung
        );

        recyclerView.setAdapter(adapter);

        firestore = FirebaseFirestore.getInstance();

        taiDanhSachPhuTung();
    }

    private void taiDanhSachPhuTung() {
        firestore.collection(TEN_COLLECTION)
                .get()
                .addOnSuccessListener(this, this::hienThiDanhSach)
                .addOnFailureListener(this, e -> {
                    Toast.makeText(
                            TatCaPhuTungActivity.this,
                            "Không tải được phụ tùng: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    private void hienThiDanhSach(
            @NonNull QuerySnapshot querySnapshot
    ) {
        danhSachPhuTung.clear();

        for (QueryDocumentSnapshot document : querySnapshot) {

            PhuTung phuTung = new PhuTung();

            phuTung.setPhuTungId(document.getId());

            // Tên phụ tùng
            String ten = document.getString("ten");
            phuTung.setTen(ten != null ? ten : "");

            // Giá: Firestore có thể lưu dạng số hoặc chuỗi
            Object giaObject = document.get("gia");
            phuTung.setGia(
                    giaObject != null
                            ? String.valueOf(giaObject)
                            : "0"
            );

            // Tên ảnh trong drawable hoặc đường dẫn đang lưu
            String hinhAnh = document.getString("hinhAnh");
            phuTung.setHinhAnh(
                    hinhAnh != null ? hinhAnh : ""
            );

            // Mô tả
            String moTa = document.getString("moTa");
            phuTung.setMoTa(
                    moTa != null ? moTa : ""
            );

            // Số lượng: chấp nhận Number hoặc String
            Object soLuongObject = document.get("soLuong");
            int soLuong = 0;

            if (soLuongObject instanceof Number) {
                soLuong = ((Number) soLuongObject).intValue();
            } else if (soLuongObject instanceof String) {
                try {
                    soLuong = Integer.parseInt(
                            ((String) soLuongObject).trim()
                    );
                } catch (NumberFormatException ignored) {
                    soLuong = 0;
                }
            }

            phuTung.setSoLuong(soLuong);

            // Trạng thái còn hàng
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

            danhSachPhuTung.add(phuTung);
        }

        adapter.notifyDataSetChanged();

        if (danhSachPhuTung.isEmpty()) {
            Toast.makeText(
                    TatCaPhuTungActivity.this,
                    "Collection spare_parts chưa có dữ liệu",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }
    private int dpToPx(int dp) {
        return (int) (
                dp * getResources()
                        .getDisplayMetrics().density + 0.5f
        );
    }
}
