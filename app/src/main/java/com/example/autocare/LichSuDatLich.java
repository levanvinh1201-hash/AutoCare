package com.example.autocare;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class LichSuDatLich extends AppCompatActivity {

    RecyclerView recyclerLichSu;
    TextView txtSoLuongLich;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;

    ArrayList<LichDat> danhSachLich;
    LichDatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lich_su_dat_lich);

        // Ánh xạ giao diện
        recyclerLichSu = findViewById(R.id.recyclerLichSu);
        txtSoLuongLich = findViewById(R.id.txtSoLuongLich);

        // Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Tạo danh sách
        danhSachLich = new ArrayList<>();

        // Cài đặt RecyclerView
        recyclerLichSu.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Adapter
        adapter = new LichDatAdapter(
                this,
                danhSachLich
        );

        recyclerLichSu.setAdapter(adapter);

        // Tải lịch
        taiDanhSachLich();
    }

    private void taiDanhSachLich() {

        // Kiểm tra đăng nhập
        if (firebaseAuth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    "Vui lòng đăng nhập lại",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String maNguoiDung =
                firebaseAuth.getCurrentUser().getUid();

        // Lấy các lịch của người dùng hiện tại
        firestore.collection("appointments")
                .whereEqualTo("maNguoiDung", maNguoiDung)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    danhSachLich.clear();

                    for (com.google.firebase.firestore.DocumentSnapshot document
                            : querySnapshot.getDocuments()) {

                        String maLich =
                                document.getString("maLich");

                        String maXe =
                                document.getString("maXe");

                        String maDichVu =
                                document.getString("maDichVu");

                        String ngay =
                                document.getString("ngay");

                        String gio =
                                document.getString("gio");

                        String ghiChu =
                                document.getString("ghiChu");

                        String trangThai =
                                document.getString("trangThai");

                        LichDat lich =
                                new LichDat(
                                        maLich,
                                        maXe,
                                        maDichVu,
                                        ngay,
                                        gio,
                                        ghiChu,
                                        trangThai
                                );

                        danhSachLich.add(lich);
                    }

                    // Cập nhật số lượng
                    txtSoLuongLich.setText(
                            danhSachLich.size() + " lịch"
                    );

                    // Cập nhật RecyclerView
                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(
                            this,
                            "Không tải được lịch: "
                                    + e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();

                });
    }
}